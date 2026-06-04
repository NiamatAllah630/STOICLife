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

    private final String[][] matinMentales = {
            {"Citation du jour", "Lire et réfléchir à une citation motivante.", "5"},
            {"Lecture stoïcienne", "Lire un passage inspirant pour développer sa sagesse et sa réflexion.", "15"},
            {"Planification des objectifs", "Définir clairement ses objectifs à court ou long terme.", "10"},
            {"Visualisation mentale", "Imaginer ses objectifs et les actions pour les atteindre.", "10"}
    };

    private final String[][] matinEmotionnelles = {
            {"Respiration profonde", "Respirer lentement pour se calmer et réduire le stress.", "5"},
            {"Gratitude", "Noter des choses positives pour améliorer son état d’esprit.", "7"},
            {"Affirmations positives", "Répéter des phrases positives pour se motiver.", "5"},
            {"Visualisation positive", "Imaginer des situations positives pour renforcer la confiance.", "10"}
    };

    private final String[][] matinDiscipline = {
            {"Réveil tôt", "Se lever tôt pour bien commencer la journée.", "10"},
            {"Douche froide", "Prendre une douche froide pour renforcer la volonté.", "5"},
            {"Routine matinale", "Suivre une routine productive le matin.", "20"},
            {"Planification du jour", "Organiser les tâches de la journée.", "10"}
    };

    private final String[][] journeeMentales = {
            {"Apprendre un nouveau concept", "Découvrir une nouvelle notion pour progresser.", "20"},
            {"Résolution de problème", "Analyser un problème et chercher une solution efficace.", "15"},
            {"Brainstorming", "Générer rapidement plusieurs idées sans se limiter.", "10"},
            {"Apprentissage d’une langue", "Pratiquer une langue étrangère pour s’améliorer.", "20"},
            {"Lecture d’article scientifique", "Lire un article pour enrichir ses connaissances.", "15"},
            {"Focus session", "Se concentrer intensément sur une tâche sans distraction.", "25"}
    };

    private final String[][] journeeEmotionnelles = {
            {"Marche consciente", "Marcher en étant attentif à ses sensations et à l’environnement.", "15"},
            {"Pause sans écran", "Faire une pause loin des écrans pour se reposer.", "10"},
            {"Observer la nature", "Prendre un moment pour observer et se reconnecter à la nature.", "10"},
            {"Contenu inspirant", "Regarder ou écouter un contenu motivant.", "10"}
    };

    private final String[][] journeeDiscipline = {
            {"Organisation bureau", "Mettre de l’ordre dans son espace de travail.", "10"},
            {"Sport léger", "Faire une activité physique douce pour rester actif.", "20"},
            {"Exercice intense", "Faire du sport plus intense pour améliorer sa condition physique.", "30"},
            {"Deep Work", "Travailler profondément sans distraction.", "30"},
            {"Étude concentrée", "Étudier avec concentration pendant un temps défini.", "25"},
            {"Révision cours", "Revoir les leçons pour mieux les mémoriser.", "20"},
            {"Gestion des tâches", "Organiser et prioriser ses tâches.", "10"},
            {"Limiter réseaux sociaux", "Réduire l’utilisation des réseaux sociaux.", "15"}
    };

    private final String[][] soirMentales = {
            {"Réflexion personnelle", "Prendre du recul pour analyser ses pensées et ses actions.", "10"},
            {"Écriture d’idées", "Noter ses idées pour mieux les organiser.", "10"},
            {"Résumé d’un livre", "Synthétiser les idées principales d’un livre.", "15"},
            {"Analyse d’une journée passée", "Faire le bilan de sa journée pour s’améliorer.", "10"}
    };

    private final String[][] soirEmotionnelles = {
            {"Journal émotionnel", "Écrire ses émotions pour mieux les comprendre.", "10"},
            {"Méditation calme", "Se poser en silence pour apaiser son esprit.", "15"},
            {"Écouter musique relaxante", "Écouter une musique douce pour se détendre.", "10"},
            {"Relaxation guidée", "Suivre une séance pour relâcher les tensions.", "10"},
            {"Écriture libre", "Écrire librement ce que l’on ressent sans réfléchir.", "10"},
            {"Déconnexion mentale", "Se libérer des pensées stressantes et se détendre.", "15"}
    };

    private final String[][] soirDiscipline = {
            {"Nettoyage chambre", "Ranger et nettoyer son espace personnel.", "15"},
            {"Préparation du lendemain", "Préparer les affaires et les objectifs du jour suivant.", "10"},
            {"Routine soir", "Mettre en place une routine calme avant de dormir.", "15"}
    };

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

        String[][] source = getActivitesSelonPlageEtType(plage, typeSelectionne);

        for (int i = 0; i < source.length; i++) {
            ajouterActiviteCard(source[i][0], source[i][1], source[i][2]);
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
                tvResumeDetails.setText(getTypeLisible(typeSelectionne) + " • " + dureeSelectionnee + " min\n" + descSelectionnee);

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

    private String[][] getActivitesSelonPlageEtType(String plageValue, String typeValue) {
        if ("matin".equals(plageValue)) {
            if ("mentale".equals(typeValue)) return matinMentales;
            if ("emotionnelle".equals(typeValue)) return matinEmotionnelles;
            return matinDiscipline;
        }

        if ("journee".equals(plageValue)) {
            if ("mentale".equals(typeValue)) return journeeMentales;
            if ("emotionnelle".equals(typeValue)) return journeeEmotionnelles;
            return journeeDiscipline;
        }

        if ("mentale".equals(typeValue)) return soirMentales;
        if ("emotionnelle".equals(typeValue)) return soirEmotionnelles;
        return soirDiscipline;
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