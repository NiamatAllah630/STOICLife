package com.stoiclife.app.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;

public class CitationsActivity extends AppCompatActivity {

    private MaterialButton btnTous, btnMarc, btnEpictete, btnSeneque;
    private LinearLayout llCitations;

    private static final String[][] CITATIONS = {
            {"Marc Aurèle",  "Tu as du pouvoir sur ton esprit, pas sur les événements extérieurs."},
            {"Marc Aurèle",  "Les obstacles à l'action font avancer l'action. Ce qui bloque devient le chemin."},
            {"Marc Aurèle",  "L'âme est teinte de la couleur de ses pensées."},
            {"Marc Aurèle",  "Le bonheur de ta vie dépend de la qualité de tes pensées."},
            {"Marc Aurèle",  "Confine-toi au présent."},
            {"Marc Aurèle",  "Agis comme si chaque acte était le dernier de ta vie."},
            {"Marc Aurèle",  "Ne gâche pas le temps qui reste à ruminer sur les autres."},

            {"Épictète",     "Ce n'est pas ce qui arrive qui trouble, mais les opinions qu'on en a."},
            {"Épictète",     "Il y a deux choses : ce qui dépend de nous et ce qui n'en dépend pas."},
            {"Épictète",     "Nous souffrons davantage en imagination que dans la réalité."},
            {"Épictète",     "La liberté naît en éliminant le désir, non en le satisfaisant."},
            {"Épictète",     "Aspire seulement aux choses qui sont en ton pouvoir."},
            {"Épictète",     "La difficulté révèle ce qu'un homme vaut vraiment."},
            {"Épictète",     "Toute grande œuvre a d'abord semblé impossible."},

            {"Sénèque",      "Seul le temps est vraiment à nous."},
            {"Sénèque",      "Pendant que tu remets à plus tard, la vie passe."},
            {"Sénèque",      "Le bonheur n'est pas de posséder beaucoup, mais d'en désirer peu."},
            {"Sénèque",      "Ce n'est pas parce que les choses sont difficiles que nous n'osons pas."},
            {"Sénèque",      "Commence. Qui commence a la moitié de la chose faite."},
            {"Sénèque",      "La chance, c'est quand la préparation rencontre l'opportunité."}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citations);

        initialiserVues();
        configurerActions();

        selectionnerFiltre(btnTous);
        afficherCitations("Tous");
    }

    private void initialiserVues() {
        llCitations = findViewById(R.id.ll_citations);

        TextView btnRetour = findViewById(R.id.btn_retour);
        btnTous = findViewById(R.id.btn_filtre_tous);
        btnMarc = findViewById(R.id.btn_filtre_marc);
        btnEpictete = findViewById(R.id.btn_filtre_epictete);
        btnSeneque = findViewById(R.id.btn_filtre_seneque);

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void configurerActions() {
        btnTous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionnerFiltre(btnTous);
                afficherCitations("Tous");
            }
        });

        btnMarc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionnerFiltre(btnMarc);
                afficherCitations("Marc Aurèle");
            }
        });

        btnEpictete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionnerFiltre(btnEpictete);
                afficherCitations("Épictète");
            }
        });

        btnSeneque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionnerFiltre(btnSeneque);
                afficherCitations("Sénèque");
            }
        });
    }

    private void selectionnerFiltre(MaterialButton filtreActif) {
        int gold = getResources().getColor(R.color.gold_accent);
        int beige = getResources().getColor(R.color.beige_card);
        int navy = getResources().getColor(R.color.navy_primary);
        int border = getResources().getColor(R.color.border_light);

        appliquerFiltreInactif(btnTous, beige, navy, border);
        appliquerFiltreInactif(btnMarc, beige, navy, border);
        appliquerFiltreInactif(btnEpictete, beige, navy, border);
        appliquerFiltreInactif(btnSeneque, beige, navy, border);

        filtreActif.setBackgroundTintList(ColorStateList.valueOf(gold));
        filtreActif.setTextColor(navy);
        filtreActif.setStrokeWidth(0);
        filtreActif.setAlpha(1f);
    }

    private void appliquerFiltreInactif(MaterialButton bouton, int beige, int navy, int border) {
        bouton.setBackgroundTintList(ColorStateList.valueOf(beige));
        bouton.setTextColor(navy);
        bouton.setStrokeColor(ColorStateList.valueOf(border));
        bouton.setStrokeWidth(dp(1));
        bouton.setAlpha(1f);
    }

    private void afficherCitations(String philosophe) {
        llCitations.removeAllViews();

        for (int i = 0; i < CITATIONS.length; i++) {
            final String auteur = CITATIONS[i][0];
            final String texte = CITATIONS[i][1];

            if (!"Tous".equals(philosophe) && !auteur.equals(philosophe)) {
                continue;
            }

            View item = LayoutInflater.from(this).inflate(R.layout.item_citation, llCitations, false);

            TextView tvAuteur = item.findViewById(R.id.tv_citation_auteur);
            TextView tvTexte = item.findViewById(R.id.tv_citation_texte);
            TextView btnPartager = item.findViewById(R.id.btn_partager_citation);

            tvAuteur.setText("— " + auteur);
            tvTexte.setText("« " + texte + " »");

            btnPartager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    partagerCitation(auteur, texte);
                }
            });

            llCitations.addView(item);
        }
    }

    private void partagerCitation(String auteur, String texte) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(
                Intent.EXTRA_TEXT,
                "« " + texte + " »\n— " + auteur + "\n\nSTOICLife"
        );

        startActivity(Intent.createChooser(share, "Partager la citation"));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}