package com.stoiclife.app.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.dao.ProgressionDAO;
import com.stoiclife.app.dao.UtilisateurDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Progression;
import com.stoiclife.app.model.Utilisateur;
import com.stoiclife.app.utils.SessionManager;
import com.stoiclife.app.view.CitationsActivity;
import com.stoiclife.app.view.JournalActivity;
import com.stoiclife.app.view.LoginActivity;
import com.stoiclife.app.view.ParametresActivity;
import com.stoiclife.app.view.SuiviEmotionnelActivity;

public class ProfilFragment extends Fragment {

    private TextView tvInitiale, tvNom, tvEmail;
    private TextView tvNiveau, tvXp, tvStreak, tvTotal;

    private View btnSuiviEmotionnel, btnCitations, btnJournal, btnParametres, btnDeconnecter;
    private View btnPartagerProgres;

    private SessionManager session;
    private DatabaseHelper db;
    private ProgressionDAO progressionDAO;
    private ActiviteDAO activiteDAO;
    private UtilisateurDAO utilisateurDAO;

    private int userId;

    private String nomUtilisateur = "Stoïcien";
    private String emailUtilisateur = "Compte STOICLife";
    private int xpUtilisateur = 0;
    private int streakUtilisateur = 0;
    private int totalTermineesUtilisateur = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialiserDonnees();
        initialiserVues(view);
        chargerProfil();
        configurerActions();

        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in));
    }

    private void initialiserDonnees() {
        session = new SessionManager(requireContext());
        db = DatabaseHelper.getInstance(requireContext());

        progressionDAO = new ProgressionDAO(db);
        activiteDAO = new ActiviteDAO(db);
        utilisateurDAO = new UtilisateurDAO(db);

        userId = session.getUserId();
    }

    private void initialiserVues(View view) {
        tvInitiale = view.findViewById(R.id.tv_profil_initiale);
        tvNom = view.findViewById(R.id.tv_profil_nom);
        tvEmail = view.findViewById(R.id.tv_profil_email);

        tvNiveau = view.findViewById(R.id.tv_profil_niveau);
        tvXp = view.findViewById(R.id.tv_profil_xp);
        tvStreak = view.findViewById(R.id.tv_profil_streak);
        tvTotal = view.findViewById(R.id.tv_profil_total);

        btnSuiviEmotionnel = view.findViewById(R.id.btn_suivi_emotionnel);
        btnCitations = view.findViewById(R.id.btn_citations);
        btnJournal = view.findViewById(R.id.btn_journal);
        btnParametres = view.findViewById(R.id.btn_parametres);
        btnDeconnecter = view.findViewById(R.id.btn_deconnecter);

        btnPartagerProgres = view.findViewById(R.id.btn_partager_progres);
    }

    private void chargerProfil() {
        Progression progression = progressionDAO.getByUtilisateur(userId);
        Utilisateur utilisateur = utilisateurDAO.getById(userId);

        String nom = session.getUserNom();
        String email = "Compte STOICLife";

        if (utilisateur != null) {
            if (utilisateur.getNom() != null && !utilisateur.getNom().trim().isEmpty()) {
                nom = utilisateur.getNom();
            }

            if (utilisateur.getEmail() != null && !utilisateur.getEmail().trim().isEmpty()) {
                email = utilisateur.getEmail();
            }
        }

        if (nom == null || nom.trim().isEmpty()) {
            nom = "Stoïcien";
        }

        int xp = 0;
        int streak = 0;

        if (progression != null) {
            xp = progression.getPointsXp();
            streak = progression.getStreakActuel();
        }

        int totalTerminees = activiteDAO.compterTerminees(userId);

        nomUtilisateur = nom;
        emailUtilisateur = email;
        xpUtilisateur = xp;
        streakUtilisateur = streak;
        totalTermineesUtilisateur = totalTerminees;

        tvNom.setText(nomUtilisateur);
        tvEmail.setText(emailUtilisateur);
        tvInitiale.setText(getInitiale(nomUtilisateur));

        tvNiveau.setText(calculerNiveau(xpUtilisateur));
        tvXp.setText(xpUtilisateur + " XP");
        tvStreak.setText(streakUtilisateur + " jours");
        tvTotal.setText(String.valueOf(totalTermineesUtilisateur));
    }

    private void configurerActions() {
        btnSuiviEmotionnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirSuiviEmotionnel();
            }
        });

        btnCitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirCitations();
            }
        });

        btnJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirJournal();
            }
        });

        btnParametres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirParametres();
            }
        });

        btnPartagerProgres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                partagerProgres();
            }
        });

        btnDeconnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deconnecter();
            }
        });
    }

    private void ouvrirSuiviEmotionnel() {
        Intent intent = new Intent(requireContext(), SuiviEmotionnelActivity.class);
        startActivity(intent);
    }

    private void ouvrirCitations() {
        Intent intent = new Intent(requireContext(), CitationsActivity.class);
        startActivity(intent);
    }

    private void ouvrirJournal() {
        Intent intent = new Intent(requireContext(), JournalActivity.class);
        startActivity(intent);
    }

    private void ouvrirParametres() {
        Intent intent = new Intent(requireContext(), ParametresActivity.class);
        startActivity(intent);
    }

    private void partagerProgres() {
        String niveau = calculerNiveau(xpUtilisateur);

        String message =
                "🏛️ Mon progrès STOICLife\n\n" +
                        "Nom : " + nomUtilisateur + "\n" +
                        "Niveau : " + niveau + "\n" +
                        "XP : " + xpUtilisateur + "\n" +
                        "Série actuelle : " + streakUtilisateur + " jours 🔥\n" +
                        "Activités terminées : " + totalTermineesUtilisateur + "\n\n" +
                        "Je progresse chaque jour avec discipline et sérénité.\n" +
                        "#STOICLife";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Mon progrès STOICLife");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(intent, "Partager mon progrès"));
    }

    private void deconnecter() {
        session.deconnecter();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        requireActivity().finish();
    }

    private String getInitiale(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return "S";
        }

        return nom.trim().substring(0, 1).toUpperCase();
    }

    private String calculerNiveau(int xp) {
        if (xp >= 600) {
            return "Maître Stoïque";
        } else if (xp >= 300) {
            return "Discipliné";
        } else if (xp >= 100) {
            return "Régulier";
        } else {
            return "Débutant";
        }
    }
}