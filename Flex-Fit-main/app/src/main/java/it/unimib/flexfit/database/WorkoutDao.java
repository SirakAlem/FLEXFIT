package it.unimib.flexfit.database;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.model.WorkoutWithExercises;
@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM workout ORDER BY lastModified DESC")
    LiveData<List<Workout>> getAllWorkouts();
    @Query("SELECT * FROM workout WHERE userId = :userId ORDER BY lastModified DESC")
    LiveData<List<Workout>> getWorkoutsByUser(String userId);
    @Query("SELECT * FROM workout WHERE id = :workoutId")
    LiveData<Workout> getWorkoutById(String workoutId);
    @Transaction
    @Query("SELECT * FROM workout WHERE id = :workoutId")
    LiveData<WorkoutWithExercises> getWorkoutWithExercises(String workoutId);
    @Transaction
    @Query("SELECT * FROM workout WHERE userId = :userId ORDER BY lastModified DESC")
    LiveData<List<WorkoutWithExercises>> getWorkoutsWithExercisesByUser(String userId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkout(Workout workout);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkouts(List<Workout> workouts);
    @Update
    void updateWorkout(Workout workout);
    @Delete
    void deleteWorkout(Workout workout);
    @Query("DELETE FROM workout WHERE id = :workoutId")
    void deleteWorkoutById(String workoutId);
    @Query("SELECT * FROM workout_exercise WHERE workoutId = :workoutId ORDER BY orderInWorkout ASC")
    LiveData<List<WorkoutExercise>> getExercisesByWorkout(String workoutId);
    @Query("SELECT * FROM workout_exercise WHERE id = :exerciseId")
    LiveData<WorkoutExercise> getWorkoutExerciseById(String exerciseId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkoutExercise(WorkoutExercise workoutExercise);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkoutExercises(List<WorkoutExercise> workoutExercises);
    @Update
    void updateWorkoutExercise(WorkoutExercise workoutExercise);
    @Delete
    void deleteWorkoutExercise(WorkoutExercise workoutExercise);
    @Query("DELETE FROM workout_exercise WHERE workoutId = :workoutId")
    void deleteAllExercisesFromWorkout(String workoutId);
    @Query("DELETE FROM workout_exercise WHERE id = :exerciseId")
    void deleteWorkoutExerciseById(String exerciseId);
    @Query("SELECT COUNT(*) FROM workout_exercise WHERE workoutId = :workoutId")
    LiveData<Integer> getExerciseCountForWorkout(String workoutId);
    @Query("UPDATE workout SET exerciseCount = (SELECT COUNT(*) FROM workout_exercise WHERE workoutId = :workoutId) WHERE id = :workoutId")
    void updateWorkoutExerciseCount(String workoutId);
    @Query("SELECT MAX(orderInWorkout) FROM workout_exercise WHERE workoutId = :workoutId")
    int getMaxOrderInWorkout(String workoutId);
    @Query("SELECT * FROM workout WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY lastModified DESC")
    LiveData<List<Workout>> searchWorkouts(String query);
    @Query("SELECT * FROM workout WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND userId = :userId ORDER BY lastModified DESC")
    LiveData<List<Workout>> searchWorkoutsByUser(String query, String userId);
}