package it.unimib.flexfit.ui.fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.SelectableExerciseAdapter;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.ui.viewmodel.ExerciseViewModel;
import it.unimib.flexfit.ui.viewmodel.WorkoutViewModel;
public class AddExerciseToWorkoutFragment extends Fragment implements SelectableExerciseAdapter.OnExerciseAddListener {
    private RecyclerView recyclerExercises;
    private SearchView searchExercises;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SelectableExerciseAdapter exerciseAdapter;
    private ExerciseViewModel exerciseViewModel;
    private WorkoutViewModel workoutViewModel;
    private String workoutId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        if (getArguments() != null) {
            workoutId = getArguments().getString("workout_id");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_exercise_to_workout, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupSearchView();
        observeData();
    }
    private void initViews(View view) {
        recyclerExercises = view.findViewById(R.id.recycler_exercises);
        searchExercises = view.findViewById(R.id.search_exercises);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
    }
    private void setupRecyclerView() {
        recyclerExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new SelectableExerciseAdapter(this);
        recyclerExercises.setAdapter(exerciseAdapter);
    }
    private void setupSearchView() {
        searchExercises.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                workoutViewModel.setSearchQuery(newText);
                return true;
            }
        });
    }
    private void observeData() {
        showLoading(true);
        workoutViewModel.getFilteredAvailableExercises(workoutId).observe(getViewLifecycleOwner(), exercises -> {
            showLoading(false);
            handleExercises(exercises);
        });
        workoutViewModel.getValidationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && !result.isValid) {
                android.widget.Toast.makeText(getContext(), result.errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleExercises(List<Exercise> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            exerciseAdapter.updateExercises(exercises);
            showEmptyView(false);
        } else {
            showEmptyView(true);
        }
    }
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerExercises != null) {
            recyclerExercises.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void showEmptyView(boolean show) {
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerExercises != null) {
            recyclerExercises.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    @Override
    public void onAddExercise(Exercise exercise) {
        showExerciseSettingsDialog(exercise);
    }
    private void showExerciseSettingsDialog(Exercise exercise) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exercise_settings, null);
        TextView tvExerciseName = dialogView.findViewById(R.id.tv_exercise_name);
        TextInputEditText etSets = dialogView.findViewById(R.id.et_sets);
        TextInputEditText etReps = dialogView.findViewById(R.id.et_reps);
        TextInputEditText etWeight = dialogView.findViewById(R.id.et_weight);
        TextInputEditText etRestTime = dialogView.findViewById(R.id.et_rest_time);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btn_add);
        tvExerciseName.setText(exercise.getName());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setView(dialogView)
            .setCancelable(true)
            .create();
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            String setsStr = etSets.getText().toString();
            String repsStr = etReps.getText().toString();
            String weightStr = etWeight.getText().toString();
            String restTimeStr = etRestTime.getText().toString();
            workoutViewModel.validateExerciseInput(setsStr, repsStr, weightStr, restTimeStr);
            androidx.lifecycle.Observer<WorkoutViewModel.ValidationResult> validationObserver = new androidx.lifecycle.Observer<WorkoutViewModel.ValidationResult>() {
                @Override
                public void onChanged(WorkoutViewModel.ValidationResult result) {
                    if (result != null && result.isValid) {
                        workoutViewModel.createWorkoutExercise(
                            workoutId,
                            exercise.getId(),
                            exercise.getName(),
                            result.sets,
                            result.reps,
                            result.weight,
                            result.restTime
                        );
                        dialog.dismiss();
                        android.widget.Toast.makeText(getContext(),
                            getString(R.string.added_to_workout, exercise.getName()),
                            android.widget.Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddExerciseToWorkoutFragment.this).navigateUp();
                        workoutViewModel.getValidationResult().removeObserver(this);
                    }
                }
            };
            workoutViewModel.getValidationResult().observeForever(validationObserver);
        });
        dialog.show();
    }
}