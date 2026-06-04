package com.stoiclife.app.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Activite;

public class EditActiviteActivity extends AppCompatActivity {

    private EditText etTitre, etDescription, etDuree;
    private Spinner spType, spActivite;
    private Button btnSave;
    private TextView btnBack;

    private ActiviteDAO activiteDAO;
    private int activiteId;

    private boolean premiereSelectionActivite = true;

    private final String[][] activitesMentales = {
            {"Lecture stoïcienne", "Lire un passage inspirant pour développer sa sagesse et sa réflexion.", "15"},
            {"Citation du jour", "Lire et réfléchir à une citation motivante.", "5"},
            {"Réflexion personnelle", "Prendre du recul pour analyser ses pensées et ses actions.", "10"},
            {"Écriture d’idées", "Noter ses idées pour mieux les organiser.", "10"},
            {"Apprendre un nouveau concept", "Découvrir une nouvelle notion pour progresser.", "20"},
            {"Planification des objectifs", "Définir clairement ses objectifs à court ou long terme.", "10"},
            {"Résolution de problème", "Analyser un problème et chercher une solution efficace.", "15"},
            {"Brainstorming", "Générer rapidement plusieurs idées sans se limiter.", "10"},
            {"Apprentissage d’une langue", "Pratiquer une langue étrangère pour s’améliorer.", "20"},
            {"Lecture d’article scientifique", "Lire un article pour enrichir ses connaissances.", "15"},
            {"Visualisation mentale", "Imaginer ses objectifs et les actions pour les atteindre.", "10"},
            {"Résumé d’un livre", "Synthétiser les idées principales d’un livre.", "15"},
            {"Analyse d’une journée passée", "Faire le bilan de sa journée pour s’améliorer.", "10"},
            {"Focus session", "Se concentrer intensément sur une tâche sans distraction.", "25"}
    };

    private final String[][] activitesEmotionnelles = {
            {"Respiration profonde", "Respirer lentement pour se calmer et réduire le stress.", "5"},
            {"Journal émotionnel", "Écrire ses émotions pour mieux les comprendre.", "10"},
            {"Méditation calme", "Se poser en silence pour apaiser son esprit.", "15"},
            {"Gratitude", "Noter des choses positives pour améliorer son état d’esprit.", "7"},
            {"Écouter musique relaxante", "Écouter une musique douce pour se détendre.", "10"},
            {"Marche consciente", "Marcher en étant attentif à ses sensations et à l’environnement.", "15"},
            {"Relaxation guidée", "Suivre une séance pour relâcher les tensions.", "10"},
            {"Écriture libre", "Écrire librement ce que l’on ressent sans réfléchir.", "10"},
            {"Pause sans écran", "Faire une pause loin des écrans pour se reposer.", "10"},
            {"Observer la nature", "Prendre un moment pour observer et se reconnecter à la nature.", "10"},
            {"Affirmations positives", "Répéter des phrases positives pour se motiver.", "5"},
            {"Visualisation positive", "Imaginer des situations positives pour renforcer la confiance.", "10"},
            {"Déconnexion mentale", "Se libérer des pensées stressantes et se détendre.", "15"},
            {"Contenu inspirant", "Regarder ou écouter un contenu motivant.", "10"}
    };

    private final String[][] activitesDiscipline = {
            {"Réveil tôt", "Se lever tôt pour bien commencer la journée.", "10"},
            {"Douche froide", "Prendre une douche froide pour renforcer la volonté.", "5"},
            {"Planification du jour", "Organiser les tâches de la journée.", "10"},
            {"Organisation bureau", "Mettre de l’ordre dans son espace de travail.", "10"},
            {"Nettoyage chambre", "Ranger et nettoyer son espace personnel.", "15"},
            {"Sport léger", "Faire une activité physique douce pour rester actif.", "20"},
            {"Exercice intense", "Faire du sport plus intense pour améliorer sa condition physique.", "30"},
            {"Deep Work", "Travailler profondément sans distraction.", "30"},
            {"Étude concentrée", "Étudier avec concentration pendant un temps défini.", "25"},
            {"Révision cours", "Revoir les leçons pour mieux les mémoriser.", "20"},
            {"Gestion des tâches", "Organiser et prioriser ses tâches.", "10"},
            {"Préparation du lendemain", "Préparer les affaires et les objectifs du jour suivant.", "10"},
            {"Limiter réseaux sociaux", "Réduire l’utilisation des réseaux sociaux.", "15"},
            {"Routine matinale", "Suivre une routine productive le matin.", "20"},
            {"Routine soir", "Mettre en place une routine calme avant de dormir.", "15"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activite);

        activiteDAO = new ActiviteDAO(DatabaseHelper.getInstance(this));

        initialiserVues();

        activiteId = getIntent().getIntExtra("id", -1);
        String typeActuel = getIntent().getStringExtra("type");

        initialiserDropdowns(typeActuel);
        initialiserActions();
    }

