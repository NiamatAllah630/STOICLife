package com.stoiclife.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.UtilisateurDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Utilisateur;
import com.stoiclife.app.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private UtilisateurDAO utilisateurDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiserDonnees();
        initialiserVues();
        lancerAnimationEntree();
        configurerActions();
    }

    private void initialiserDonnees() {
        utilisateurDAO = new UtilisateurDAO(DatabaseHelper.getInstance(this));
        sessionManager = new SessionManager(this);
    }

    private void initialiserVues() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void lancerAnimationEntree() {
        View card = findViewById(R.id.login_card);

        if (card != null) {
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(600);
            card.startAnimation(fadeIn);
        }
    }

    private void configurerActions() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenterConnexion();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirInscription();
            }
        });
    }

    private void ouvrirInscription() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);

        overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
    }

    private void tenterConnexion() {
        String email = getTexteChamp(etEmail).toLowerCase();
        String password = getTexteChamp(etPassword);

        if (!validerEmail(email)) {
            return;
        }

        if (!validerPassword(password)) {
            return;
        }

        Utilisateur user = utilisateurDAO.connecter(email, password);

        if (user != null) {
            connecterUtilisateur(user);
        } else {
            afficherErreurConnexion();
        }
    }

    private String getTexteChamp(TextInputEditText champ) {
        if (champ.getText() != null) {
            return champ.getText().toString().trim();
        }

        return "";
    }

    private boolean validerEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Veuillez entrer votre email");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validerPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Veuillez entrer votre mot de passe");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void connecterUtilisateur(Utilisateur user) {
        sessionManager.connecter(user.getId(), user.getNom());

        Toast.makeText(
                this,
                "Bienvenue, " + user.getNom() + " !",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();
    }

    private void afficherErreurConnexion() {
        Toast.makeText(
                this,
                "Email ou mot de passe incorrect.",
                Toast.LENGTH_LONG
        ).show();

        lancerAnimationErreur();
    }

    private void lancerAnimationErreur() {
        TranslateAnimation shake = new TranslateAnimation(-10, 10, 0, 0);

        shake.setDuration(80);
        shake.setRepeatCount(5);
        shake.setRepeatMode(android.view.animation.Animation.REVERSE);

        btnLogin.startAnimation(shake);
    }
}