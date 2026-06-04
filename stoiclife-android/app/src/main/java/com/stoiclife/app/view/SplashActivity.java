package com.stoiclife.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stoiclife.app.R;
import com.stoiclife.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView appName;
    private TextView tagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initialiserVues();
        lancerAnimations();
        redirigerApresDelai();
    }

    private void initialiserVues() {
        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.splash_app_name);
        tagline = findViewById(R.id.splash_tagline);
    }

    private void lancerAnimations() {
        AnimationSet logoAnim = new AnimationSet(true);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(900);

        ScaleAnimation scaleUp = new ScaleAnimation(
                0.6f,
                1f,
                0.6f,
                1f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );

        scaleUp.setDuration(900);

        logoAnim.addAnimation(fadeIn);
        logoAnim.addAnimation(scaleUp);

        logo.startAnimation(logoAnim);

        AlphaAnimation textFade = new AlphaAnimation(0f, 1f);
        textFade.setDuration(800);
        textFade.setStartOffset(400);
        textFade.setFillAfter(true);

        appName.startAnimation(textFade);
        tagline.startAnimation(textFade);
    }

    private void redirigerApresDelai() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ouvrirEcranSuivant();
            }
        }, 2200);
    }

    private void ouvrirEcranSuivant() {
        SessionManager session = new SessionManager(SplashActivity.this);

        Intent intent;

        if (session.estConnecte()) {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}