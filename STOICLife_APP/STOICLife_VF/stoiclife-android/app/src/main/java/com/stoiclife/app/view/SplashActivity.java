package com.stoiclife.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.stoiclife.app.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView appName;
    private TextView tagline;
    private Button btnCommencer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initialiserVues();
        lancerAnimations();
        configurerBoutonCommencer();
    }

    private void initialiserVues() {
        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.splash_app_name);
        tagline = findViewById(R.id.splash_tagline);
        btnCommencer = findViewById(R.id.btn_commencer);
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
        btnCommencer.startAnimation(textFade);
    }

    private void configurerBoutonCommencer() {
        btnCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirAuthentification();
            }
        });
    }

    private void ouvrirAuthentification() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}