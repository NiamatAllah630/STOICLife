package com.stoiclife.app.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.dao.PlanningDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Activite;
import com.stoiclife.app.model.PlanningJour;
import com.stoiclife.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPlanningActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvPlage;
    private TextView tvSubtitle;

    private LinearLayout llTypeContainer;
    private LinearLayout llActivityContainer;

    private TextView tvResumeTitle;
    private TextView tvResumeDetails;
    private MaterialButton btnAdd;

    private ActiviteDAO activiteDAO;
    private PlanningDAO planningDAO;

    private int userId;
    private String selectedDate;
    private String plage;

    private String typeSelectionne = "mentale";
    private String titreSelectionne = "";
    private String descSelectionnee = "";
    private int dureeSelectionnee = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planning);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        activiteDAO = new ActiviteDAO(db);
        planningDAO = new PlanningDAO(db);

        userId = new SessionManager(this).getUserId();

        plage = getIntent().getStringExtra("plage");
        selectedDate = getIntent().getStringExtra("date");

        if (plage == null || plage.trim().isEmpty()) {
            plage = "matin";
        }

        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        btnBack = findViewById(R.id.btn_back);
        tvPlage = findViewById(R.id.tv_plage_selected);
        tvSubtitle = findViewById(R.id.tv_subtitle);

        llTypeContainer = findViewById(R.id.ll_type_container);
        llActivityContainer = findViewById(R.id.ll_activity_container);

        tvResumeTitle = findViewById(R.id.tv_resume_title);
        tvResumeDetails = findViewById(R.id.tv_resume_details);
        btnAdd = findViewById(R.id.btn_add_activity);

        configurerHeader();
        afficherTypes();
        afficherActivites();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouterActivite();
            }
        });
    }

    private void configurerHeader() {
        if ("matin".equals(plage)) {
            tvPlage.setText("🌅 Matin");
            tvSubtitle.setText("Choisissez une activité douce et motivante pour bien commencer la journée.");
        } else if ("journee".equals(plage)) {
            tvPlage.setText("☀️ Journée");
            tvSubtitle.setText("Ajoutez une activité productive pour garder votre énergie et votre concentration.");
        } else {
            tvPlage.setText("🌙 Soir");
            tvSubtitle.setText("Préparez une activité calme pour terminer la journée avec sérénité.");
        }
    }

    private void afficherTypes() {
        llTypeContainer.removeAllViews();

        ajouterTypeCard(
                "mentale",
                "🧠 Mentale",
                "Réflexion, concentration et clarté."
        );

        ajouterTypeCard(
                "emotionnelle",
                "💛 Émotionnelle",
                "Calme, gratitude et équilibre intérieur."
        );

        ajouterTypeCard(
                "discipline",
                "🎯 Discipline",
                "Routine, effort et constance."
        );
    }

    private void ajouterTypeCard(final String typeValue, String titre, String description) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(13), dp(16), dp(13));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(params);

        card.setBackground(creerFondType(typeSelectionne.equals(typeValue)));

        TextView tvTitre = new TextView(this);
        tvTitre.setText(titre);
        tvTitre.setTextSize(16);
        tvTitre.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitre.setTextColor(getColor(R.color.navy_primary));

        TextView tvDescription = new TextView(this);
        tvDescription.setText(description);
        tvDescription.setTextSize(13);
        tvDescription.setTextColor(getColor(R.color.text_secondary));
        tvDescription.setPadding(0, dp(4), 0, 0);

        card.addView(tvTitre);
        card.addView(tvDescription);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeSelectionne = typeValue;
                titreSelectionne = "";
                descSelectionnee = "";
                dureeSelectionnee = 0;

                tvResumeTitle.setText("Aucune activité sélectionnée");
                tvResumeDetails.setText("Choisissez une activité pour voir le résumé.");

                afficherTypes();
                afficherActivites();
            }
        });

        llTypeContainer.addView(card);
    }

    private void afficherActivites() {
        llActivityContainer.removeAllViews();

        List<String[]> activites = activiteDAO.getCatalogueParPlageEtType(plage, typeSelectionne);

        if (activites == null || activites.isEmpty()) {
            TextView tvVide = new TextView(this);
            tvVide.setText("Aucune activité disponible pour cette catégorie.");
            tvVide.setTextSize(14);
            tvVide.setTextColor(getColor(R.color.text_secondary));
            tvVide.setPadding(dp(12), dp(12), dp(12), dp(12));
            llActivityContainer.addView(tvVide);
            return;
        }

        for (int i = 0; i < activites.size(); i++) {
            String[] item = activites.get(i);

            ajouterActiviteCard(
                    item[0],
                    item[1],
                    item[2]
            );
        }
    }

    private void ajouterActiviteCard(final String titre, final String description, final String duree) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(params);

        card.setBackground(creerFondActivite(titre.equals(titreSelectionne)));

        TextView tvTitre = new TextView(this);
        tvTitre.setText(titre);
        tvTitre.setTextSize(17);
        tvTitre.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitre.setTextColor(getColor(R.color.navy_primary));

        TextView tvMeta = new TextView(this);
        tvMeta.setText(getIconeType(typeSelectionne) + " " + getTypeLisible(typeSelectionne) + "  •  " + duree + " min");
        tvMeta.setTextSize(13);
        tvMeta.setTypeface(Typeface.DEFAULT_BOLD);
        tvMeta.setTextColor(getColor(R.color.gold_accent));
        tvMeta.setPadding(0, dp(6), 0, dp(5));

        TextView tvDesc = new TextView(this);
        tvDesc.setText(description);
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(getColor(R.color.text_secondary));
        tvDesc.setLineSpacing(dp(2), 1.0f);

        card.addView(tvTitre);
        card.addView(tvMeta);
        card.addView(tvDesc);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titreSelectionne = titre;
                descSelectionnee = description;

                try {
                    dureeSelectionnee = Integer.parseInt(duree);
                } catch (NumberFormatException e) {
                    dureeSelectionnee = 0;
                }

                tvResumeTitle.setText(titreSelectionne);
                tvResumeDetails.setText(
                        getTypeLisible(typeSelectionne)
                                + " • "
                                + dureeSelectionnee
                                + " min\n"
                                + descSelectionnee
                );

                afficherActivites();
            }
        });

        llActivityContainer.addView(card);
    }

    private void ajouterActivite() {
        if (titreSelectionne == null || titreSelectionne.trim().isEmpty()) {
            Toast.makeText(this, "Choisissez une activité", Toast.LENGTH_SHORT).show();
            return;
        }

        Activite activite = new Activite(
                titreSelectionne,
                typeSelectionne,
                descSelectionnee,
                dureeSelectionnee,
                selectedDate,
                userId
        );

        activite.setPlageHoraire(plage);
        activite.setStatut("planifiee");

        long id = activiteDAO.inserer(activite);

        if (id > 0) {
            ajouterTitreDansPlanningJour();
            Toast.makeText(this, "Activité ajoutée au planning ✅", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
        }
    }

    private void ajouterTitreDansPlanningJour() {
        PlanningJour planningJour = planningDAO.getByDate(userId, selectedDate);

        String matin = "";
        String journee = "";
        String soir = "";

        if (planningJour != null) {
            matin = planningJour.getResumeMatin();
            journee = planningJour.getResumeJournee();
            soir = planningJour.getResumeSoir();
        }

        if ("matin".equals(plage)) {
            matin = ajouterTitreAuResume(matin, titreSelectionne);
        } else if ("journee".equals(plage)) {
            journee = ajouterTitreAuResume(journee, titreSelectionne);
        } else {
            soir = ajouterTitreAuResume(soir, titreSelectionne);
        }

        PlanningJour nouveauPlanning = new PlanningJour(
                selectedDate,
                matin,
                journee,
                soir,
                userId
        );

        planningDAO.upsert(nouveauPlanning);
    }

    private String ajouterTitreAuResume(String ancienTexte, String titre) {
        if (ancienTexte == null || ancienTexte.trim().isEmpty()) {
            return titre;
        }

        return titre + "\n" + ancienTexte;
    }

    private String getIconeType(String type) {
        if ("mentale".equals(type)) {
            return "🧠";
        } else if ("emotionnelle".equals(type)) {
            return "💛";
        } else {
            return "🎯";
        }
    }

    private String getTypeLisible(String type) {
        if ("mentale".equals(type)) {
            return "Mentale";
        } else if ("emotionnelle".equals(type)) {
            return "Émotionnelle";
        } else {
            return "Discipline";
        }
    }

    private GradientDrawable creerFondType(boolean selected) {
        GradientDrawable bg = new GradientDrawable();

        if (selected) {
            bg.setColor(Color.parseColor("#FFF4CC"));
            bg.setStroke(dp(2), getColor(R.color.gold_accent));
        } else {
            bg.setColor(Color.WHITE);
            bg.setStroke(dp(1), getColor(R.color.border_light));
        }

        bg.setCornerRadius(dp(18));
        return bg;
    }

    private GradientDrawable creerFondActivite(boolean selected) {
        GradientDrawable bg = new GradientDrawable();

        if (selected) {
            bg.setColor(Color.parseColor("#FFF8DD"));
            bg.setStroke(dp(2), getColor(R.color.gold_accent));
        } else {
            bg.setColor(Color.WHITE);
            bg.setStroke(dp(1), getColor(R.color.border_light));
        }

        bg.setCornerRadius(dp(18));
        return bg;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}