    private void initialiserVues() {
        spType = findViewById(R.id.sp_edit_type);
        spActivite = findViewById(R.id.sp_edit_activite);
        etTitre = findViewById(R.id.et_edit_titre);
        etDescription = findViewById(R.id.et_edit_description);
        etDuree = findViewById(R.id.et_edit_duree);
        btnSave = findViewById(R.id.btn_save_edit);
        btnBack = findViewById(R.id.btn_back_edit);
    }

    private void initialiserActions() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifierActivite();
            }
        });
    }

    private void initialiserDropdowns(String typeActuel) {
        String[] types = {
                "🧠 Mentale",
                "💛 Émotionnelle",
                "🎯 Discipline"
        };

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        if ("emotionnelle".equals(typeActuel)) {
            spType.setSelection(1);
            remplirActivites("emotionnelle");
        } else if ("discipline".equals(typeActuel)) {
            spType.setSelection(2);
            remplirActivites("discipline");
        } else {
            spType.setSelection(0);
            remplirActivites("mentale");
        }

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                premiereSelectionActivite = true;

                if (position == 0) {
                    remplirActivites("mentale");
                } else if (position == 1) {
                    remplirActivites("emotionnelle");
                } else {
                    remplirActivites("discipline");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void remplirActivites(String type) {
        final String[][] data;

        if ("mentale".equals(type)) {
            data = activitesMentales;
        } else if ("emotionnelle".equals(type)) {
            data = activitesEmotionnelles;
        } else {
            data = activitesDiscipline;
        }

        String[] titres = new String[data.length];

        for (int i = 0; i < data.length; i++) {
            titres[i] = data[i][0];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                titres
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivite.setAdapter(adapter);

        spActivite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etTitre.setText(data[position][0]);
                etDescription.setText(data[position][1]);
                etDuree.setText(data[position][2]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectionnerActiviteActuelle(data);
    }

    private void selectionnerActiviteActuelle(String[][] data) {
        String titreActuel = getIntent().getStringExtra("titre");

        if (titreActuel == null) {
            return;
        }

        for (int i = 0; i < data.length; i++) {
            if (titreActuel.equals(data[i][0])) {
                spActivite.setSelection(i);
                return;
            }
        }

        etTitre.setText(titreActuel);
        etDescription.setText(getIntent().getStringExtra("description"));
        etDuree.setText(String.valueOf(getIntent().getIntExtra("duree", 10)));
    }

    private void modifierActivite() {
        String titre = etTitre.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String dureeStr = etDuree.getText().toString().trim();

        if (activiteId == -1) {
            Toast.makeText(this, "Erreur activité", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(titre)) {
            etTitre.setError("Titre obligatoire");
            return;
        }

        if (TextUtils.isEmpty(dureeStr)) {
            etDuree.setError("Durée obligatoire");
            return;
        }

        int duree;

        try {
            duree = Integer.parseInt(dureeStr);
        } catch (Exception e) {
            etDuree.setError("Durée invalide");
            return;
        }

        String selected = spType.getSelectedItem().toString();
        String type;

        if (selected.contains("Mentale")) {
            type = "mentale";
        } else if (selected.contains("Émotionnelle")) {
            type = "emotionnelle";
        } else {
            type = "discipline";
        }

        String plage = getIntent().getStringExtra("plage");
        String date = getIntent().getStringExtra("date");
        int userId = getIntent().getIntExtra("user_id", 1);

        Activite a = new Activite();
        a.setId(activiteId);
        a.setTitre(titre);
        a.setTypeActivite(type);
        a.setDescription(desc);
        a.setDureeMinutes(duree);
        a.setPlageHoraire(plage);
        a.setDatePlanifiee(date);
        a.setUtilisateurId(userId);

        int result = activiteDAO.modifier(a);

        if (result > 0) {
            Toast.makeText(this, "Modifié ✅", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Erreur modification", Toast.LENGTH_SHORT).show();
        }
    }
}