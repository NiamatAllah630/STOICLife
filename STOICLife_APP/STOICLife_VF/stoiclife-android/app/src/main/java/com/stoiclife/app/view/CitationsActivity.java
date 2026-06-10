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
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.database.DatabaseHelper;

import java.util.List;

public class CitationsActivity extends AppCompatActivity {

    private MaterialButton btnTous, btnMarc, btnEpictete, btnSeneque;
    private LinearLayout llCitations;

    private ActiviteDAO activiteDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citations);

        activiteDAO = new ActiviteDAO(DatabaseHelper.getInstance(this));

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

        List<String[]> citations = activiteDAO.getToutesCitations();

        if (citations == null || citations.isEmpty()) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Aucune citation disponible.");
            tvVide.setTextSize(14);
            tvVide.setTextColor(getResources().getColor(R.color.text_secondary));
            tvVide.setPadding(dp(12), dp(12), dp(12), dp(12));
            llCitations.addView(tvVide);
            return;
        }

        for (int i = 0; i < citations.size(); i++) {
            final String auteur = citations.get(i)[0];
            final String texte = citations.get(i)[1];

            if (!"Tous".equals(philosophe) && !auteur.equals(philosophe)) {
                continue;
            }

            View item = LayoutInflater.from(this).inflate(
                    R.layout.item_citation,
                    llCitations,
                    false
            );

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