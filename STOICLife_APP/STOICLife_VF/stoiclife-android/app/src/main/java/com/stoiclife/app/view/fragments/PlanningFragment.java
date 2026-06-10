package com.stoiclife.app.view.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.stoiclife.app.R;
import com.stoiclife.app.dao.ActiviteDAO;
import com.stoiclife.app.dao.PlanningDAO;
import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Activite;
import com.stoiclife.app.model.PlanningJour;
import com.stoiclife.app.utils.SessionManager;
import com.stoiclife.app.view.AddPlanningActivity;
import com.stoiclife.app.view.DemarrerActiviteActivity;
import com.stoiclife.app.view.EditActiviteActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanningFragment extends Fragment {

    private EditText etMatin, etJournee, etSoir;
    private String selectedDate;

    private PlanningDAO planningDAO;
    private ActiviteDAO activiteDAO;
    private int userId;

    private LinearLayout btnAddMatin, btnAddJournee, btnAddSoir;
    private LinearLayout llMatinActivites, llJourneeActivites, llSoirActivites;
    private LinearLayout llWeekDays, llActivites;

    private TextView tvProgressPercent, tvProgressText;
    private ProgressBar pbProgressDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
        planningDAO = new PlanningDAO(db);
        activiteDAO = new ActiviteDAO(db);
        userId = new SessionManager(requireContext()).getUserId();

        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvProgressText = view.findViewById(R.id.tv_progress_text);
        pbProgressDay = view.findViewById(R.id.pb_progress_day);

        etMatin = view.findViewById(R.id.et_planning_matin);
        etJournee = view.findViewById(R.id.et_planning_journee);
        etSoir = view.findViewById(R.id.et_planning_soir);

        llWeekDays = view.findViewById(R.id.ll_week_days);
        llActivites = view.findViewById(R.id.ll_planning_activites);

        btnAddMatin = view.findViewById(R.id.btn_add_matin);
        btnAddJournee = view.findViewById(R.id.btn_add_journee);
        btnAddSoir = view.findViewById(R.id.btn_add_soir);

        llMatinActivites = view.findViewById(R.id.ll_matin_activites);
        llJourneeActivites = view.findViewById(R.id.ll_journee_activites);
        llSoirActivites = view.findViewById(R.id.ll_soir_activites);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        configurerPlagesHoraires();
        construireVueSemaine(view);
        chargerJour(selectedDate);

        Button btnSave = view.findViewById(R.id.btn_save_planning);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                sauvegarder();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (selectedDate != null) {
            chargerJour(selectedDate);
        }
    }

    private void configurerPlagesHoraires() {
        etMatin.setFocusable(false);
        etJournee.setFocusable(false);
        etSoir.setFocusable(false);

        btnAddMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirDialog("matin");
            }
        });

        btnAddJournee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirDialog("journee");
            }
        });

        btnAddSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ouvrirDialog("soir");
            }
        });
    }

    private void construireVueSemaine(final View rootView) {
        llWeekDays.removeAllViews();

        Calendar cal = Calendar.getInstance();

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int diff = (dayOfWeek == Calendar.SUNDAY) ? -6 : Calendar.MONDAY - dayOfWeek;
        cal.add(Calendar.DAY_OF_MONTH, diff);

        String[] jours = {"L", "M", "M", "J", "V", "S", "D"};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        for (int i = 0; i < 7; i++) {
            String date = sdf.format(cal.getTime());
            int dayNum = cal.get(Calendar.DAY_OF_MONTH);

            View dayView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_day_week, llWeekDays, false);

            TextView tvLetter = dayView.findViewById(R.id.tv_day_letter);
            TextView tvNum = dayView.findViewById(R.id.tv_day_num);

            tvLetter.setText(jours[i]);
            tvNum.setText(String.valueOf(dayNum));

            if (date.equals(selectedDate)) {
                dayView.setBackgroundResource(R.drawable.bg_day_selected);
                tvLetter.setTextColor(requireContext().getColor(R.color.gold_accent));
                tvNum.setTextColor(requireContext().getColor(android.R.color.white));
            } else if (date.equals(today)) {
                tvLetter.setTextColor(requireContext().getColor(R.color.text_secondary));
                tvNum.setTextColor(requireContext().getColor(R.color.gold_accent));
            } else {
                tvLetter.setTextColor(requireContext().getColor(R.color.text_secondary));
                tvNum.setTextColor(requireContext().getColor(R.color.navy_primary));
            }

            final String finalDate = date;

            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    selectedDate = finalDate;
                    construireVueSemaine(rootView);
                    chargerJour(finalDate);
                }
            });

            llWeekDays.addView(dayView);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void chargerJour(String date) {
        PlanningJour p = planningDAO.getByDate(userId, date);

        etMatin.setText(p != null ? p.getResumeMatin() : "");
        etJournee.setText(p != null ? p.getResumeJournee() : "");
        etSoir.setText(p != null ? p.getResumeSoir() : "");

        List<Activite> activites = activiteDAO.getByDate(userId, date);

        mettreAJourProgressionJour(activites);

        llActivites.removeAllViews();
        llMatinActivites.removeAllViews();
        llJourneeActivites.removeAllViews();
        llSoirActivites.removeAllViews();

        for (int i = 0; i < activites.size(); i++) {
            Activite a = activites.get(i);

            if ("matin".equals(a.getPlageHoraire())) {
                ajouterItemPlanning(llMatinActivites, a);
            } else if ("journee".equals(a.getPlageHoraire())) {
                ajouterItemPlanning(llJourneeActivites, a);
            } else {
                ajouterItemPlanning(llSoirActivites, a);
            }
        }

        afficherEtatVideSiNecessaire();
    }

    private void ajouterItemPlanning(LinearLayout parent, final Activite activite) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardParams);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(1), Color.parseColor("#E8DCC8"));
        card.setBackground(bg);

        TextView tvTitre = new TextView(requireContext());
        tvTitre.setText(activite.getTitre());
        tvTitre.setTextSize(17);
        tvTitre.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitre.setTextColor(requireContext().getColor(R.color.navy_primary));

        TextView tvType = new TextView(requireContext());
        tvType.setText(getIconeType(activite.getTypeActivite()) + " " +
                getTypeLisible(activite.getTypeActivite()) +
                "  •  " + activite.getDureeMinutes() + " min");
        tvType.setTextSize(14);
        tvType.setTextColor(requireContext().getColor(R.color.text_secondary));
        tvType.setPadding(0, dp(6), 0, dp(4));

        TextView tvDescription = new TextView(requireContext());
        tvDescription.setText(activite.getDescription());
        tvDescription.setTextSize(14);
        tvDescription.setTextColor(Color.parseColor("#5F6470"));
        tvDescription.setPadding(0, dp(2), 0, dp(8));

        TextView tvStatut = new TextView(requireContext());
        tvStatut.setTextSize(14);
        tvStatut.setTypeface(Typeface.DEFAULT_BOLD);

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

        card.addView(tvTitre);
        card.addView(tvType);
        card.addView(tvDescription);
        card.addView(tvStatut);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet(activite);
            }
        });

        parent.addView(card);
    }

    private void afficherEtatVideSiNecessaire() {
        if (llMatinActivites.getChildCount() == 0) {
            ajouterMessageVide(llMatinActivites, "Aucune activité prévue le matin");
        }

        if (llJourneeActivites.getChildCount() == 0) {
            ajouterMessageVide(llJourneeActivites, "Aucune activité prévue dans la journée");
        }

        if (llSoirActivites.getChildCount() == 0) {
            ajouterMessageVide(llSoirActivites, "Aucune activité prévue le soir");
        }
    }

    private void ajouterMessageVide(LinearLayout parent, String message) {
        TextView tvVide = new TextView(requireContext());
        tvVide.setText(message);
        tvVide.setTextSize(14);
        tvVide.setTextColor(requireContext().getColor(R.color.text_secondary));
        tvVide.setPadding(dp(12), dp(10), dp(12), dp(10));
        parent.addView(tvVide);
    }

    private void showBottomSheet(final Activite activite) {
        final BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_activite, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvTypeDuree = view.findViewById(R.id.tv_type_duree);
        TextView tvStatut = view.findViewById(R.id.tv_statut);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvConseil = view.findViewById(R.id.tv_conseil);
        TextView tvImpact = view.findViewById(R.id.tv_impact);

        TextView btnClose = view.findViewById(R.id.btn_close);
        TextView btnModifier = view.findViewById(R.id.btn_modifier);
        TextView btnSupprimer = view.findViewById(R.id.btn_supprimer);

        com.google.android.material.button.MaterialButton btnStart =
                view.findViewById(R.id.btn_start);

        tvTitle.setText(activite.getTitre());

        tvTypeDuree.setText(
                getIconeType(activite.getTypeActivite()) + " " +
                        getTypeLisible(activite.getTypeActivite()) +
                        " • " + activite.getDureeMinutes() + " min"
        );

        tvDescription.setText(activite.getDescription());

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

        tvConseil.setText(getCitationAleatoire());

        String titre = activite.getTitre();

        if (titre.contains("Respiration") || titre.contains("Méditation") || titre.contains("Relaxation")) {
            tvImpact.setText("Réduit le stress, calme l’esprit et favorise la relaxation.");
        } else if (titre.contains("Lecture") || titre.contains("Résumé") || titre.contains("Apprendre")) {
            tvImpact.setText("Développe la réflexion, la concentration et la sagesse.");
        } else if (titre.contains("Sport") || titre.contains("Douche") || titre.contains("Routine")) {
            tvImpact.setText("Renforce la discipline, l’énergie et la constance.");
        } else if (titre.contains("Gratitude") || titre.contains("Affirmations")) {
            tvImpact.setText("Améliore l’humeur, la confiance et l’état d’esprit positif.");
        } else {
            tvImpact.setText("Aide à progresser, rester concentré et améliorer ton équilibre.");
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                dialog.dismiss();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                Intent intent = new Intent(getContext(), DemarrerActiviteActivity.class);

                intent.putExtra("id", activite.getId());
                intent.putExtra("titre", activite.getTitre());
                intent.putExtra("type", activite.getTypeActivite());
                intent.putExtra("description", activite.getDescription());
                intent.putExtra("duree", activite.getDureeMinutes());
                intent.putExtra("plage", activite.getPlageHoraire());

                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                Intent intent = new Intent(requireContext(), EditActiviteActivity.class);

                intent.putExtra("id", activite.getId());
                intent.putExtra("titre", activite.getTitre());
                intent.putExtra("type", activite.getTypeActivite());
                intent.putExtra("description", activite.getDescription());
                intent.putExtra("duree", activite.getDureeMinutes());
                intent.putExtra("plage", activite.getPlageHoraire());
                intent.putExtra("date", activite.getDatePlanifiee());
                intent.putExtra("user_id", activite.getUtilisateurId());

                requireActivity().startActivity(intent);
                dialog.dismiss();
            }
        });

        btnSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                activiteDAO.supprimer(activite.getId());
                dialog.dismiss();
                chargerJour(selectedDate);
                Toast.makeText(requireContext(), "Activité supprimée", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private String getCitationAleatoire() {
        List<String[]> citations = activiteDAO.getToutesCitations();

        if (citations == null || citations.isEmpty()) {
            return "Concentre-toi sur ce qui dépend de toi.";
        }

        int index = (int) (Math.random() * citations.size());

        String auteur = citations.get(index)[0];
        String texte = citations.get(index)[1];

        return texte + "\n— " + auteur;
    }

    private void ouvrirDialog(final String plage) {
        Intent intent = new Intent(requireContext(), AddPlanningActivity.class);
        intent.putExtra("plage", plage);
        intent.putExtra("date", selectedDate);
        startActivity(intent);
    }

    private void sauvegarderSansToast() {
        PlanningJour p = new PlanningJour(
                selectedDate,
                etMatin.getText().toString().trim(),
                etJournee.getText().toString().trim(),
                etSoir.getText().toString().trim(),
                userId
        );

        planningDAO.upsert(p);
    }

    private void mettreAJourProgressionJour(List<Activite> activites) {
        int total = activites.size();
        int terminees = 0;

        for (int i = 0; i < activites.size(); i++) {
            Activite a = activites.get(i);

            if (a.isTerminee()) {
                terminees++;
            }
        }

        int pourcentage = 0;

        if (total > 0) {
            pourcentage = (terminees * 100) / total;
        }

        tvProgressPercent.setText(pourcentage + "%");
        tvProgressText.setText(terminees + " / " + total + " activités terminées");
        pbProgressDay.setProgress(pourcentage);
    }

    private void sauvegarder() {
        sauvegarderSansToast();
        Toast.makeText(requireContext(), "Planning enregistré !", Toast.LENGTH_SHORT).show();
    }

    private String getIconeType(String type) {
        if ("mentale".equals(type)) {
            return "🧠";
        } else if ("emotionnelle".equals(type)) {
            return "💛";
        } else {
            return "🎯";
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