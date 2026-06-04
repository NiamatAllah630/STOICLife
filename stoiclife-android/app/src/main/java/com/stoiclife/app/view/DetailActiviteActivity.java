package com.stoiclife.app.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Activite;

public class DetailActiviteActivity extends AppCompatActivity {

    private TextView tvIcon, tvTitre, tvType, tvDuree, tvStatut, tvDesc, tvPeriode, tvFooter, btnBack;
    private MaterialButton btnModifier, btnSupprimer;

    private ActiviteDAO activiteDAO;
    private Activite activite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activite);

        activiteDAO = new ActiviteDAO(DatabaseHelper.getInstance(this));

        initialiserVues();

        int id = getIntent().getIntExtra("id", -1);

        if (id == -1) {
            finish();
            return;
        }

        activite = activiteDAO.getById(id);

        if (activite == null) {
            finish();
            return;
        }

        afficherActivite();
        configurerActions();
    }

    private void initialiserVues() {
        tvIcon = findViewById(R.id.tv_icon);
        tvTitre = findViewById(R.id.tv_titre);
        tvType = findViewById(R.id.tv_type);
        tvDuree = findViewById(R.id.tv_duree);
        tvStatut = findViewById(R.id.tv_statut);
        tvDesc = findViewById(R.id.tv_desc);
        tvPeriode = findViewById(R.id.tv_periode);
        tvFooter = findViewById(R.id.tv_footer);
        btnBack = findViewById(R.id.btn_back);

        btnModifier = findViewById(R.id.btn_modifier);
        btnSupprimer = findViewById(R.id.btn_supprimer);
    }

    private void configurerActions() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirModification();
            }
        });

        btnSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmerSuppression();
            }
        });
    }

    private void ouvrirModification() {
        Intent intent = new Intent(DetailActiviteActivity.this, EditActiviteActivity.class);

        intent.putExtra("id", activite.getId());
        intent.putExtra("titre", activite.getTitre());
        intent.putExtra("type", activite.getTypeActivite());
        intent.putExtra("description", activite.getDescription());
        intent.putExtra("duree", activite.getDureeMinutes());

        intent.putExtra("plage", activite.getPlageHoraire());
        intent.putExtra("date", activite.getDatePlanifiee());
        intent.putExtra("user_id", activite.getUtilisateurId());

        startActivity(intent);
    }

    private void confirmerSuppression() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActiviteActivity.this);

        builder.setTitle("Supprimer l’activité ?");
        builder.setMessage("Voulez-vous vraiment supprimer : " + activite.getTitre() + " ?");

        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                activiteDAO.supprimer(activite.getId());
                Toast.makeText(
                        DetailActiviteActivity.this,
                        "Activité supprimée 🗑️",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (activiteDAO != null && activite != null) {
            activite = activiteDAO.getById(activite.getId());

            if (activite != null) {
                afficherActivite();
            }
        }
    }

    private void afficherActivite() {
        tvTitre.setText(activite.getTitre());

        afficherTypeEtIcone();
        afficherDuree();
        afficherPeriode();
        afficherDescription();
        afficherStatut();
    }

    private void afficherTypeEtIcone() {
        String type = activite.getTypeActivite();

        if ("mentale".equals(type)) {
            tvIcon.setText("🧠");
            tvType.setText("💡 Type : Mentale");
        } else if ("emotionnelle".equals(type)) {
            tvIcon.setText("💛");
            tvType.setText("💡 Type : Émotionnelle");
        } else {
            tvIcon.setText("🎯");
            tvType.setText("💡 Type : Discipline");
        }
    }

    private void afficherDuree() {
        tvDuree.setText("⏱ Durée : " + activite.getDureeMinutes() + " minutes");
    }

    private void afficherPeriode() {
        String periode = activite.getPlageHoraire();

        if ("matin".equals(periode)) {
            tvPeriode.setText("🕒 Période : 🌅 Matin");
        } else if ("journee".equals(periode)) {
            tvPeriode.setText("🕒 Période : ☀️ Journée");
        } else if ("soir".equals(periode)) {
            tvPeriode.setText("🕒 Période : 🌙 Soir");
        } else {
            tvPeriode.setText("🕒 Période : Aujourd’hui");
        }
    }

    private void afficherDescription() {
        String desc = activite.getDescription();

        if (desc == null || desc.trim().isEmpty()) {
            desc = "Aucune description.";
        }

        tvDesc.setText(desc);
    }

    private void afficherStatut() {
        if (activite.isTerminee()) {
            tvStatut.setText("✓ Terminée");
            tvStatut.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvFooter.setText("Cette activité est terminée.");
        } else if (activite.isEnCours()) {
            tvStatut.setText("⏳ En cours");
            tvStatut.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvFooter.setText("Cette activité est actuellement en cours.");
        } else {
            tvStatut.setText("○ Planifiée");
            tvStatut.setTextColor(getResources().getColor(R.color.gold_accent));
            tvFooter.setText("Cette activité est planifiée.");
        }
    }
}