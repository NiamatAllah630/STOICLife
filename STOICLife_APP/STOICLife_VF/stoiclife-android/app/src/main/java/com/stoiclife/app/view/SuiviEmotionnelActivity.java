package com.stoiclife.app.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuiviEmotionnelActivity extends AppCompatActivity {

    private RadioGroup rgHumeur, rgDeclencheur;
    private SeekBar seekIntensite;
    private TextView tvIntensite, tvRecommandation;
    private EditText etNote;
    private LinearLayout llHistorique;

    private DatabaseHelper db;
    private int userId;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suivi_emotionnel);

        initialiserDonnees();
        initialiserVues();
        configurerActions();

        chargerHistorique();
    }

    private void initialiserDonnees() {
        db = DatabaseHelper.getInstance(this);
        userId = new SessionManager(this).getUserId();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void initialiserVues() {
        TextView btnRetour = findViewById(R.id.btn_retour_emotion);
        MaterialButton btnEnregistrer = findViewById(R.id.btn_enregistrer_emotion);

        rgHumeur = findViewById(R.id.rg_humeur);
        rgDeclencheur = findViewById(R.id.rg_declencheur);
        seekIntensite = findViewById(R.id.seek_intensite);
        tvIntensite = findViewById(R.id.tv_intensite_value);
        tvRecommandation = findViewById(R.id.tv_recommandation);
        etNote = findViewById(R.id.et_note_emotion);
        llHistorique = findViewById(R.id.ll_historique_emotions);

        seekIntensite.setProgress(2);
        tvIntensite.setText("3 / 5");

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEnregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enregistrerEmotion();
            }
        });
    }

    private void configurerActions() {
        seekIntensite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvIntensite.setText((progress + 1) + " / 5");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        rgHumeur.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    String humeur = getTexteRadio(checkedId);
                    tvRecommandation.setText(genererRecommandation(humeur));
                }
            }
        });
    }

    private void enregistrerEmotion() {
        int humeurId = rgHumeur.getCheckedRadioButtonId();
        int declencheurId = rgDeclencheur.getCheckedRadioButtonId();

        if (humeurId == -1) {
            Toast.makeText(this, "Choisissez votre humeur.", Toast.LENGTH_SHORT).show();
            return;
        }

        String humeur = getTexteRadio(humeurId);
        String declencheur;

        if (declencheurId == -1) {
            declencheur = "Non précisé";
        } else {
            declencheur = getTexteRadio(declencheurId);
        }

        int intensite = seekIntensite.getProgress() + 1;
        String note = etNote.getText().toString().trim();

        SQLiteDatabase sqlDb = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("utilisateur_id", userId);
        values.put("humeur", humeur);
        values.put("intensite", intensite);
        values.put("declencheur", declencheur);
        values.put("date_jour", today);
        values.put("note", note);
        values.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        long result = sqlDb.insert(DatabaseHelper.TABLE_SUIVI_EMOTIONNEL, null, values);
        sqlDb.close();

        if (result > 0) {
            Toast.makeText(this, "Suivi émotionnel enregistré !", Toast.LENGTH_SHORT).show();
            etNote.setText("");
            chargerHistorique();
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement.", Toast.LENGTH_SHORT).show();
        }
    }

    private void chargerHistorique() {
        llHistorique.removeAllViews();

        SQLiteDatabase sqlDb = db.getReadableDatabase();

        Cursor cursor = sqlDb.query(
                DatabaseHelper.TABLE_SUIVI_EMOTIONNEL,
                null,
                "utilisateur_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC",
                "10"
        );

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date_jour"));
            String humeur = cursor.getString(cursor.getColumnIndexOrThrow("humeur"));
            int intensite = cursor.getInt(cursor.getColumnIndexOrThrow("intensite"));
            String declencheur = cursor.getString(cursor.getColumnIndexOrThrow("declencheur"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

            View item = LayoutInflater.from(this).inflate(R.layout.item_emotion, llHistorique, false);

            TextView tvEmoji = item.findViewById(R.id.tv_emotion_emoji);
            TextView tvTitre = item.findViewById(R.id.tv_emotion_titre);
            TextView tvDetails = item.findViewById(R.id.tv_emotion_details);
            TextView tvNote = item.findViewById(R.id.tv_emotion_note);

            tvEmoji.setText(getEmojiHumeur(humeur));
            tvTitre.setText(nettoyerTexteRadio(humeur) + " · " + date);
            tvDetails.setText("Intensité : " + intensite + "/5 · Déclencheur : " + nettoyerTexteRadio(declencheur));

            if (note == null || note.trim().isEmpty()) {
                tvNote.setText("Aucune note ajoutée.");
            } else {
                tvNote.setText(note);
            }

            llHistorique.addView(item);
        }

        cursor.close();
        sqlDb.close();
    }

    private String getTexteRadio(int id) {
        RadioButton rb = findViewById(id);

        if (rb == null) {
            return "";
        }

        return rb.getText().toString().trim();
    }

    private String nettoyerTexteRadio(String texte) {
        if (texte == null) {
            return "";
        }

        return texte
                .replace("😌", "")
                .replace("😟", "")
                .replace("😔", "")
                .replace("🔥", "")
                .replace("😴", "")
                .trim();
    }

    private String getEmojiHumeur(String humeur) {
        if (humeur == null) {
            return "🙂";
        }

        String h = humeur.toLowerCase(Locale.ROOT);

        if (h.contains("calme")) return "😌";
        if (h.contains("stress")) return "😟";
        if (h.contains("triste")) return "😔";
        if (h.contains("motivé")) return "🔥";
        if (h.contains("fatigué")) return "😴";

        return "🙂";
    }

    private String genererRecommandation(String humeur) {
        if (humeur == null) {
            return "Recommandation : choisissez une activité simple pour prendre soin de votre équilibre intérieur.";
        }

        String h = humeur.toLowerCase(Locale.ROOT);

        if (h.contains("stress")) {
            return "Recommandation : faites une respiration consciente de 5 minutes pour calmer votre esprit.";
        }

        if (h.contains("triste")) {
            return "Recommandation : écrivez une entrée de gratitude ou lisez une citation stoïcienne.";
        }

        if (h.contains("fatigué")) {
            return "Recommandation : choisissez une activité légère comme relaxation ou pause nature.";
        }

        if (h.contains("motivé")) {
            return "Recommandation : profitez de cette énergie pour réaliser une activité importante.";
        }

        if (h.contains("calme")) {
            return "Recommandation : maintenez cet état avec une courte méditation ou une lecture stoïcienne.";
        }

        return "Recommandation : choisissez une activité simple pour prendre soin de votre équilibre intérieur.";
    }
}