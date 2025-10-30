package it.unimib.flexfit.ui.viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.model.WorkoutWithExercises;
import it.unimib.flexfit.repository.IExerciseRepository;
import it.unimib.flexfit.repository.IWorkoutRepository;
import it.unimib.flexfit.util.ServiceLocator;
public class WorkoutViewModel extends AndroidViewModel {
    private final IWorkoutRepository workoutRepository;
    private final IExerciseRepository exerciseRepository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MediatorLiveData<List<Exercise>> filteredAvailableExercises = new MediatorLiveData<>();
    private final MutableLiveData<ValidationResult> validationResult = new MutableLiveData<>();
    private List<Exercise> cachedAllExercises = new ArrayList<>();
    private List<WorkoutExercise> cachedWorkoutExercises = new ArrayList<>();
    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        workoutRepository = serviceLocator.getWorkoutRepository(application);
        exerciseRepository = serviceLocator.getExerciseRepository(application);
        exerciseRepository.fetchAllExercises();
        setupFilteredExercises();
    }
    public WorkoutViewModel(IWorkoutRepository workoutRepository) {
        super(null);
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = null;
    }
    public WorkoutViewModel(IWorkoutRepository workoutRepository, IExerciseRepository exerciseRepository) {
        super(null);
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        setupFilteredExercises();
    }
    public LiveData<List<Workout>> getAllWorkouts() {
        return workoutRepository.getAllWorkouts();
    }
    public LiveData<List<Workout>> getUserWorkouts() {
        return workoutRepository.getUserWorkouts();
    }
    public LiveData<Workout> getWorkoutById(String workoutId) {
        return workoutRepository.getWorkoutById(workoutId);
    }
    public LiveData<WorkoutWithExercises> getWorkoutWithExercises(String workoutId) {
        return workoutRepository.getWorkoutWithExercises(workoutId);
    }
    public LiveData<List<WorkoutWithExercises>> getUserWorkoutsWithExercises() {
        return workoutRepository.getUserWorkoutsWithExercises();
    }
    public void createWorkout(Workout workout) {
        workoutRepository.createWorkout(workout);
    }
    public void updateWorkout(Workout workout) {
        workoutRepository.updateWorkout(workout);
    }
    public void deleteWorkout(String workoutId) {
        workoutRepository.deleteWorkout(workoutId);
    }
    public LiveData<List<WorkoutExercise>> getWorkoutExercises(String workoutId) {
        return workoutRepository.getWorkoutExercises(workoutId);
    }
    public void addExerciseToWorkout(WorkoutExercise workoutExercise) {
        workoutRepository.addExerciseToWorkout(workoutExercise);
    }
    public void updateWorkoutExercise(WorkoutExercise workoutExercise) {
        workoutRepository.updateWorkoutExercise(workoutExercise);
    }
    public void removeExerciseFromWorkout(String workoutExerciseId) {
        workoutRepository.removeExerciseFromWorkout(workoutExerciseId);
    }
    public LiveData<List<Workout>> searchWorkouts(String query) {
        return workoutRepository.searchWorkouts(query);
    }
    public LiveData<List<Workout>> searchUserWorkouts(String query) {
        return workoutRepository.searchUserWorkouts(query);
    }
    public void createWorkoutExercise(String workoutId, String exerciseId, String exerciseName, 
                                     int sets, int reps, double weight, int restTime) {
        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkoutId(workoutId);
        workoutExercise.setExerciseId(exerciseId);
        workoutExercise.setExerciseName(exerciseName);
        workoutExercise.setSets(sets);
        workoutExercise.setReps(reps);
        workoutExercise.setWeight(weight);
        workoutExercise.setRestTimeSeconds(restTime);
        addExerciseToWorkout(workoutExercise);
    }
    public void createWorkout(String name, String description) {
        Workout workout = new Workout();
        workout.setName(name);
        workout.setDescription(description);
        createWorkout(workout);
    }
    private void setupFilteredExercises() {
        if (exerciseRepository == null) return;
        filteredAvailableExercises.addSource(searchQuery, query -> updateFilteredExercises());
        filteredAvailableExercises.addSource(exerciseRepository.getAllExercises(), exercises -> {
            cachedAllExercises = exercises != null ? new ArrayList<>(exercises) : new ArrayList<>();
            updateFilteredExercises();
        });
    }
    public void setSearchQuery(String query) {
        searchQuery.setValue(query != null ? query.trim() : "");
    }
    public LiveData<List<Exercise>> getFilteredAvailableExercises(String workoutId) {
        filteredAvailableExercises.addSource(getWorkoutExercises(workoutId), workoutExercises -> {
            cachedWorkoutExercises = workoutExercises != null ? new ArrayList<>(workoutExercises) : new ArrayList<>();
            updateFilteredExercises();
        });
        return filteredAvailableExercises;
    }
    private void updateFilteredExercises() {
        String query = searchQuery.getValue();
        List<Exercise> exercisesToFilter;
        if (query == null || query.isEmpty()) {
            exercisesToFilter = cachedAllExercises;
        } else {
            exercisesToFilter = new ArrayList<>();
            String queryLower = query.toLowerCase();
            for (Exercise exercise : cachedAllExercises) {
                if (exercise.getName().toLowerCase().contains(queryLower)) {
                    exercisesToFilter.add(exercise);
                }
            }
        }
        List<Exercise> availableExercises = filterAlreadyAddedExercises(exercisesToFilter);
        filteredAvailableExercises.setValue(availableExercises);
    }
    private List<Exercise> filterAlreadyAddedExercises(List<Exercise> allExercises) {
        List<Exercise> availableExercises = new ArrayList<>();
        List<String> existingExerciseIds = new ArrayList<>();
        for (WorkoutExercise exercise : cachedWorkoutExercises) {
            existingExerciseIds.add(exercise.getExerciseId());
        }
        for (Exercise exercise : allExercises) {
            if (!existingExerciseIds.contains(exercise.getId())) {
                availableExercises.add(exercise);
            }
        }
        return availableExercises;
    }
    public void validateExerciseInput(String setsStr, String repsStr, String weightStr, String restTimeStr) {
        ValidationResult result = new ValidationResult();
        try {
            int sets = Integer.parseInt(setsStr);
            int reps = Integer.parseInt(repsStr);
            double weight = Double.parseDouble(weightStr);
            int restTime = Integer.parseInt(restTimeStr);
            if (sets <= 0 || reps <= 0 || restTime < 0 || weight < 0) {
                result.isValid = false;
                result.errorMessage = "Please enter valid values";
            } else {
                result.isValid = true;
                result.sets = sets;
                result.reps = reps;
                result.weight = weight;
                result.restTime = restTime;
            }
        } catch (NumberFormatException e) {
            result.isValid = false;
            result.errorMessage = "Please enter valid numbers";
        }
        validationResult.setValue(result);
    }
    public LiveData<ValidationResult> getValidationResult() {
        return validationResult;
    }
    public static class ValidationResult {
        public boolean isValid;
        public String errorMessage;
        public int sets;
        public int reps;
        public double weight;
        public int restTime;
    }
}