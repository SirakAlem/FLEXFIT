package it.unimib.flexfit.database;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.model.Category;
import it.unimib.flexfit.model.User;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.model.WorkoutExercise;
import it.unimib.flexfit.util.Constants;
@Database(
    entities = {Exercise.class, Category.class, User.class, Workout.class, WorkoutExercise.class}, 
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
public abstract class FlexFitRoomDatabase extends RoomDatabase {
    public abstract ExerciseDao exerciseDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();
    public abstract WorkoutDao workoutDao();
    private static volatile FlexFitRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static FlexFitRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FlexFitRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FlexFitRoomDatabase.class,
                            Constants.DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}