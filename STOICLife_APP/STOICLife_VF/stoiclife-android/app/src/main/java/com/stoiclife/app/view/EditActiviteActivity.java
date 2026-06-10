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

import java.util.List;

public class EditActiviteActivity extends AppCompatActivity {

    private EditText etTitre, etDescription, etDuree;
    private Spinner spType, spActivite;
    private Button btnSave;
    private TextView btnBack;

    private ActiviteDAO activiteDAO;
    private int activiteId;

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

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        types
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

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

        spType.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

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
        final List<String[]> data =
                activiteDAO.getCatalogueParType(type);

        if (data == null || data.isEmpty()) {
            String[] vide = {"Aucune activité disponible"};

            ArrayAdapter<String> adapterVide =
                    new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_spinner_item,
                            vide
                    );

            adapterVide.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            spActivite.setAdapter(adapterVide);
            etTitre.setText("");
            etDescription.setText("");
            etDuree.setText("");
            return;
        }

        String[] titres = new String[data.size()];

        for (int i = 0; i < data.size(); i++) {
            titres[i] = data.get(i)[0];
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        titres
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spActivite.setAdapter(adapter);

        spActivite.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (position >= 0 && position < data.size()) {
                            etTitre.setText(data.get(position)[0]);
                            etDescription.setText(data.get(position)[1]);
                            etDuree.setText(data.get(position)[2]);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        selectionnerActiviteActuelle(data);
    }

    private void selectionnerActiviteActuelle(List<String[]> data) {
        String titreActuel = getIntent().getStringExtra("titre");

        if (titreActuel == null) {
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            String[] item = data.get(i);

            if (titreActuel.equals(item[0])) {
                spActivite.setSelection(i);
                return;
            }
        }

        etTitre.setText(titreActuel);
        etDescription.setText(getIntent().getStringExtra("description"));
        etDuree.setText(
                String.valueOf(
                        getIntent().getIntExtra("duree", 10)
                )
        );
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