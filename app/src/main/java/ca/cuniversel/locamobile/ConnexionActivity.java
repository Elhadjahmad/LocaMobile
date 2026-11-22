package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ConnexionRequete;
import ca.cuniversel.locamobile.reseau.Profil;
import ca.cuniversel.locamobile.reseau.TokenReponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Exercice 4 : connexion en deux temps (POST connexion -> jeton, puis GET api/moi avec le jeton). */
public class ConnexionActivity extends AppCompatActivity {

    private EditText champCourriel;
    private EditText champMotDePasse;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        BarreTitre.appliquer(this);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        resultat = findViewById(R.id.resultat);

        findViewById(R.id.boutonSeConnecter).setOnClickListener(v -> connecter());
    }

    /** Exercice 4, étape 5 : temps 1, on envoie courriel + mot de passe une seule fois. */
    private void connecter() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();

        if (courriel.isEmpty()) {
            champCourriel.setError("Courriel obligatoire");
            return;
        }
        if (motDePasse.isEmpty()) {
            champMotDePasse.setError("Mot de passe obligatoire");
            return;
        }

        resultat.setText("Connexion…");
        ClientApi.get().connecter(new ConnexionRequete(courriel, motDePasse))
                .enqueue(new Callback<TokenReponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenReponse> call,
                                           @NonNull Response<TokenReponse> reponse) {
                        Log.d(ListeActivity.TAG, "connexion -> code " + reponse.code());
                        TokenReponse jeton = reponse.body();
                        if (reponse.isSuccessful() && jeton != null) {
                            // On garde le jeton et le nom, jamais le mot de passe
                            Session.enregistrer(ConnexionActivity.this, jeton.token, jeton.nom);
                            // Temps 2 : le jeton existe enfin, on peut appeler api/moi ICI
                            chargerProfil(jeton.token);
                        } else {
                            // Exercice 4, étape 7 : le message du serveur est dans errorBody()
                            resultat.setText("Erreur " + reponse.code() + " : " + messageServeur(reponse));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenReponse> call, @NonNull Throwable t) {
                        resultat.setText("Serveur injoignable : " + t.getMessage());
                    }
                });
    }

    /** Exercice 4, étapes 5 et 6 : temps 2, on présente le jeton dans l'en-tête Authorization. */
    private void chargerProfil(String jeton) {
        String entete = "Bearer " + jeton;   // l'espace après Bearer est obligatoire
        ClientApi.get().monProfil(entete).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(@NonNull Call<Profil> call, @NonNull Response<Profil> reponse) {
                Log.d(ListeActivity.TAG, "api/moi -> code " + reponse.code());
                Profil p = reponse.body();
                if (reponse.isSuccessful() && p != null) {
                    resultat.setText("Connecté !\n"
                            + "Nom : " + p.nom + "\n"
                            + "Rôle : " + p.role + "\n"
                            + "Courriel : " + p.courriel + "\n"
                            + "Code HTTP (api/moi) : " + reponse.code());
                } else {
                    resultat.setText("Erreur " + reponse.code() + " sur api/moi : " + messageServeur(reponse));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profil> call, @NonNull Throwable t) {
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    /** Lit le champ "message" du JSON d'erreur, sans écrire de classe (JsonObject). */
    private String messageServeur(Response<?> reponse) {
        try {
            if (reponse.errorBody() == null) {
                return "(aucun message)";
            }
            String texte = reponse.errorBody().string();
            JsonObject json = new Gson().fromJson(texte, JsonObject.class);
            if (json != null && json.has("message")) {
                return json.get("message").getAsString();
            }
            return texte.isEmpty() ? "(aucun message)" : texte;
        } catch (Exception e) {
            return "(message illisible)";
        }
    }
}
