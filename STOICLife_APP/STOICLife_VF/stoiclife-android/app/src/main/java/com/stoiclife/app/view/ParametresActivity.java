package com.stoiclife.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stoiclife.app.R;
import com.stoiclife.app.utils.SessionManager;

public class ParametresActivity extends AppCompatActivity {

    private MaterialButton btnDeconnecter;
    private TextView btnRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        initialiserVues();
        configurerActions();
    }

    private void initialiserVues() {
        btnRetour = findViewById(R.id.btn_retour_param);
        btnDeconnecter = findViewById(R.id.btn_deconnecter_param);
    }

    private void configurerActions() {
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDeconnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deconnecter();
            }
        });
    }

    private void deconnecter() {
        new SessionManager(this).deconnecter();

        Intent intent = new Intent(ParametresActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finishAffinity();
    }
}