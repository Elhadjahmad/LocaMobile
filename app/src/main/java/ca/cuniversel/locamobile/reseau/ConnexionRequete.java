package ca.cuniversel.locamobile.reseau;

/** Exercice 4, étape 1 : le corps du POST api/auth/connexion. Gson le transforme en JSON. */
public class ConnexionRequete {
    public String courriel;
    public String motDePasse;

    public ConnexionRequete(String courriel, String motDePasse) {
        this.courriel = courriel;
        this.motDePasse = motDePasse;
    }
}
