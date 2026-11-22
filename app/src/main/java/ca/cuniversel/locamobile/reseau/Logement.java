package ca.cuniversel.locamobile.reseau;

/**
 * Exercice 1, étape 4 : le « moule » du JSON.
 * Un attribut public par clé du JSON, avec EXACTEMENT le même nom :
 * Gson associe "prixParNuit" du JSON à l'attribut prixParNuit.
 */
public class Logement {
    public int id;
    public String titre;
    public String ville;
    public double prixParNuit;
    public int nbVoyageurs;
    public boolean disponible;
    public String proprietaireNom;
}
