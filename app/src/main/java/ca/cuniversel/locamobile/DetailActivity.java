package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Exercice 3 : la fiche reçoit seulement l'id et demande les détails au serveur. */
public class DetailActivity extends AppCompatActivity {

    public static final String CLE_ID = "id";

    private TextView etat;
    private View carteDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        BarreTitre.appliquer(this);

        etat = findViewById(R.id.etat);
        carteDetail = findViewById(R.id.carteDetail);
        TextView detailTitre = findViewById(R.id.detailTitre);
        TextView detailVille = findViewById(R.id.detailVille);
        TextView detailPrix = findViewById(R.id.detailPrix);
        TextView detailVoyageurs = findViewById(R.id.detailVoyageurs);
        TextView detailProprietaire = findViewById(R.id.detailProprietaire);

        // Exercice 3, étape 4 : on lit seulement l'id
        int id = getIntent().getIntExtra(CLE_ID, -1);
        // Exercice 3, étape 6 (test du 404) : décommenter, observer, puis remettre en commentaire
        // id = 999;

        ClientApi.get().trouverLogement(id).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(@NonNull Call<Logement> call, @NonNull Response<Logement> reponse) {
                Log.d(ListeActivity.TAG, "URL : " + call.request().url() + " -> code " + reponse.code());
                Logement l = reponse.body();
                if (reponse.isSuccessful() && l != null) {
                    etat.setVisibility(View.GONE);
                    carteDetail.setVisibility(View.VISIBLE);
                    detailTitre.setText(l.titre);
                    detailVille.setText("Ville : " + l.ville);
                    detailPrix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
                    detailVoyageurs.setText("Voyageurs : " + l.nbVoyageurs);
                    detailProprietaire.setText("Propriétaire : " + l.proprietaireNom);
                } else if (reponse.code() == 404) {
                    // Un 404 n'est pas une panne : le serveur a bien répondu « introuvable »
                    etat.setText("Ce logement n'existe plus (404).");
                } else {
                    etat.setText("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Logement> call, @NonNull Throwable t) {
                Log.e(ListeActivity.TAG, "onFailure : " + t.getMessage());
                etat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}
