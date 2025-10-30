package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.ui.viewmodel.ExerciseViewModel;
import it.unimib.flexfit.ui.viewmodel.WorkoutViewModel;
import it.unimib.flexfit.util.Constants;
public class WorkoutSessionFragment extends Fragment {
    private TextView tvExerciseCounter, tvCurrentExerciseName, tvSetsInfo, tvRepsInfo, tvWeightInfo;
    private TextView tvCurrentSet, tvSetStatus, tvRestTimer, tvProgressText;
    private MaterialCardView cardRestTimer;
    private MaterialButton btnPrevious, btnCompleteSet, btnSkip, btnFinishWorkout, btnExerciseDetail;
    private LinearProgressIndicator progressWorkout;
    private ImageView ivExerciseImage;
    private WorkoutViewModel workoutViewModel;
    private ExerciseViewModel exerciseViewModel;
    private String workoutId;
    private List<WorkoutExercise> exercises;
    private int currentExerciseIndex = 0;
    private int currentSet = 1;
    private CountDownTimer restTimer;
    private boolean isResting = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        if (getArguments() != null) {
            workoutId = getArguments().getString("workout_id");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_session, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupClickListeners();
        loadWorkoutExercises();
    }
    private void initViews(View view) {
        tvExerciseCounter = view.findViewById(R.id.tv_exercise_counter);
        tvCurrentExerciseName = view.findViewById(R.id.tv_current_exercise_name);
        tvSetsInfo = view.findViewById(R.id.tv_sets_info);
        tvRepsInfo = view.findViewById(R.id.tv_reps_info);
        tvWeightInfo = view.findViewById(R.id.tv_weight_info);
        tvCurrentSet = view.findViewById(R.id.tv_current_set);
        tvSetStatus = view.findViewById(R.id.tv_set_status);
        tvRestTimer = view.findViewById(R.id.tv_rest_timer);
        tvProgressText = view.findViewById(R.id.tv_progress_text);
        cardRestTimer = view.findViewById(R.id.card_rest_timer);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnCompleteSet = view.findViewById(R.id.btn_complete_set);
        btnSkip = view.findViewById(R.id.btn_skip);
        btnFinishWorkout = view.findViewById(R.id.btn_finish_workout);
        btnExerciseDetail = view.findViewById(R.id.btn_exercise_detail);
        progressWorkout = view.findViewById(R.id.progress_workout);
        ivExerciseImage = view.findViewById(R.id.iv_exercise_image);
    }
    private void setupClickListeners() {
        btnCompleteSet.setOnClickListener(v -> completeCurrentSet());
        btnPrevious.setOnClickListener(v -> goToPreviousExercise());
        btnSkip.setOnClickListener(v -> skipCurrentExercise());
        btnFinishWorkout.setOnClickListener(v -> finishWorkout());
        btnExerciseDetail.setOnClickListener(v -> showExerciseDetail());
    }
    private void loadWorkoutExercises() {
        if (workoutId != null) {
            workoutViewModel.getWorkoutExercises(workoutId).observe(getViewLifecycleOwner(), exerciseList -> {
                if (exerciseList != null && !exerciseList.isEmpty()) {
                    exercises = exerciseList;
                    currentExerciseIndex = 0;
                    currentSet = 1;
                    updateUI();
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_exercises_in_workout), Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).navigateUp();
                }
            });
        }
    }
    private void updateUI() {
        if (exercises == null || exercises.isEmpty() || currentExerciseIndex >= exercises.size()) {
            return;
        }
        WorkoutExercise currentExercise = exercises.get(currentExerciseIndex);
        tvExerciseCounter.setText(getString(R.string.exercise_counter, currentExerciseIndex + 1, exercises.size()));
        tvCurrentExerciseName.setText(currentExercise.getExerciseName());
        tvSetsInfo.setText(getString(R.string.sets_info, currentExercise.getSets()));
        tvRepsInfo.setText(getString(R.string.reps_info, currentExercise.getReps()));
        if (currentExercise.getWeight() > 0) {
            tvWeightInfo.setText(getString(R.string.weight_info, currentExercise.getWeight()));
            tvWeightInfo.setVisibility(View.VISIBLE);
        } else {
            tvWeightInfo.setVisibility(View.GONE);
        }
        tvCurrentSet.setText(getString(R.string.current_set, currentSet, currentExercise.getSets()));
        int totalSets = 0;
        for (WorkoutExercise exercise : exercises) {
            totalSets += exercise.getSets();
        }
        int completedSets = 0;
        for (int i = 0; i < currentExerciseIndex; i++) {
            completedSets += exercises.get(i).getSets();
        }
        completedSets += (currentSet - 1);
        int progressPercentage = totalSets > 0 ? (completedSets * 100 / totalSets) : 0;
        progressWorkout.setProgress(progressPercentage);
        tvProgressText.setText(getString(R.string.overall_progress, progressPercentage));
        btnPrevious.setEnabled(currentExerciseIndex > 0 || currentSet > 1);
        loadExerciseImage(currentExercise.getExerciseId());
    }
    private void loadExerciseImage(String exerciseId) {
        exerciseViewModel.getExerciseById(exerciseId).observe(getViewLifecycleOwner(), exercise -> {
            if (exercise != null && exercise.getImages() != null && !exercise.getImages().isEmpty()) {
                String imageUrl = Constants.EXERCISES_IMAGES_BASE_URL + exercise.getImages().get(0);
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_fitness_center)
                    .error(R.drawable.ic_fitness_center)
                    .into(ivExerciseImage);
                ivExerciseImage.setVisibility(View.VISIBLE);
            } else {
                ivExerciseImage.setVisibility(View.GONE);
            }
        });
    }
    private void completeCurrentSet() {
        if (isResting) {
            cancelRestTimer();
            nextStep();
            return;
        }
        WorkoutExercise currentExercise = exercises.get(currentExerciseIndex);
        if (currentSet < currentExercise.getSets()) {
            currentSet++;
            startRestTimer(currentExercise.getRestTimeSeconds());
        } else {
            nextExercise();
        }
    }
    private void startRestTimer(int seconds) {
        isResting = true;
        cardRestTimer.setVisibility(View.VISIBLE);
        tvSetStatus.setText(getString(R.string.rest_between_sets));
        btnCompleteSet.setText(getString(R.string.skip_rest));
        restTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int remainingSeconds = (int) (millisUntilFinished / 1000);
                int minutes = remainingSeconds / 60;
                int secs = remainingSeconds % 60;
                tvRestTimer.setText(getString(R.string.rest_time_format, minutes, secs));
            }
            @Override
            public void onFinish() {
                nextStep();
            }
        };
        restTimer.start();
    }
    private void cancelRestTimer() {
        if (restTimer != null) {
            restTimer.cancel();
            restTimer = null;
        }
    }
    private void nextStep() {
        cancelRestTimer();
        isResting = false;
        cardRestTimer.setVisibility(View.GONE);
        tvSetStatus.setText(getString(R.string.tap_complete_when_finished));
        btnCompleteSet.setText(getString(R.string.complete_set));
        updateUI();
    }
    private void nextExercise() {
        currentExerciseIndex++;
        currentSet = 1;
        if (currentExerciseIndex >= exercises.size()) {
            showWorkoutCompletedDialog();
        } else {
            updateUI();
        }
    }
    private void goToPreviousExercise() {
        if (currentSet > 1) {
            currentSet--;
        } else if (currentExerciseIndex > 0) {
            currentExerciseIndex--;
            if (currentExerciseIndex < exercises.size()) {
                currentSet = exercises.get(currentExerciseIndex).getSets();
            }
        }
        cancelRestTimer();
        isResting = false;
        cardRestTimer.setVisibility(View.GONE);
        btnCompleteSet.setText(getString(R.string.complete_set));
        updateUI();
    }
    private void skipCurrentExercise() {
        nextExercise();
    }
    private void showExerciseDetail() {
        if (exercises != null && currentExerciseIndex < exercises.size()) {
            WorkoutExercise currentExercise = exercises.get(currentExerciseIndex);
            Bundle bundle = new Bundle();
            bundle.putString("exercise_id", currentExercise.getExerciseId());
            bundle.putString("exercise_name", currentExercise.getExerciseName());
            NavHostFragment.findNavController(this).navigate(R.id.action_workout_session_to_exercise_detail, bundle);
        }
    }
    private void showWorkoutCompletedDialog() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.workout_completed_title))
            .setMessage(getString(R.string.workout_completed_message))
            .setPositiveButton(getString(R.string.finish), (dialog, which) -> {
                NavHostFragment.findNavController(this).popBackStack(R.id.myWorkoutsFragment, false);
            })
            .setCancelable(false)
            .show();
    }
    private void finishWorkout() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_workout_early_title))
            .setMessage(getString(R.string.finish_workout_early_message))
            .setPositiveButton(getString(R.string.yes_finish), (dialog, which) -> {
                NavHostFragment.findNavController(this).popBackStack(R.id.myWorkoutsFragment, false);
            })
            .setNegativeButton(getString(R.string.continue_workout), null)
            .show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelRestTimer();
    }
}