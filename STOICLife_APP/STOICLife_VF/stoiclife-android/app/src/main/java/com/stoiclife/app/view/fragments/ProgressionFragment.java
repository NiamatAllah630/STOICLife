package com.stoiclife.app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.dao.ProgressionDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Progression;
import com.stoiclife.app.utils.SessionManager;


public class ProgressionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progression, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = new SessionManager(requireContext());
        DatabaseHelper db      = DatabaseHelper.getInstance(requireContext());
        ProgressionDAO progDAO = new ProgressionDAO(db);
        ActiviteDAO    actDAO  = new ActiviteDAO(db);
        int userId             = session.getUserId();

        Progression prog = progDAO.getByUtilisateur(userId);
        int totalActivites = actDAO.compterTerminees(userId);

        // Niveau + XP
        TextView tvNiveau  = view.findViewById(R.id.tv_prog_niveau);
        TextView tvXpVal   = view.findViewById(R.id.tv_prog_xp);
        TextView tvStreak  = view.findViewById(R.id.tv_prog_streak);
        TextView tvTotal   = view.findViewById(R.id.tv_prog_total);
        TextView tvProchain= view.findViewById(R.id.tv_prog_prochain);
        ProgressBar pbXp   = view.findViewById(R.id.pb_prog_xp);

        tvNiveau.setText(calculerNiveau(prog.getPointsXp()));
        tvXpVal.setText(prog.getPointsXp() + " XP");
        tvStreak.setText(prog.getStreakActuel() + " jours");
        tvTotal.setText(String.valueOf(totalActivites));

        int xpManquant = prog.xpPourProchainNiveau();
        tvProchain.setText(xpManquant > 0
            ? xpManquant + " XP pour le niveau suivant"
            : "Niveau maximum atteint !");
        pbXp.setProgress(prog.pourcentageProgression());
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in));
        afficherBadges(view, prog, totalActivites);
        afficherNiveaux(view, prog.getPointsXp());
    }

    private void afficherBadges(View view, Progression prog, int totalActivites) {
        LinearLayout llBadges = view.findViewById(R.id.ll_badges);
        llBadges.removeAllViews();

        String[][] badges = {
            {"🔥", "Première Flamme",  "1ère activité",   String.valueOf(totalActivites >= 1)},
            {"⚡", "Esprit Éveillé",    "20 XP gagnés",    String.valueOf(prog.getPointsXp() >= 20)},
            {"🛡️", "Maître de Soi",     "5 activités",     String.valueOf(totalActivites >= 5)},
            {"❤️", "Cœur Stoïcien",     "100 XP gagnés",   String.valueOf(prog.getPointsXp() >= 100)},
            {"📖", "Philosophe",        "50 activités",    String.valueOf(totalActivites >= 50)},
            {"📅", "Série de 7",        "7 jours consécutifs", String.valueOf(prog.getStreakActuel() >= 7)},
            {"⭐", "Sage Stoïcien",     "300 XP gagnés",   String.valueOf(prog.getPointsXp() >= 300)},
            {"👑", "Maître Absolu",     "600 XP gagnés",   String.valueOf(prog.getPointsXp() >= 600)},
        };

        for (String[] badge : badges) {
            boolean debloque = "true".equals(badge[3]);
            View item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_badge, llBadges, false);
            ((TextView) item.findViewById(R.id.tv_badge_icon)).setText(badge[0]);
            ((TextView) item.findViewById(R.id.tv_badge_nom)).setText(badge[1]);
            ((TextView) item.findViewById(R.id.tv_badge_desc)).setText(badge[2]);
            item.setAlpha(debloque ? 1f : 0.35f);
            llBadges.addView(item);
        }
    }

    private void afficherNiveaux(View view, int xp) {
        String[][] niveaux = {
            {"🌱", "Débutant",       "0 – 99 XP",   String.valueOf(xp >= 0)},
            {"⚡", "Régulier",       "100 – 299 XP", String.valueOf(xp >= 100)},
            {"🔥", "Discipliné",     "300 – 599 XP", String.valueOf(xp >= 300)},
            {"👑", "Maître Stoïque", "600+ XP",      String.valueOf(xp >= 600)},
        };

        LinearLayout llNiveaux = view.findViewById(R.id.ll_niveaux);
        llNiveaux.removeAllViews();
        for (String[] n : niveaux) {
            boolean atteint = "true".equals(n[3]);
            View item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_niveau, llNiveaux, false);
            ((TextView) item.findViewById(R.id.tv_niveau_icon)).setText(n[0]);
            ((TextView) item.findViewById(R.id.tv_niveau_nom)).setText(n[1]);
            ((TextView) item.findViewById(R.id.tv_niveau_range)).setText(n[2]);
            ((TextView) item.findViewById(R.id.tv_niveau_statut)).setText(atteint ? "✓" : "🔒");
            item.setAlpha(atteint ? 1f : 0.45f);
            llNiveaux.addView(item);
        }
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
