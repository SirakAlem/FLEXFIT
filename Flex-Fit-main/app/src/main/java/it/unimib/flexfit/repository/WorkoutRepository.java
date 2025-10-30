package it.unimib.flexfit.repository;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import it.unimib.flexfit.database.WorkoutDao;
import it.unimib.flexfit.database.FlexFitRoomDatabase;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.model.WorkoutWithExercises;
public class WorkoutRepository implements IWorkoutRepository {
    private static final String TAG = WorkoutRepository.class.getSimpleName();
    private final WorkoutDao workoutDao;
    private final Executor executor;
    private final FirebaseAuth firebaseAuth;
    public WorkoutRepository(FlexFitRoomDatabase database) {
        this.workoutDao = database.workoutDao();
        this.executor = FlexFitRoomDatabase.databaseWriteExecutor;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : "anonymous";
    }
    @Override
    public LiveData<List<Workout>> getAllWorkouts() {
        return workoutDao.getAllWorkouts();
    }
    @Override
    public LiveData<List<Workout>> getUserWorkouts() {
        String userId = getCurrentUserId();
        Log.d(TAG, "Getting workouts for user: " + userId);
        return workoutDao.getWorkoutsByUser(userId);
    }
    @Override
    public LiveData<Workout> getWorkoutById(String workoutId) {
        return workoutDao.getWorkoutById(workoutId);
    }
    @Override
    public LiveData<WorkoutWithExercises> getWorkoutWithExercises(String workoutId) {
        return workoutDao.getWorkoutWithExercises(workoutId);
    }
    @Override
    public LiveData<List<WorkoutWithExercises>> getUserWorkoutsWithExercises() {
        String userId = getCurrentUserId();
        return workoutDao.getWorkoutsWithExercisesByUser(userId);
    }
    @Override
    public void createWorkout(Workout workout) {
        if (workout.getId() == null || workout.getId().isEmpty()) {
            workout.setId(UUID.randomUUID().toString());
        }
        workout.setUserId(getCurrentUserId());
        Log.d(TAG, "Creating workout: " + workout.getName() + " for user: " + workout.getUserId());
        executor.execute(() -> workoutDao.insertWorkout(workout));
    }
    @Override
    public void updateWorkout(Workout workout) {
        workout.setUserId(getCurrentUserId());
        workout.setLastModified(System.currentTimeMillis());
        Log.d(TAG, "Updating workout: " + workout.getName());
        executor.execute(() -> workoutDao.updateWorkout(workout));
    }
    @Override
    public void deleteWorkout(String workoutId) {
        Log.d(TAG, "Deleting workout: " + workoutId);
        executor.execute(() -> workoutDao.deleteWorkoutById(workoutId));
    }
    @Override
    public LiveData<List<WorkoutExercise>> getWorkoutExercises(String workoutId) {
        return workoutDao.getExercisesByWorkout(workoutId);
    }
    @Override
    public void addExerciseToWorkout(WorkoutExercise workoutExercise) {
        if (workoutExercise.getId() == null || workoutExercise.getId().isEmpty()) {
            workoutExercise.setId(UUID.randomUUID().toString());
        }
        if (workoutExercise.getOrderInWorkout() == 0) {
            executor.execute(() -> {
                int maxOrder = workoutDao.getMaxOrderInWorkout(workoutExercise.getWorkoutId());
                workoutExercise.setOrderInWorkout(maxOrder + 1);
                workoutDao.insertWorkoutExercise(workoutExercise);
                workoutDao.updateWorkoutExerciseCount(workoutExercise.getWorkoutId());
            });
        } else {
            executor.execute(() -> {
                workoutDao.insertWorkoutExercise(workoutExercise);
                workoutDao.updateWorkoutExerciseCount(workoutExercise.getWorkoutId());
            });
        }
        Log.d(TAG, "Adding exercise to workout: " + workoutExercise.getExerciseName());
    }
    @Override
    public void updateWorkoutExercise(WorkoutExercise workoutExercise) {
        Log.d(TAG, "Updating workout exercise: " + workoutExercise.getExerciseName());
        executor.execute(() -> workoutDao.updateWorkoutExercise(workoutExercise));
    }
    @Override
    public void removeExerciseFromWorkout(String workoutExerciseId) {
        Log.d(TAG, "Removing exercise from workout: " + workoutExerciseId);
        executor.execute(() -> workoutDao.deleteWorkoutExerciseById(workoutExerciseId));
    }
    @Override
    public LiveData<List<Workout>> searchWorkouts(String query) {
        return workoutDao.searchWorkouts(query);
    }
    @Override
    public LiveData<List<Workout>> searchUserWorkouts(String query) {
        String userId = getCurrentUserId();
        return workoutDao.searchWorkoutsByUser(query, userId);
    }
    public void insertWorkouts(List<Workout> workouts) {
        executor.execute(() -> workoutDao.insertWorkouts(workouts));
    }
    public LiveData<Integer> getExerciseCountForWorkout(String workoutId) {
        return workoutDao.getExerciseCountForWorkout(workoutId);
    }
    public LiveData<WorkoutExercise> getWorkoutExerciseById(String exerciseId) {
        return workoutDao.getWorkoutExerciseById(exerciseId);
    }
}