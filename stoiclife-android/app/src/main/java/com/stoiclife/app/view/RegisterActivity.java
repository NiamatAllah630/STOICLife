package com.stoiclife.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.ProgressionDAO;
import com.stoiclife.app.dao.UtilisateurDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Utilisateur;
import com.stoiclife.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNom, etEmail, etPassword, etPasswordConfirm;
    private Button btnRegister;
    private TextView tvLogin;

    private UtilisateurDAO utilisateurDAO;
    private ProgressionDAO progressionDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialiserDonnees();
        initialiserVues();
        configurerActions();
    }

    private void initialiserDonnees() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        utilisateurDAO = new UtilisateurDAO(db);
        progressionDAO = new ProgressionDAO(db);
        sessionManager = new SessionManager(this);
    }

    private void initialiserVues() {
        etNom = findViewById(R.id.et_nom);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);

        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void configurerActions() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenterInscription();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retournerVersConnexion();
            }
        });
    }

    private void retournerVersConnexion() {
        finish();
        overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );
    }

    private void tenterInscription() {
        String nom = getTexteChamp(etNom);
        String email = getTexteChamp(etEmail).toLowerCase();
        String pass = getTexteChamp(etPassword);
        String passConf = getTexteChamp(etPasswordConfirm);

        if (!validerNom(nom)) {
            return;
        }

        if (!validerEmail(email)) {
            return;
        }

        if (!validerMotDePasse(pass, passConf)) {
            return;
        }

        if (utilisateurDAO.emailExiste(email)) {
            etEmail.setError("Cet email est déjà utilisé");
            etEmail.requestFocus();
            return;
        }

        creerCompte(nom, email, pass);
    }

    private String getTexteChamp(TextInputEditText champ) {
        if (champ.getText() != null) {
            return champ.getText().toString().trim();
        }

        return "";
    }

    private boolean validerNom(String nom) {
        if (TextUtils.isEmpty(nom)) {
            etNom.setError("Veuillez entrer votre prénom");
            etNom.requestFocus();
            return false;
        }

        return true;
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

    private boolean validerMotDePasse(String pass, String passConf) {
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Veuillez entrer un mot de passe");
            etPassword.requestFocus();
            return false;
        }

        if (pass.length() < 6) {
            etPassword.setError("Minimum 6 caractères");
            etPassword.requestFocus();
            return false;
        }

        if (!pass.equals(passConf)) {
            etPasswordConfirm.setError("Les mots de passe ne correspondent pas");
            etPasswordConfirm.requestFocus();
            return false;
        }

        return true;
    }

    private void creerCompte(String nom, String email, String pass) {
        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        Utilisateur utilisateur = new Utilisateur(nom, email, pass, today);

        long userId = utilisateurDAO.inserer(utilisateur);

        if (userId > 0) {
            progressionDAO.inserer((int) userId);
            sessionManager.connecter((int) userId, nom);

            Toast.makeText(
                    this,
                    "Bienvenue dans STOICLife, " + nom + " !",
                    Toast.LENGTH_SHORT
            ).show();

            ouvrirAccueil();
        } else {
            Toast.makeText(
                    this,
                    "Erreur lors de l'inscription.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void ouvrirAccueil() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();
    }
}