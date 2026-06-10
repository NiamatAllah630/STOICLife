package com.stoiclife.app.view.fragments;


import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.stoiclife.app.model.Activite;
import com.stoiclife.app.model.Progression;
import com.stoiclife.app.utils.SessionManager;
import com.stoiclife.app.view.DetailActiviteActivity;
import com.stoiclife.app.view.EditActiviteActivity;
import com.stoiclife.app.view.FocusActivity;
import com.stoiclife.app.view.SuiviEmotionnelActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccueilFragment extends Fragment {

    private static final String[] QUOTES = {
            "« Tu as du pouvoir sur ton esprit, pas sur les événements. »\n— Marc Aurèle",
            "« Ce n'est pas ce qui arrive qui trouble, mais les opinions qu'on en a. »\n— Épictète",
            "« Seul le temps est nôtre. »\n— Sénèque",
            "« L'âme est teinte de la couleur de ses pensées. »\n— Marc Aurèle",
            "« La liberté naît en éliminant le désir, non en le satisfaisant. »\n— Épictète",
            "« Pendant que tu remets, la vie passe. »\n— Sénèque",
            "« Ce qui bloque le chemin devient le chemin. »\n— Marc Aurèle"
    };

    private SessionManager session;
    private ActiviteDAO actDAO;
    private ProgressionDAO progDAO;
    private int userId;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_accueil, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());

        DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
        actDAO = new ActiviteDAO(db);
        progDAO = new ProgressionDAO(db);

        userId = session.getUserId();

        initialiserBoutons(view);
        chargerDashboard();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (rootView != null) {
            chargerDashboard();
        }
    }

    private void initialiserBoutons(View view) {
        Button btnFocus = view.findViewById(R.id.btn_mode_focus);

        btnFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                Intent intent = new Intent(requireContext(), FocusActivity.class);
                startActivity(intent);
            }
        });

        View btnSuivreHumeur = view.findViewById(R.id.btn_suivre_humeur);

        btnSuivreHumeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                Intent intent = new Intent(requireContext(), SuiviEmotionnelActivity.class);
                startActivity(intent);
            }
        });
    }

    private void chargerDashboard() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        TextView tvQuote = rootView.findViewById(R.id.tv_quote_home);
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        tvQuote.setText(QUOTES[dayOfWeek % QUOTES.length]);

        TextView tvGreeting = rootView.findViewById(R.id.tv_greeting);

        String nom = session.getUserNom();
        if (nom == null || nom.trim().isEmpty()) {
            nom = "Stoïcien";
        }

        tvGreeting.setText("Bonjour, " + nom + " !");

        TextView tvDate = rootView.findViewById(R.id.tv_date_home);
        String dateFormatee = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH).format(new Date());

        if (dateFormatee.length() > 0) {
            dateFormatee = dateFormatee.substring(0, 1).toUpperCase() + dateFormatee.substring(1);
        }

        tvDate.setText(dateFormatee);

        Progression prog = progDAO.getByUtilisateur(userId);

        TextView tvNiveau = rootView.findViewById(R.id.tv_niveau);
        TextView tvXp = rootView.findViewById(R.id.tv_xp);
        TextView tvStreak = rootView.findViewById(R.id.tv_streak);
        ProgressBar pbXp = rootView.findViewById(R.id.pb_xp);
        TextView tvCoach = rootView.findViewById(R.id.tv_coach_home);

        int xp = 0;
        int streak = 0;
        int pourcentage = 0;

        if (prog != null) {
            xp = prog.getPointsXp();
            streak = prog.getStreakActuel();
            pourcentage = prog.pourcentageProgression();
        }

        tvNiveau.setText(calculerNiveau(xp));
        tvXp.setText(xp + " XP");
        tvStreak.setText(streak + " jours 🔥");
        pbXp.setProgress(pourcentage);
        tvCoach.setText(genererConseilAccueil(xp, streak));

        List<Activite> activites = actDAO.getByDate(userId, today);

        int terminees = 0;

        for (Activite activite : activites) {
            if (activite.isTerminee()) {
                terminees++;
            }
        }

        TextView tvActivitesToday = rootView.findViewById(R.id.tv_activites_today);
        tvActivitesToday.setText(terminees + "/" + activites.size() + " activités complétées");

        afficherListeActivites(activites);
    }

    private String genererConseilAccueil(int xp, int streak) {
        if (xp < 100) {
            if (streak == 0) {
                return "Commencez par une petite activité aujourd’hui pour lancer votre progression stoïcienne.";
            }

            return "Continuez doucement : chaque petite action renforce votre discipline.";
        }

        if (xp < 300) {
            if (streak == 0) {
                return "Vous avez déjà progressé. Relancez votre série avec une activité mentale simple.";
            }

            return "Vous progressez bien. Gardez votre rythme avec une activité mentale aujourd’hui.";
        }

        if (xp < 600) {
            if (streak == 0) {
                return "Votre niveau est solide. Reprenez votre régularité avec une action courte et utile.";
            }

            return "Votre discipline devient solide. Essayez une activité plus exigeante aujourd’hui.";
        }

        if (streak == 0) {
            return "Excellent niveau. Relancez votre série pour garder votre constance stoïcienne.";
        }

        return "Excellent niveau. Continuez à pratiquer avec constance et sérénité.";
    }

    private void afficherListeActivites(List<Activite> activites) {
        LinearLayout listContainer = rootView.findViewById(R.id.ll_activites_container);
        listContainer.removeAllViews();

        if (activites == null || activites.isEmpty()) {
            TextView emptyView = new TextView(requireContext());
            emptyView.setText("Aucune activité prévue aujourd’hui.");
            emptyView.setTextColor(getResources().getColor(R.color.text_secondary));
            emptyView.setTextSize(15);
            emptyView.setPadding(dp(14), dp(16), dp(14), dp(16));
            listContainer.addView(emptyView);
            return;
        }

        for (int i = 0; i < activites.size(); i++) {
            final Activite activite = activites.get(i);

            View item = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_activite_home, listContainer, false);

            TextView tvIcon = item.findViewById(R.id.tv_act_icon);
            TextView tvTitre = item.findViewById(R.id.tv_act_titre);
            TextView tvType = item.findViewById(R.id.tv_act_type);
            TextView tvStatut = item.findViewById(R.id.tv_act_statut);

            tvTitre.setText(activite.getTitre());

            if ("mentale".equals(activite.getTypeActivite())) {
                tvIcon.setText("🧠");
            } else if ("emotionnelle".equals(activite.getTypeActivite())) {
                tvIcon.setText("💛");
            } else {
                tvIcon.setText("🎯");
            }

            tvType.setText(
                    getTypeLisible(activite.getTypeActivite())
                            + " • "
                            + activite.getDureeMinutes()
                            + " min"
            );

            if (activite.isTerminee()) {
                tvStatut.setText("✓ Terminée");
                tvStatut.setTextColor(requireContext().getColor(android.R.color.holo_green_dark));
            } else if (activite.isEnCours()) {
                tvStatut.setText("⏳ En cours");
                tvStatut.setTextColor(requireContext().getColor(android.R.color.holo_blue_dark));
            } else {
                tvStatut.setText("○ Planifiée");
                tvStatut.setTextColor(requireContext().getColor(R.color.gold_accent));
            }

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClick) {
                    Intent intent = new Intent(requireContext(), DetailActiviteActivity.class);
                    intent.putExtra("id", activite.getId());
                    startActivity(intent);
                }
            });

            listContainer.addView(item);
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

    private String getTypeLisible(String type) {
        if ("mentale".equals(type)) {
            return "Mentale";
        } else if ("emotionnelle".equals(type)) {
            return "Émotionnelle";
        } else {
            return "Discipline";
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}