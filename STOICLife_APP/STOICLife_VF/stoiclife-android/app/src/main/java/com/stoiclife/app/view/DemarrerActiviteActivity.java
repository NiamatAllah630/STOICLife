package com.stoiclife.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Activite;

public class DemarrerActiviteActivity extends AppCompatActivity {

    private TextView tvBack, tvIcon, tvTitre, tvDescription;
    private TextView tvDuree, tvType, tvPlage, tvConseil, tvImpact;
    private TextView tvAnnuler;
    private MaterialButton btnCommencer;

    private int id;
    private String titre, type, description, plage;
    private int duree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demarrer_activite);

        tvBack = findViewById(R.id.tv_back);
        tvIcon = findViewById(R.id.tv_icon);
        tvTitre = findViewById(R.id.tv_titre);
        tvDescription = findViewById(R.id.tv_description);
        tvDuree = findViewById(R.id.tv_duree);
        tvType = findViewById(R.id.tv_type);
        tvPlage = findViewById(R.id.tv_plage);
        tvConseil = findViewById(R.id.tv_conseil);
        tvImpact = findViewById(R.id.tv_impact);
        tvAnnuler = findViewById(R.id.tv_annuler);
        btnCommencer = findViewById(R.id.btn_commencer);

        id = getIntent().getIntExtra("id", -1);

        titre = getIntent().getStringExtra("titre");
        type = getIntent().getStringExtra("type");
        description = getIntent().getStringExtra("description");
        duree = getIntent().getIntExtra("duree", -1);
        plage = getIntent().getStringExtra("plage");

        if (id != -1 && informationsManquantes()) {
            ActiviteDAO dao = new ActiviteDAO(DatabaseHelper.getInstance(this));
            Activite activite = dao.getById(id);

            if (activite != null) {
                titre = activite.getTitre();
                type = activite.getTypeActivite();
                description = activite.getDescription();
                duree = activite.getDureeMinutes();
                plage = activite.getPlageHoraire();
            }
        }

        appliquerValeursParDefaut();
        afficherInfos();

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirSession();
            }
        });
    }

    private boolean informationsManquantes() {
        return titre == null
                || type == null
                || description == null
                || duree == -1
                || plage == null;
    }

    private void appliquerValeursParDefaut() {
        if (titre == null || titre.trim().isEmpty()) {
            titre = "Activité";
        }

        if (type == null || type.trim().isEmpty()) {
            type = "mentale";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "Prends un moment pour toi et avance avec calme.";
        }

        if (duree <= 0) {
            duree = 10;
        }

        if (plage == null || plage.trim().isEmpty()) {
            plage = "aujourd’hui";
        }
    }

    private void afficherInfos() {
        tvTitre.setText(titre);
        tvDescription.setText(description);

        tvDuree.setText("Durée estimée : " + duree + " min");
        tvType.setText("Type : " + getTypeLisible(type));
        tvPlage.setText("Moment conseillé : " + getPlageLisible(plage));

        tvConseil.setText(getConseilByActivity(titre));
        tvImpact.setText(getImpactByActivity(titre));

        if ("mentale".equals(type)) {
            tvIcon.setText("🧠");
        } else if ("emotionnelle".equals(type)) {
            tvIcon.setText("💛");
        } else {
            tvIcon.setText("🎯");
        }
    }

    private void ouvrirSession() {
        Intent intent = new Intent(this, SessionActivity.class);

        intent.putExtra("id", id);
        intent.putExtra("titre", titre);
        intent.putExtra("type", type);
        intent.putExtra("description", description);
        intent.putExtra("duree", duree);
        intent.putExtra("plage", plage);

        startActivity(intent);
        finish();
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

    private String getPlageLisible(String plage) {
        if ("matin".equals(plage)) {
            return "Matin";
        } else if ("journee".equals(plage)) {
            return "Journée";
        } else if ("soir".equals(plage)) {
            return "Soir";
        } else {
            return plage;
        }
    }

    private String getConseilByActivity(String titre) {
        String t = titre.toLowerCase();

        if (t.contains("gratitude")) {
            return "Apprécie les petites choses de ta journée. La sérénité commence par ce que tu remarques.";
        }

        if (t.contains("affirmation")) {
            return "Répète tes phrases positives avec calme et confiance.";
        }

        if (t.contains("marche")) {
            return "Marche lentement, observe chaque pas et reviens au moment présent.";
        }

        if (t.contains("lecture")) {
            return "Lis calmement et garde une seule idée importante à appliquer aujourd’hui.";
        }

        if (t.contains("respiration")) {
            return "Respire lentement. Relâche les tensions et reprends le contrôle de ton attention.";
        }

        if (t.contains("méditation")) {
            return "Observe tes pensées sans les juger. Laisse-les passer simplement.";
        }

        if (t.contains("focus") || t.contains("deep work")) {
            return "Concentre-toi sur une seule tâche. La discipline commence par l’attention.";
        }

        if (t.contains("sport") || t.contains("exercice")) {
            return "Commence maintenant, même doucement. L’action crée l’énergie.";
        }

        if (t.contains("douche")) {
            return "Accepte l’inconfort quelques instants pour renforcer ta volonté.";
        }

        return "Commence simplement. Une petite action bien faite vaut mieux qu’une grande intention reportée.";
    }

    private String getImpactByActivity(String titre) {
        String t = titre.toLowerCase();

        if (t.contains("gratitude") || t.contains("affirmation")) {
            return "Améliore l’humeur, la confiance et l’état d’esprit positif.";
        }

        if (t.contains("respiration") || t.contains("méditation") || t.contains("relaxation")) {
            return "Réduit le stress, calme l’esprit et favorise la stabilité intérieure.";
        }

        if (t.contains("lecture") || t.contains("apprendre") || t.contains("résumé")) {
            return "Développe la réflexion, la concentration et la sagesse.";
        }

        if (t.contains("sport") || t.contains("exercice") || t.contains("douche") || t.contains("routine")) {
            return "Renforce la discipline, l’énergie et la constance.";
        }

        if (t.contains("focus") || t.contains("deep work") || t.contains("étude")) {
            return "Améliore la productivité, l’attention et la maîtrise de soi.";
        }

        return "Aide à progresser, rester concentré et améliorer ton équilibre quotidien.";
    }
}