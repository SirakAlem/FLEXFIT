package it.unimib.flexfit.ui.fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.WorkoutExerciseAdapter;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.ui.viewmodel.WorkoutViewModel;
public class CreateWorkoutFragment extends Fragment implements WorkoutExerciseAdapter.OnExerciseMenuClickListener {
    private TextInputEditText etWorkoutName, etWorkoutDescription;
    private RecyclerView recyclerWorkoutExercises;
    private LinearLayout emptyExercisesView;
    private MaterialButton btnAddExercise, btnSaveWorkout, btnCancel;
    private WorkoutExerciseAdapter exerciseAdapter;
    private WorkoutViewModel workoutViewModel;
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();
    private String workoutId;
    private boolean editMode = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        if (getArguments() != null) {
            workoutId = getArguments().getString("workout_id");
            editMode = getArguments().getBoolean("edit_mode", false);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_workout, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        if (editMode && workoutId != null) {
            loadWorkoutForEditing();
        } else if (workoutId != null) {
            loadWorkoutExercises();
        }
    }
    private void initViews(View view) {
        etWorkoutName = view.findViewById(R.id.et_workout_name);
        etWorkoutDescription = view.findViewById(R.id.et_workout_description);
        recyclerWorkoutExercises = view.findViewById(R.id.recycler_workout_exercises);
        emptyExercisesView = view.findViewById(R.id.empty_exercises_view);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        btnSaveWorkout = view.findViewById(R.id.btn_save_workout);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }
    private void setupRecyclerView() {
        recyclerWorkoutExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new WorkoutExerciseAdapter(this);
        recyclerWorkoutExercises.setAdapter(exerciseAdapter);
    }
    private void setupClickListeners() {
        btnAddExercise.setOnClickListener(v -> navigateToAddExercise());
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
        btnCancel.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }
    private void loadWorkoutForEditing() {
        workoutViewModel.getWorkoutWithExercises(workoutId).observe(getViewLifecycleOwner(), workoutWithExercises -> {
            if (workoutWithExercises != null) {
                etWorkoutName.setText(workoutWithExercises.workout.getName());
                etWorkoutDescription.setText(workoutWithExercises.workout.getDescription());
                workoutExercises = workoutWithExercises.exercises != null ? workoutWithExercises.exercises : new ArrayList<>();
                updateExercisesList();
            }
        });
    }
    private void loadWorkoutExercises() {
        workoutViewModel.getWorkoutExercises(workoutId).observe(getViewLifecycleOwner(), exercises -> {
            if (exercises != null) {
                workoutExercises = exercises;
                updateExercisesList();
            }
        });
    }
    private void navigateToAddExercise() {
        if (workoutId == null || workoutId.isEmpty()) {
            String name = etWorkoutName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.please_enter_workout_name_first), Toast.LENGTH_SHORT).show();
                return;
            }
            workoutId = UUID.randomUUID().toString();
            String description = etWorkoutDescription.getText().toString().trim();
            Workout workout = new Workout(workoutId, name);
            workout.setDescription(description);
            workoutViewModel.createWorkout(workout);
        }
        Bundle bundle = new Bundle();
        bundle.putString("workout_id", workoutId);
        NavHostFragment.findNavController(this).navigate(R.id.action_create_workout_to_add_exercise, bundle);
    }
    private void saveWorkout() {
        String name = etWorkoutName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.please_enter_workout_name), Toast.LENGTH_SHORT).show();
            return;
        }
        String description = etWorkoutDescription.getText().toString().trim();
        if (workoutId != null && !workoutId.isEmpty()) {
            workoutViewModel.getWorkoutById(workoutId).observe(getViewLifecycleOwner(), workout -> {
                if (workout != null) {
                    workout.setName(name);
                    workout.setDescription(description);
                    workoutViewModel.updateWorkout(workout);
                } else {
                    Workout newWorkout = new Workout(workoutId, name);
                    newWorkout.setDescription(description);
                    workoutViewModel.createWorkout(newWorkout);
                }
                NavHostFragment.findNavController(this).navigateUp();
            });
        } else {
            workoutId = UUID.randomUUID().toString();
            Workout workout = new Workout(workoutId, name);
            workout.setDescription(description);
            workoutViewModel.createWorkout(workout);
            NavHostFragment.findNavController(this).navigateUp();
        }
    }
    private void updateExercisesList() {
        if (workoutExercises.isEmpty()) {
            emptyExercisesView.setVisibility(View.VISIBLE);
            recyclerWorkoutExercises.setVisibility(View.GONE);
        } else {
            emptyExercisesView.setVisibility(View.GONE);
            recyclerWorkoutExercises.setVisibility(View.VISIBLE);
            exerciseAdapter.updateExercises(workoutExercises);
        }
    }
    @Override
    public void onEditExercise(WorkoutExercise exercise) {
        showEditExerciseDialog(exercise);
    }
    private void showEditExerciseDialog(WorkoutExercise exercise) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exercise_settings, null);
        TextView tvExerciseName = dialogView.findViewById(R.id.tv_exercise_name);
        TextInputEditText etSets = dialogView.findViewById(R.id.et_sets);
        TextInputEditText etReps = dialogView.findViewById(R.id.et_reps);
        TextInputEditText etWeight = dialogView.findViewById(R.id.et_weight);
        TextInputEditText etRestTime = dialogView.findViewById(R.id.et_rest_time);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btn_add);
        tvExerciseName.setText(exercise.getExerciseName());
        etSets.setText(String.valueOf(exercise.getSets()));
        etReps.setText(String.valueOf(exercise.getReps()));
        etWeight.setText(String.valueOf(exercise.getWeight()));
        etRestTime.setText(String.valueOf(exercise.getRestTimeSeconds()));
        btnAdd.setText(getString(R.string.update_exercise));
        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setView(dialogView)
            .setCancelable(true)
            .create();
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            try {
                int sets = Integer.parseInt(etSets.getText().toString());
                int reps = Integer.parseInt(etReps.getText().toString());
                double weight = Double.parseDouble(etWeight.getText().toString());
                int restTime = Integer.parseInt(etRestTime.getText().toString());
                if (sets <= 0 || reps <= 0 || restTime < 0 || weight < 0) {
                    Toast.makeText(getContext(), 
                        getString(R.string.please_enter_valid_values), 
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                exercise.setSets(sets);
                exercise.setReps(reps);
                exercise.setWeight(weight);
                exercise.setRestTimeSeconds(restTime);
                workoutViewModel.updateWorkoutExercise(exercise);
                dialog.dismiss();
                Toast.makeText(getContext(), 
                    getString(R.string.updated_exercise, exercise.getExerciseName()), 
                    Toast.LENGTH_SHORT).show();
                updateExercisesList();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), 
                    getString(R.string.please_enter_valid_numbers), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    @Override
    public void onDeleteExercise(WorkoutExercise exercise) {
        workoutExercises.remove(exercise);
        if (exercise.getId() != null && !exercise.getId().isEmpty()) {
            workoutViewModel.removeExerciseFromWorkout(exercise.getId());
        }
        updateExercisesList();
    }
    @Override
    public void onExerciseClick(WorkoutExercise exercise) {
        Bundle bundle = new Bundle();
        bundle.putString("exercise_id", exercise.getExerciseId());
        bundle.putString("exercise_name", exercise.getExerciseName());
        NavHostFragment.findNavController(this).navigate(R.id.action_to_exercise_detail, bundle);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (workoutId != null && !workoutId.isEmpty()) {
            loadWorkoutExercises();
        }
    }
}