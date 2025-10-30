package it.unimib.flexfit.repository;
import androidx.lifecycle.LiveData;
import java.util.List;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.model.WorkoutWithExercises;
public interface IWorkoutRepository {
    LiveData<List<Workout>> getAllWorkouts();
    LiveData<List<Workout>> getUserWorkouts();
    LiveData<Workout> getWorkoutById(String workoutId);
    LiveData<WorkoutWithExercises> getWorkoutWithExercises(String workoutId);
    LiveData<List<WorkoutWithExercises>> getUserWorkoutsWithExercises();
    void createWorkout(Workout workout);
    void updateWorkout(Workout workout);
    void deleteWorkout(String workoutId);
    LiveData<List<WorkoutExercise>> getWorkoutExercises(String workoutId);
    void addExerciseToWorkout(WorkoutExercise workoutExercise);
    void updateWorkoutExercise(WorkoutExercise workoutExercise);
    void removeExerciseFromWorkout(String workoutExerciseId);
    LiveData<List<Workout>> searchWorkouts(String query);
    LiveData<List<Workout>> searchUserWorkouts(String query);
}