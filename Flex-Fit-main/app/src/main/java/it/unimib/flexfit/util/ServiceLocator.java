package it.unimib.flexfit.util;
import android.content.Context;
import it.unimib.flexfit.database.FlexFitRoomDatabase;
import it.unimib.flexfit.repository.ExerciseRepository;
import it.unimib.flexfit.repository.IExerciseRepository;
import it.unimib.flexfit.repository.IWorkoutRepository;
import it.unimib.flexfit.repository.WorkoutRepository;
public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;
    private static FlexFitRoomDatabase database;
    private ServiceLocator() {}
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }
    public FlexFitRoomDatabase getDatabase(Context context) {
        if (database == null) {
            database = FlexFitRoomDatabase.getDatabase(context);
        }
        return database;
    }
    public IExerciseRepository getExerciseRepository(Context context) {
        return new ExerciseRepository(getDatabase(context));
    }
    public IWorkoutRepository getWorkoutRepository(Context context) {
        return new WorkoutRepository(getDatabase(context));
    }
}