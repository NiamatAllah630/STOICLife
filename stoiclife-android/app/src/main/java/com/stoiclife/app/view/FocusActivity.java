package com.stoiclife.app.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.stoiclife.app.R;

public class FocusActivity extends AppCompatActivity {

    private TextView tvTimer, tvTache, tvStatut, tvQuote, tvPourcent;
    private Button btnDemarrer, btn25, btn45, btn60;
    private ImageButton btnSilence, btnPluie, btnNature, btnFeu;
    private ProgressBar progressTimer;
    private View circleAnim1, circleAnim2, circleAnim3;

    private CountDownTimer countDownTimer;
    private long totalMillis = 25 * 60 * 1000L;
    private long remainingMillis = totalMillis;
    private boolean timerEnCours = false;

    private MediaPlayer mediaPlayer;
    private int selectedSound = 0;

    private ObjectAnimator pulse1, pulse1y, pulse2, pulse2y, pulse3, pulse3y;

    private static final String[] QUOTES = {
            "La discipline est la racine de toute réussite.",
            "Concentre-toi sur ce que tu contrôles.",
            "L'excellence est une habitude, pas un acte.",
            "Chaque moment est une occasion de grandir.",
            "Nul vent n'est favorable à qui ne sait où aller.",
            "Ce qui ne dépend pas de toi — laisse-le.",
            "L'action est l'unique réponse digne du destin."
    };

    private int quoteIndex = 0;

    private final Runnable quoteRunnable = new Runnable() {
        @Override
        public void run() {
            quoteIndex = (quoteIndex + 1) % QUOTES.length;
            tvQuote.setText(QUOTES[quoteIndex]);
            tvQuote.postDelayed(this, 12000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configurerFenetre();
        setContentView(R.layout.activity_focus);

        initialiserVues();
        initialiserEtatParDefaut();
        recupererTacheIntent();
        configurerActions();

        demarrerAnimations();
        demarrerRotationQuotes();
    }

    private void configurerFenetre() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    private void initialiserVues() {
        tvTimer = findViewById(R.id.tv_timer);
        tvTache = findViewById(R.id.tv_tache_focus);
        tvStatut = findViewById(R.id.tv_statut_focus);
        tvQuote = findViewById(R.id.tv_quote_focus);
        tvPourcent = findViewById(R.id.tv_pourcent);

        progressTimer = findViewById(R.id.progress_timer);

        btnDemarrer = findViewById(R.id.btn_demarrer_focus);
        btn25 = findViewById(R.id.btn_25min);
        btn45 = findViewById(R.id.btn_45min);
        btn60 = findViewById(R.id.btn_60min);

        btnSilence = findViewById(R.id.btn_son_silence);
        btnPluie = findViewById(R.id.btn_son_pluie);
        btnNature = findViewById(R.id.btn_son_nature);
        btnFeu = findViewById(R.id.btn_son_feu);

        circleAnim1 = findViewById(R.id.circle_anim_1);
        circleAnim2 = findViewById(R.id.circle_anim_2);
        circleAnim3 = findViewById(R.id.circle_anim_3);
    }

    private void initialiserEtatParDefaut() {
        tvTimer.setText("25:00");
        tvStatut.setText("Prêt");
        tvPourcent.setText("0% accompli");
        progressTimer.setProgress(0);
        btnDemarrer.setText("Démarrer");

        tvQuote.setText(QUOTES[0]);

        mettreAJourSelectionDuree(25);
        mettreAJourSelectionSon(0);
    }

    private void recupererTacheIntent() {
        String tache = getIntent().getStringExtra("tache");

        if (tache != null && !tache.trim().isEmpty()) {
            tvTache.setText(tache);
        } else {
            tvTache.setText("Session de focus");
        }
    }

    private void configurerActions() {
        btn25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choisirDuree(25);
            }
        });

