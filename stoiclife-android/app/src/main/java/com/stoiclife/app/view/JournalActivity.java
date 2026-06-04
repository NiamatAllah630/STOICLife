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
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    private EditText etJournal;
    private RadioGroup rgPrompt;
    private LinearLayout llEntrees;
    private TextView tvPrompt;

    private RadioButton rbIntention, rbBilan, rbGratitude, rbDiscipline, rbLibre;

    private DatabaseHelper db;
    private int userId;
    private String today;
    private String typeActuel = "intention";
    private int categorieActuelleIndex = 0;
    private int questionActuelleIndex = 0;

    private static final String[][] PROMPTS = {
            {
                    "Quelles sont mes intentions pour aujourd’hui ?",
                    "Quelle est la chose la plus importante que je veux accomplir aujourd’hui ?",
                    "Comment puis-je rester calme face aux difficultés aujourd’hui ?",
                    "Quelle attitude positive vais-je pratiquer aujourd’hui ?",
                    "Sur quoi dois-je concentrer mon énergie aujourd’hui ?",
                    "Qu’est-ce qui dépend vraiment de moi aujourd’hui ?",
                    "Comment puis-je agir avec discipline et sérénité aujourd’hui ?",
                    "Quelle mauvaise habitude dois-je éviter aujourd’hui ?",
                    "Quel petit progrès puis-je faire aujourd’hui ?",
                    "Comment puis-je rendre cette journée utile et équilibrée ?"
            },
            {
                    "Qu’ai-je bien fait aujourd’hui ?",
                    "Qu’est-ce que je peux améliorer demain ?",
                    "Ai-je agi aujourd’hui selon mes valeurs ?",
                    "Quelle difficulté ai-je rencontrée aujourd’hui ?",
                    "Comment ai-je réagi face aux imprévus ?",
                    "Ai-je gardé mon calme dans les moments difficiles ?",
                    "Quelle leçon puis-je retenir de cette journée ?",
                    "De quoi suis-je fier aujourd’hui ?",
                    "Qu’est-ce que j’aurais pu faire autrement ?",
                    "Comment puis-je terminer cette journée avec paix intérieure ?"
            },
            {
                    "Quelles sont les trois choses pour lesquelles je suis reconnaissant aujourd’hui ?",
                    "Quelle personne a rendu ma journée meilleure ?",
                    "Quelle petite chose m’a apporté de la joie aujourd’hui ?",
                    "Qu’est-ce que j’ai aujourd’hui que je ne dois pas considérer comme acquis ?",
                    "Quel moment simple m’a fait du bien ?",
                    "Quelle qualité chez moi puis-je apprécier aujourd’hui ?",
                    "Quelle difficulté m’a appris quelque chose de positif ?",
                    "Quelle opportunité ai-je eue aujourd’hui ?",
                    "Qu’est-ce qui m’a donné de l’espoir aujourd’hui ?",
                    "Pour quelle partie de ma vie suis-je reconnaissant en ce moment ?"
            },
            {
                    "Quelle habitude dois-je respecter aujourd’hui ?",
                    "Quelle action demande de la discipline aujourd’hui ?",
                    "Quelle tentation dois-je éviter pour rester concentré ?",
                    "Comment puis-je choisir l’effort utile au lieu du confort immédiat ?",
                    "Dans quelle situation dois-je pratiquer la maîtrise de soi ?",
                    "Quelle tâche importante ai-je tendance à repousser ?",
                    "Comment puis-je rester fidèle à mes objectifs aujourd’hui ?",
                    "Quelle distraction dois-je limiter aujourd’hui ?",
                    "Quel engagement personnel dois-je honorer ?",
                    "Comment puis-je devenir un peu plus discipliné aujourd’hui ?"
            },
            {
                    "Écris librement ce que tu ressens en ce moment.",
                    "Quelle pensée occupe ton esprit aujourd’hui ?",
                    "Qu’est-ce que tu as besoin d’exprimer sans jugement ?",
                    "Quel événement récent veux-tu comprendre davantage ?",
                    "Quelle émotion veux-tu déposer ici ?",
                    "De quoi as-tu besoin de te libérer mentalement ?",
                    "Quelle question personnelle aimerais-tu explorer ?",
                    "Qu’aimerais-tu dire à ton toi futur ?",
                    "Quelle vérité intérieure veux-tu reconnaître aujourd’hui ?",
                    "Écris tout ce qui te vient à l’esprit, sans chercher à être parfait."
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        db = DatabaseHelper.getInstance(this);
        userId = new SessionManager(this).getUserId();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        initialiserVues();
        configurerActions();
        configurerScrollZoneTexte();

        afficherPrompt(0);
        mettreAJourStyleRadio();
        chargerEntrees();
    }

    private void initialiserVues() {
        TextView btnRetour = findViewById(R.id.btn_retour_journal);
        TextView btnQuestionSuivante = findViewById(R.id.btn_question_suivante);
        MaterialButton btnEnregistrer = findViewById(R.id.btn_enregistrer_journal);

        etJournal = findViewById(R.id.et_journal_contenu);
        rgPrompt = findViewById(R.id.rg_prompt_type);
        llEntrees = findViewById(R.id.ll_journal_entrees);
        tvPrompt = findViewById(R.id.tv_prompt_guide);

        rbIntention = findViewById(R.id.rb_intention);
        rbBilan = findViewById(R.id.rb_bilan);
        rbGratitude = findViewById(R.id.rb_gratitude);
        rbDiscipline = findViewById(R.id.rb_discipline);
        rbLibre = findViewById(R.id.rb_libre);

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnQuestionSuivante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionSuivante();
            }
        });

        btnEnregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enregistrer();
            }
        });
    }

    private void configurerActions() {
        rgPrompt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index;

                if (checkedId == R.id.rb_bilan) {
                    index = 1;
                    typeActuel = "bilan";
                } else if (checkedId == R.id.rb_gratitude) {
                    index = 2;
                    typeActuel = "gratitude";
                } else if (checkedId == R.id.rb_discipline) {
                    index = 3;
                    typeActuel = "discipline";
                } else if (checkedId == R.id.rb_libre) {
                    index = 4;
                    typeActuel = "libre";
                } else {
                    index = 0;
                    typeActuel = "intention";
                }

                afficherPrompt(index);
                mettreAJourStyleRadio();
            }
        });
    }
    private void configurerScrollZoneTexte() {
        etJournal.setVerticalScrollBarEnabled(true);
        etJournal.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        etJournal.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        etJournal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.et_journal_contenu) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);

                    if (event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_CANCEL) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }

                return false;
            }
        });
    }

    private void afficherPrompt(int indexCategorie) {
        categorieActuelleIndex = indexCategorie;
        questionActuelleIndex = 0;
        tvPrompt.setText(PROMPTS[categorieActuelleIndex][questionActuelleIndex]);
    }

    private void questionSuivante() {
        questionActuelleIndex++;

        if (questionActuelleIndex >= PROMPTS[categorieActuelleIndex].length) {
            questionActuelleIndex = 0;
        }

        tvPrompt.setText(PROMPTS[categorieActuelleIndex][questionActuelleIndex]);
    }

    private void mettreAJourStyleRadio() {
        int navy = ContextCompat.getColor(this, R.color.navy_primary);
        int gold = ContextCompat.getColor(this, R.color.gold_accent);

        appliquerStyleCategorie(rbIntention, navy, gold);
        appliquerStyleCategorie(rbBilan, navy, gold);
        appliquerStyleCategorie(rbGratitude, navy, gold);
        appliquerStyleCategorie(rbDiscipline, navy, gold);
        appliquerStyleCategorie(rbLibre, navy, gold);
    }

    private void appliquerStyleCategorie(RadioButton radioButton, int navy, int gold) {
        radioButton.setTextColor(navy);

        if (radioButton.isChecked()) {
            radioButton.setAlpha(1f);
            radioButton.setTextColor(navy);
        } else {
            radioButton.setAlpha(0.72f);
            radioButton.setTextColor(navy);
        }
    }

    private void enregistrer() {
        String contenu = etJournal.getText().toString().trim();

        if (contenu.isEmpty()) {
            Toast.makeText(this, "Écrivez quelque chose avant d'enregistrer.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase sqlDb = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("utilisateur_id", userId);
        values.put("date_jour", today);
        values.put("type_prompt", typeActuel);
        values.put("contenu", contenu);
        values.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        sqlDb.insert(DatabaseHelper.TABLE_JOURNAL, null, values);
        sqlDb.close();

        etJournal.setText("");
        Toast.makeText(this, "Journal enregistré !", Toast.LENGTH_SHORT).show();

        chargerEntrees();
    }

    private void chargerEntrees() {
        llEntrees.removeAllViews();

        SQLiteDatabase sqlDb = db.getReadableDatabase();

        Cursor cursor = sqlDb.query(
                DatabaseHelper.TABLE_JOURNAL,
                null,
                "utilisateur_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC",
                "20"
        );

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date_jour"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type_prompt"));
            String contenu = cursor.getString(cursor.getColumnIndexOrThrow("contenu"));

            View item = LayoutInflater.from(this).inflate(R.layout.item_journal, llEntrees, false);

            TextView tvDate = item.findViewById(R.id.tv_journal_date);
            TextView tvContenu = item.findViewById(R.id.tv_journal_contenu);
            TextView tvIcon = item.findViewById(R.id.tv_journal_icon);

            tvDate.setText(formaterType(type) + " · " + date);
            tvContenu.setText(contenu);
            tvIcon.setText(getIconeType(type));

            llEntrees.addView(item);
        }

        cursor.close();
        sqlDb.close();
    }

    private String formaterType(String type) {
        if ("bilan".equals(type)) {
            return "Bilan";
        } else if ("gratitude".equals(type)) {
            return "Gratitude";
        } else if ("discipline".equals(type)) {
            return "Discipline";
        } else if ("libre".equals(type)) {
            return "Libre";
        } else {
            return "Intention";
        }
    }

    private String getIconeType(String type) {
        if ("bilan".equals(type)) {
            return "🌙";
        } else if ("gratitude".equals(type)) {
            return "♡";
        } else if ("discipline".equals(type)) {
            return "🎯";
        } else if ("libre".equals(type)) {
            return "✍️";
        } else {
            return "☀️";
        }
    }
}