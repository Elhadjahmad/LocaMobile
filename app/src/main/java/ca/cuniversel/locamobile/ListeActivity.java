package ca.cuniversel.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    public static final String TAG = "LocaMobile";

    private LinearLayout conteneurLogements;
    private TextView messageErreur;
    private EditText champVille;
    private TextView etatSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);
        BarreTitre.appliquer(this);

        conteneurLogements = findViewById(R.id.conteneurLogements);
        messageErreur = findViewById(R.id.messageErreur);
        champVille = findViewById(R.id.champVille);

        // Exercice 3, étape 8 : le bouton relance le chargement avec la ville saisie
        findViewById(R.id.boutonChercher).setOnClickListener(v -> chargerLogements());

        // Exercice 4, étape 4 : le bouton Connexion ouvre l'écran de connexion
        etatSession = findViewById(R.id.etatSession);
        findViewById(R.id.boutonConnexion).setOnClickListener(v ->
                startActivity(new Intent(this, ConnexionActivity.class)));
    }

    // Exercice 2, étape 7 : l'appel est dans onResume -> la liste se recharge à chaque retour sur l'écran
    @Override
    protected void onResume() {
        super.onResume();
        String nom = Session.nom(this);
        etatSession.setText(nom == null ? "Non connecté" : "Connecté : " + nom);
        chargerLogements();
    }

    /** Exercice 3, étape 8 : champ vide -> liste complète ; sinon -> recherche par ville. */
    private void chargerLogements() {
        String ville = champVille.getText().toString().trim();
        Call<List<Logement>> appel = ville.isEmpty()
                ? ClientApi.get().listerLogements()
                : ClientApi.get().chercherParVille(ville);

        Log.d(TAG, "1 - avant enqueue");
        appel.enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(@NonNull Call<List<Logement>> call,
                                   @NonNull Response<List<Logement>> reponse) {
                Log.d(TAG, "3 - début de onResponse");
                Log.d(TAG, "URL : " + call.request().url() + " -> code " + reponse.code());

                // Exercice 2, étape 6 : onResponse ne veut pas dire « succès ».
                // Un 404 ou un 500 arrivent aussi ici, avec body() à null.
                List<Logement> logements = reponse.body();
                if (reponse.isSuccessful() && logements != null) {
                    Log.d(TAG, "code " + reponse.code() + " : " + logements.size() + " logements reçus");
                    if (logements.isEmpty() && !ville.isEmpty()) {
                        // Une liste vide n'est pas une erreur pour le serveur (code 200)
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(logements);
                    }
                } else {
                    Log.d(TAG, "code " + reponse.code() + " (échec HTTP)");
                    afficherErreur("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Logement>> call, @NonNull Throwable t) {
                // Aucune réponse HTTP : réseau refusé, adresse injoignable, serveur arrêté...
                Log.e(TAG, "onFailure : " + t.getMessage());
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
        Log.d(TAG, "2 - juste après enqueue");
    }

    /** Exercice 2, étape 3 : une carte gonflée, remplie puis ajoutée pour chaque logement reçu. */
    private void afficher(List<Logement> logements) {
        conteneurLogements.removeAllViews();
        messageErreur.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneurLogements, false);

            TextView titre = carte.findViewById(R.id.carteTitre);
            TextView ville = carte.findViewById(R.id.carteVille);
            TextView prix = carte.findViewById(R.id.cartePrix);

            titre.setText(l.titre);
            ville.setText(l.ville);
            // Exercice 2, étape 4 : format « 145,50 $ / nuit »
            prix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            // Exercice 3, étape 3 : la carte ouvre la fiche avec UN SEUL extra, l'id
            final int id = l.id;
            carte.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, id);
                startActivity(intent);
            });

            conteneurLogements.addView(carte);
        }
    }

    /** Exercice 2, étape 5 : on vide la liste et on montre le message. */
    private void afficherErreur(String message) {
        conteneurLogements.removeAllViews();
        messageErreur.setText(message);
        messageErreur.setVisibility(View.VISIBLE);
    }
}
