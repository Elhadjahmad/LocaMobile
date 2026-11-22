package ca.cuniversel.locamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Exercice 1, étape 6 : le client.
 * Singleton : Retrofit est construit UNE seule fois, puis réutilisé partout.
 * 10.0.2.2 = l'ordinateur hôte vu depuis l'émulateur Android.
 */
public final class ClientApi {

    private static final String URL_BASE = "http://10.0.2.2:8080/";

    private static LocaMobileApi api;

    private ClientApi() {
        // pas d'instance
    }

    public static synchronized LocaMobileApi get() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(LocaMobileApi.class);
        }
        return api;
    }
}
