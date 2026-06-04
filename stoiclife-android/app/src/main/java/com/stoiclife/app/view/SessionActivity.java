package com.stoiclife.app.view;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.database.DatabaseHelper;

import java.io.IOException;
import java.util.Locale;

public class SessionActivity extends AppCompatActivity {

    private TextureView videoView;
    private ImageView imgBackground;

    private TextView tvTitre, tvTimer, tvConseil, tvTitreTop, btnBack;
    private MaterialButton btnPause, btnTerminer;
    private ProgressBar progressTimer;

    private MediaPlayer mediaPlayer;
    private CountDownTimer timer;

    private long tempsRestant;
    private long dureeTotale;

    private boolean isPaused = false;
    private boolean sessionTerminee = false;

    private String titre;
    private int duree;
    private int activiteId;

    private ActiviteDAO activiteDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_session);

        initialiserVues();
        recupererDonneesIntent();
        initialiserSession();
        configurerActions();
    }

    private void initialiserVues() {
        imgBackground = findViewById(R.id.imgBackground);
        videoView = findViewById(R.id.videoView);

        tvTitre = findViewById(R.id.tvTitre);
        tvTitreTop = findViewById(R.id.tvTitreTop);
        tvTimer = findViewById(R.id.tvTimer);
        tvConseil = findViewById(R.id.tvConseil);

        btnBack = findViewById(R.id.btnBack);
        btnPause = findViewById(R.id.btnPause);
        btnTerminer = findViewById(R.id.btnTerminer);

        progressTimer = findViewById(R.id.progressTimer);
    }

    private void recupererDonneesIntent() {
        titre = getIntent().getStringExtra("titre");
        duree = getIntent().getIntExtra("duree", 10);
        activiteId = getIntent().getIntExtra("id", -1);

        if (titre == null || titre.trim().isEmpty()) {
            titre = "Activité";
        }

        if (duree <= 0) {
            duree = 10;
        }
    }

    private void initialiserSession() {
        activiteDAO = new ActiviteDAO(DatabaseHelper.getInstance(this));

        if (activiteId != -1) {
            activiteDAO.marquerEnCours(activiteId);
        }

        tempsRestant = duree * 60L * 1000L;
        dureeTotale = tempsRestant;

        tvTitre.setText(titre);
        tvTitreTop.setText(titre);
        tvConseil.setText(getConseilByActivity(titre));

        afficherTemps(tempsRestant);
        progressTimer.setProgress(100);

        lancerMedia(titre);
        startTimer();
    }

    private void configurerActions() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminerSessionSansValider();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gererPauseReprise();
            }
        });

        btnTerminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminerActivite();
            }
        });
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(tempsRestant, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempsRestant = millisUntilFinished;
                afficherTemps(millisUntilFinished);
                mettreAJourProgression(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                progressTimer.setProgress(0);
                terminerActivite();
            }
        };

        timer.start();
    }

    private void afficherTemps(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / 60000);
        int seconds = (int) ((millisUntilFinished % 60000) / 1000);

        tvTimer.setText(
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        );
    }

    private void mettreAJourProgression(long millisUntilFinished) {
        if (dureeTotale <= 0) {
            return;
        }

        int progress = (int) ((millisUntilFinished * 100) / dureeTotale);
        progressTimer.setProgress(progress);
    }

    private void gererPauseReprise() {
        if (isPaused) {
            reprendreSession();
        } else {
            mettreEnPauseSession();
        }
    }

    private void mettreEnPauseSession() {
        if (timer != null) {
            timer.cancel();
        }

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        if (activiteId != -1) {
            activiteDAO.marquerEnCours(activiteId);
        }

        btnPause.setText("Reprendre");
        isPaused = true;
    }

    private void reprendreSession() {
        startTimer();

        if (mediaPlayer != null) {
            try {
                mediaPlayer.start();
            } catch (Exception ignored) {
            }
        }

        btnPause.setText("Pause");
        isPaused = false;
    }

    private void terminerActivite() {
        if (sessionTerminee) {
            return;
        }

        sessionTerminee = true;

        if (activiteId != -1) {
            activiteDAO.marquerTerminee(activiteId);

            int xpGagne = getXpByActivite(titre);
            activiteDAO.ajouterXpUtilisateur(activiteId, xpGagne);

            Toast.makeText(
                    SessionActivity.this,
                    "Activité terminée +" + xpGagne + " XP",
                    Toast.LENGTH_SHORT
            ).show();
        }

        quitterVersHome();
    }

    private void terminerSessionSansValider() {
        quitterVersHome();
    }

    private void quitterVersHome() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        releaseMediaPlayer();

        Intent intent = new Intent(SessionActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        finish();
    }

    private void lancerMedia(String titre) {
        imgBackground.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        int videoRes = getVideoByActivity(titre);
        lancerVideoAvecTexture(videoRes);
    }

    private void lancerVideoAvecTexture(final int videoRes) {
        videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                try {
                    releaseMediaPlayer();

                    Surface surface = new Surface(surfaceTexture);
                    mediaPlayer = new MediaPlayer();

                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                    .build()
                    );

                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoRes);

                    mediaPlayer.setDataSource(SessionActivity.this, uri);
                    mediaPlayer.setSurface(surface);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(1.0f, 1.0f);

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mp) {
                            videoView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scaleVideoAdaptive(mp, videoView);
                                }
                            });

                            mp.start();
                        }
                    });

                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(final MediaPlayer mp, int videoWidth, int videoHeight) {
                            videoView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scaleVideoAdaptive(mp, videoView);
                                }
                            });
                        }
                    });

                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                    tvConseil.setText("Impossible de lire la vidéo.");
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (mediaPlayer != null) {
                    videoView.post(new Runnable() {
                        @Override
                        public void run() {
                            scaleVideoAdaptive(mediaPlayer, videoView);
                        }
                    });
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                releaseMediaPlayer();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    private void scaleVideoAdaptive(MediaPlayer mp, TextureView view) {
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        if (videoWidth == 0 || videoHeight == 0 || viewWidth == 0 || viewHeight == 0) {
            return;
        }

        float videoRatio = (float) videoWidth / videoHeight;
        float viewRatio = (float) viewWidth / viewHeight;

        float scale;

        if (videoRatio > viewRatio) {
            scale = (float) viewHeight / videoHeight;
        } else {
            scale = (float) viewWidth / videoWidth;
        }

        float zoom = 1.04f;
        scale = scale * zoom;

        float dx = (viewWidth - videoWidth * scale) / 2f;
        float dy = (viewHeight - videoHeight * scale) / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);

        view.setTransform(matrix);
    }

    private void releaseMediaPlayer() {
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

    private int getVideoByActivity(String titre) {
        String t = titre.toLowerCase(Locale.ROOT);

        if (t.contains("respiration") || t.contains("relaxation") || t.contains("déconnexion")) {
            return R.raw.relaxation_edge;
        }

        if (t.contains("visualisation positive") || t.contains("affirmation") || t.contains("contenu inspirant")) {
            return R.raw.visualisation_positive_edge;
        }

        if (t.contains("gratitude")) {
            return R.raw.gratitude_edge;
        }

        if (t.contains("méditation") || t.contains("visualisation mentale") || t.contains("réflexion")) {
            return R.raw.meditation_edge;
        }

        if (t.contains("musique")) {
            return R.raw.rainwindow_edge;
        }

        if (t.contains("nature") || t.contains("pause")) {
            return R.raw.nature_edge;
        }

        if (t.contains("marche")) {
            return R.raw.marche_consciente_edge;
        }

        if (t.contains("focus")
                || t.contains("deep")
                || t.contains("étude")
                || t.contains("révision")
                || t.contains("lecture")
                || t.contains("scientifique")) {
            return R.raw.focus_study_edge;
        }

        if (t.contains("sport") || t.contains("exercice")) {
            return R.raw.sport_edge;
        }

        if (t.contains("douche")
                || t.contains("réveil")
                || t.contains("routine")
                || t.contains("réseaux sociaux")) {
            return R.raw.discipline_edge;
        }

        if (t.contains("journal")
                || t.contains("écriture")
                || t.contains("résumé")
                || t.contains("analyse")) {
            return R.raw.journal_edge;
        }

        if (t.contains("organisation")
                || t.contains("nettoyage")
                || t.contains("gestion")
                || t.contains("préparation")
                || t.contains("planification")
                || t.contains("objectifs")) {
            return R.raw.organisation_edge;
        }

        if (t.contains("apprendre")
                || t.contains("apprentissage")
                || t.contains("brainstorming")
                || t.contains("résolution")
                || t.contains("problème")
                || t.contains("concept")
                || t.contains("langue")) {
            return R.raw.apprentissage_edge;
        }

        return R.raw.nature_edge;
    }

    private String getConseilByActivity(String titre) {
        String t = titre.toLowerCase(Locale.ROOT);

        if (t.contains("respiration")) return "Inspire doucement, puis expire lentement.";
        if (t.contains("relaxation")) return "Relâche les tensions. Laisse ton corps se calmer.";
        if (t.contains("déconnexion")) return "Lâche prise. Coupe-toi du bruit mental.";
        if (t.contains("gratitude")) return "Apprécie ce que tu as déjà. Chaque petit moment compte.";
        if (t.contains("affirmation")) return "Répète calmement tes phrases positives avec confiance.";
        if (t.contains("contenu inspirant")) return "Écoute, observe et garde seulement ce qui t’élève.";
        if (t.contains("visualisation")) return "Imagine le meilleur scénario et avance avec sérénité.";
        if (t.contains("méditation")) return "Observe tes pensées sans les juger.";
        if (t.contains("musique")) return "Écoute calmement et relâche les tensions.";
        if (t.contains("marche")) return "Marche lentement. Observe chaque pas.";
        if (t.contains("nature")) return "Observe le calme autour de toi.";
        if (t.contains("pause")) return "Éloigne-toi des écrans et respire.";

        if (t.contains("focus") || t.contains("deep") || t.contains("étude") || t.contains("révision") || t.contains("lecture")) {
            return "Une seule tâche à la fois. Ignore les distractions.";
        }

        if (t.contains("sport") || t.contains("exercice")) {
            return "Bouge avec énergie. Ton corps te remercie.";
        }

        if (t.contains("douche") || t.contains("réveil") || t.contains("routine") || t.contains("réseaux sociaux")) {
            return "La discipline commence par une petite action.";
        }

        if (t.contains("journal") || t.contains("écriture") || t.contains("résumé") || t.contains("analyse")) {
            return "Écris, clarifie, puis avance plus léger.";
        }

        if (t.contains("organisation") || t.contains("nettoyage") || t.contains("gestion") || t.contains("planification")) {
            return "Range ton espace, clarifie ton esprit.";
        }

        if (t.contains("apprendre") || t.contains("apprentissage") || t.contains("brainstorming") || t.contains("résolution")) {
            return "Apprends un peu chaque jour. Le progrès se construit.";
        }

        return "Reste concentré. Fais cette activité calmement.";
    }

    private int getXpByActivite(String titre) {
        String t = titre.toLowerCase(Locale.ROOT);

        if (t.contains("respiration") || t.contains("relaxation")) return 5;
        if (t.contains("méditation") || t.contains("visualisation")) return 8;
        if (t.contains("focus") || t.contains("deep") || t.contains("étude")) return 15;
        if (t.contains("sport") || t.contains("exercice")) return 20;
        if (t.contains("journal") || t.contains("écriture")) return 10;

        return 10;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!sessionTerminee && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        releaseMediaPlayer();
    }
}