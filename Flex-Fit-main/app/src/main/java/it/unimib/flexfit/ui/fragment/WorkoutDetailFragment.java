package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.WorkoutExerciseAdapter;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.model.WorkoutWithExercises;
import it.unimib.flexfit.ui.viewmodel.WorkoutViewModel;
public class WorkoutDetailFragment extends Fragment implements WorkoutExerciseAdapter.OnExerciseMenuClickListener {
    private TextView workoutName, workoutDescription, workoutStats, emptyView;
    private RecyclerView recyclerExercises;
    private MaterialButton btnStartWorkout;
    private ProgressBar progressBar;
    private WorkoutExerciseAdapter exerciseAdapter;
    private WorkoutViewModel workoutViewModel;
    private String workoutId;
    private String workoutNameString;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        if (getArguments() != null) {
            workoutId = getArguments().getString("workout_id");
            workoutNameString = getArguments().getString("workout_name");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeData();
    }
    private void initViews(View view) {
        workoutName = view.findViewById(R.id.workout_name);
        workoutDescription = view.findViewById(R.id.workout_description);
        workoutStats = view.findViewById(R.id.workout_stats);
        recyclerExercises = view.findViewById(R.id.recycler_exercises);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
    }
    private void setupRecyclerView() {
        recyclerExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new WorkoutExerciseAdapter(this);
        recyclerExercises.setAdapter(exerciseAdapter);
    }
    private void setupClickListeners() {
        btnStartWorkout.setOnClickListener(v -> startWorkout());
    }
    private void observeData() {
        showLoading(true);
        workoutViewModel.getWorkoutWithExercises(workoutId).observe(getViewLifecycleOwner(), workoutWithExercises -> {
            showLoading(false);
            if (workoutWithExercises != null) {
                populateWorkoutData(workoutWithExercises);
            }
        });
    }
    private void populateWorkoutData(WorkoutWithExercises workoutWithExercises) {
        workoutName.setText(workoutWithExercises.workout.getName());
        if (workoutWithExercises.workout.getDescription() != null && 
            !workoutWithExercises.workout.getDescription().trim().isEmpty()) {
            workoutDescription.setText(workoutWithExercises.workout.getDescription());
            workoutDescription.setVisibility(View.VISIBLE);
        } else {
            workoutDescription.setVisibility(View.GONE);
        }
        int exerciseCount = workoutWithExercises.exercises != null ? workoutWithExercises.exercises.size() : 0;
        String timeAgo = DateUtils.getRelativeTimeSpanString(
            workoutWithExercises.workout.getCreatedAt(),
            System.currentTimeMillis(),
            DateUtils.DAY_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString();
        String stats = exerciseCount + (exerciseCount == 1 ? " exercise" : " exercises") + 
                      " • Created " + timeAgo;
        workoutStats.setText(stats);
        handleExercises(workoutWithExercises.exercises);
    }
    private void handleExercises(List<WorkoutExercise> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            exerciseAdapter.updateExercises(exercises);
            recyclerExercises.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            btnStartWorkout.setEnabled(true);
        } else {
            recyclerExercises.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            btnStartWorkout.setEnabled(false);
        }
    }
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    private void startWorkout() {
        Bundle bundle = new Bundle();
        bundle.putString("workout_id", workoutId);
        NavHostFragment.findNavController(this).navigate(R.id.action_workout_detail_to_session, bundle);
    }
    @Override
    public void onEditExercise(WorkoutExercise exercise) {
        Toast.makeText(getContext(), getString(R.string.edit_exercise_toast, exercise.getExerciseName()), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDeleteExercise(WorkoutExercise exercise) {
        workoutViewModel.removeExerciseFromWorkout(exercise.getId());
    }
    @Override
    public void onExerciseClick(WorkoutExercise exercise) {
        Bundle bundle = new Bundle();
        bundle.putString("exercise_id", exercise.getExerciseId());
        bundle.putString("exercise_name", exercise.getExerciseName());
        NavHostFragment.findNavController(this).navigate(R.id.action_workout_detail_to_exercise_detail, bundle);
    }
}