        btn45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choisirDuree(45);
            }
        });

        btn60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choisirDuree(60);
            }
        });

        btnSilence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changerSon(0);
            }
        });

        btnPluie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changerSon(1);
            }
        });

        btnNature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changerSon(2);
            }
        });

        btnFeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changerSon(3);
            }
        });

        btnDemarrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                animerBoutonDemarrer(view);
                gererDemarragePause();
            }
        });

        ImageButton btnBack = findViewById(R.id.btn_back_focus);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmerSortie();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmerSortie();
            }
        });
    }

    private void animerBoutonDemarrer(final View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(80)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .start();
                    }
                })
                .start();
    }

    private void gererDemarragePause() {
        if (!timerEnCours) {
            if (remainingMillis <= 0) {
                remainingMillis = totalMillis;
                progressTimer.setProgress(0);
                tvPourcent.setText("0% accompli");
            }

            demarrerTimer();
        } else {
            pauserTimer();
        }
    }

    private void demarrerAnimations() {
        pulse1 = creerPulse(circleAnim1, 0.85f, 1.0f, 2000, 0);
        pulse1y = creerPulseY(circleAnim1, 0.85f, 1.0f, 2000, 0);

        pulse2 = creerPulse(circleAnim2, 0.90f, 1.05f, 2600, 400);
        pulse2y = creerPulseY(circleAnim2, 0.90f, 1.05f, 2600, 400);

        pulse3 = creerPulse(circleAnim3, 0.92f, 1.08f, 3200, 800);
        pulse3y = creerPulseY(circleAnim3, 0.92f, 1.08f, 3200, 800);

        pulse1.start();
        pulse1y.start();
        pulse2.start();
        pulse2y.start();
        pulse3.start();
        pulse3y.start();
    }

    private ObjectAnimator creerPulse(View target, float from, float to, long duration, long delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "scaleX", from, to);
        animator.setDuration(duration);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setStartDelay(delay);
        animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        return animator;
    }

    private ObjectAnimator creerPulseY(View target, float from, float to, long duration, long delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "scaleY", from, to);
        animator.setDuration(duration);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setStartDelay(delay);
        animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        return animator;
    }

    private void demarrerRotationQuotes() {
        tvQuote.removeCallbacks(quoteRunnable);
        tvQuote.postDelayed(quoteRunnable, 12000);
    }

    private void demarrerTimer() {
        timerEnCours = true;
        btnDemarrer.setText("Pause");
        tvStatut.setText("En cours...");

        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                mettreAJourAffichageTimer();
            }

            @Override
            public void onFinish() {
                terminerSessionFocus();
            }
        }.start();
    }

    private void terminerSessionFocus() {
        timerEnCours = false;
        remainingMillis = 0;

        tvTimer.setText("00:00");
        progressTimer.setProgress(100);
        tvPourcent.setText("100% accompli");
        tvStatut.setText("Session terminée !");
        btnDemarrer.setText("Recommencer");

        arreterSon();

        Toast.makeText(
                FocusActivity.this,
                "Félicitations ! Session accomplie.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void pauserTimer() {
        timerEnCours = false;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        btnDemarrer.setText("Reprendre");
        tvStatut.setText("En pause");
    }

    private void mettreAJourAffichageTimer() {
        long minutes = remainingMillis / 60000;
        long seconds = (remainingMillis % 60000) / 1000;

        tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

        int progress = (int) ((1f - (float) remainingMillis / totalMillis) * 100);
        progressTimer.setProgress(progress);
        tvPourcent.setText(progress + "% accompli");
    }

    private void choisirDuree(int minutes) {
        if (timerEnCours) {
            pauserTimer();
        }

        totalMillis = minutes * 60 * 1000L;
        remainingMillis = totalMillis;

        tvTimer.setText(String.format("%02d:00", minutes));
        progressTimer.setProgress(0);
        tvPourcent.setText("0% accompli");
        tvStatut.setText("Prêt");
        btnDemarrer.setText("Démarrer");

        mettreAJourSelectionDuree(minutes);
    }

    private void mettreAJourSelectionDuree(int minutes) {
        appliquerEtatDuree(btn25, minutes == 25);
        appliquerEtatDuree(btn45, minutes == 45);
        appliquerEtatDuree(btn60, minutes == 60);
    }

    private void appliquerEtatDuree(Button button, boolean selected) {
        if (selected) {
            button.setAlpha(1f);
            button.setScaleX(1.08f);
            button.setScaleY(1.08f);
        } else {
            button.setAlpha(0.45f);
            button.setScaleX(1f);
            button.setScaleY(1f);
        }
    }

    private void changerSon(int type) {
        arreterSon();

        selectedSound = type;
        mettreAJourSelectionSon(type);

        if (type == 0) {
            if (timerEnCours) {
                tvStatut.setText("En cours...");
            } else {
                tvStatut.setText("Prêt");
            }
            return;
        }

        int resId = getAudioResId(type);

        if (resId != 0) {
            try {
                mediaPlayer = MediaPlayer.create(this, resId);

                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erreur de lecture audio", Toast.LENGTH_SHORT).show();
            }
        }

        appliquerStatutSon(type);
    }

    private int getAudioResId(int type) {
        if (type == 1) {
            return R.raw.pluie;
        } else if (type == 2) {
            return R.raw.foret;
        } else if (type == 3) {
            return R.raw.feu;
        }

        return 0;
    }

    private void appliquerStatutSon(int type) {
        if (type == 1) {
            tvStatut.setText("Pluie activée");
        } else if (type == 2) {
            tvStatut.setText("Forêt activée");
        } else if (type == 3) {
            tvStatut.setText("Feu activé");
        }
    }

    private void mettreAJourSelectionSon(int type) {
        resetSoundButton(btnSilence);
        resetSoundButton(btnPluie);
        resetSoundButton(btnNature);
        resetSoundButton(btnFeu);

        if (type == 0) {
            activateSoundButton(btnSilence);
        } else if (type == 1) {
            activateSoundButton(btnPluie);
        } else if (type == 2) {
            activateSoundButton(btnNature);
        } else if (type == 3) {
            activateSoundButton(btnFeu);
        }
    }

    private void resetSoundButton(ImageButton button) {
        button.setAlpha(0.55f);
        button.setScaleX(1f);
        button.setScaleY(1f);
        button.setBackgroundResource(R.drawable.bg_sound_inactive);
        button.setColorFilter(ContextCompat.getColor(this, R.color.focus_icon_inactive));
    }

    private void activateSoundButton(final ImageButton button) {
        button.setAlpha(1f);
        button.setBackgroundResource(R.drawable.bg_sound_active);
        button.setColorFilter(ContextCompat.getColor(this, R.color.gold_accent));

        button.animate()
                .scaleX(1.18f)
                .scaleY(1.18f)
                .rotation(4f)
                .setDuration(120)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .rotation(0f)
                                .setDuration(120)
                                .start();
                    }
                })
                .start();

        ObjectAnimator glow = ObjectAnimator.ofFloat(button, "alpha", 0.75f, 1f);

        glow.setDuration(900);
        glow.setRepeatMode(ValueAnimator.REVERSE);
        glow.setRepeatCount(2);
        glow.setInterpolator(new LinearInterpolator());
        glow.start();
    }

    private void arreterSon() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {
            }

            try {
                mediaPlayer.release();
            } catch (Exception ignored) {
            }

            mediaPlayer = null;
        }
    }

    private void confirmerSortie() {
        if (timerEnCours) {
            new AlertDialog.Builder(this)
                    .setTitle("Quitter le Mode Focus ?")
                    .setMessage("La session en cours sera annulée.")
                    .setPositiveButton("Quitter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            );
                        }
                    })
                    .setNegativeButton("Continuer", null)
                    .show();
        } else {
            finish();
            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        tvQuote.removeCallbacks(quoteRunnable);
        arreterSon();

        annulerAnimations();
    }

    private void annulerAnimations() {
        if (pulse1 != null) pulse1.cancel();
        if (pulse1y != null) pulse1y.cancel();
        if (pulse2 != null) pulse2.cancel();
        if (pulse2y != null) pulse2y.cancel();
        if (pulse3 != null) pulse3.cancel();
        if (pulse3y != null) pulse3y.cancel();
    }
}