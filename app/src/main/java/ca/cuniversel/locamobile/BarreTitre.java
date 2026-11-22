package ca.cuniversel.locamobile;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/** Remplace le titre standard de la barre du haut par « LOCAMOBILE » centré. */
public final class BarreTitre {

    private BarreTitre() {
        // pas d'instance
    }

    public static void appliquer(AppCompatActivity activite) {
        ActionBar barre = activite.getSupportActionBar();
        if (barre == null) {
            return;
        }
        View titre = activite.getLayoutInflater().inflate(R.layout.barre_titre, null);
        barre.setDisplayShowTitleEnabled(false);   // on cache le titre aligné à gauche
        barre.setDisplayShowCustomEnabled(true);   // on affiche notre propre vue
        barre.setCustomView(titre, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
    }
}
