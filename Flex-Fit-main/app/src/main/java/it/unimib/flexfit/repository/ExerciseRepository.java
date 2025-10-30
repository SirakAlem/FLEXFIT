package it.unimib.flexfit.repository;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.Executor;
import it.unimib.flexfit.database.ExerciseDao;
import it.unimib.flexfit.database.CategoryDao;
import it.unimib.flexfit.database.FlexFitRoomDatabase;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.model.Category;
import it.unimib.flexfit.service.ExerciseApiService;
import it.unimib.flexfit.util.Constants;
import it.unimib.flexfit.util.ResponseCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ExerciseRepository implements IExerciseRepository {
    private static final String TAG = ExerciseRepository.class.getSimpleName();
    private final ExerciseDao exerciseDao;
    private final CategoryDao categoryDao;
    private final Executor executor;
    private final ExerciseApiService exerciseApiService;
    private final FavoritesRepository favoritesRepository;
    public ExerciseRepository(FlexFitRoomDatabase database) {
        this.exerciseDao = database.exerciseDao();
        this.categoryDao = database.categoryDao();
        this.executor = FlexFitRoomDatabase.databaseWriteExecutor;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.EXERCISES_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.exerciseApiService = retrofit.create(ExerciseApiService.class);
        this.favoritesRepository = FavoritesRepository.getInstance();
    }
    @Override
    public void fetchAllExercises() {
        Call<List<Exercise>> call = exerciseApiService.getAllExercises();
        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Successfully fetched " + response.body().size() + " exercises");
                    insertExercises(response.body());
                } else {
                    Log.e(TAG, "API call failed with code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
            }
        });
    }
    @Override
    public LiveData<List<Exercise>> getAllExercises() {
        return exerciseDao.getAll();
    }
    @Override
    public LiveData<List<Exercise>> getExercisesByCategory(String category) {
        return exerciseDao.getExercisesByCategory(category);
    }
    @Override
    public LiveData<List<Exercise>> getFavoriteExercises() {
        return exerciseDao.getFavoriteExercises();
    }
    @Override
    public LiveData<Exercise> getExerciseById(String exerciseId) {
        return exerciseDao.getExerciseById(exerciseId);
    }
    @Override
    public LiveData<List<Exercise>> getExercisesByLevel(String level) {
        return exerciseDao.getExercisesByLevel(level);
    }
    @Override
    public LiveData<List<Exercise>> getExercisesByEquipment(String equipment) {
        return exerciseDao.getExercisesByEquipment(equipment);
    }
    @Override
    public LiveData<List<Exercise>> searchExercises(String query) {
        return exerciseDao.searchExercises(query);
    }
    @Override
    public void addToFavorites(String exerciseId) {
        LiveData<Exercise> exerciseLiveData = exerciseDao.getExerciseById(exerciseId);
        androidx.lifecycle.Observer<Exercise> observer = new androidx.lifecycle.Observer<Exercise>() {
            @Override
            public void onChanged(Exercise exercise) {
                if (exercise != null) {
                    favoritesRepository.addToFavorites(exercise);
                    updateFavoriteStatus(exerciseId, true);
                    Log.d(TAG, "Added exercise " + exerciseId + " to favorites");
                    exerciseLiveData.removeObserver(this);
                }
            }
        };
        exerciseLiveData.observeForever(observer);
    }
    @Override
    public void removeFromFavorites(String exerciseId) {
        favoritesRepository.removeFromFavorites(exerciseId);
        updateFavoriteStatus(exerciseId, false);
        Log.d(TAG, "Removed exercise " + exerciseId + " from favorites");
    }
    @Override
    public void toggleFavorite(String exerciseId) {
        LiveData<Exercise> exerciseLiveData = exerciseDao.getExerciseById(exerciseId);
        androidx.lifecycle.Observer<Exercise> observer = new androidx.lifecycle.Observer<Exercise>() {
            @Override
            public void onChanged(Exercise exercise) {
                if (exercise != null) {
                    if (exercise.isFavorite()) {
                        favoritesRepository.removeFromFavorites(exerciseId);
                        updateFavoriteStatus(exerciseId, false);
                        Log.d(TAG, "Removed exercise " + exerciseId + " from favorites");
                    } else {
                        favoritesRepository.addToFavorites(exercise);
                        updateFavoriteStatus(exerciseId, true);
                        Log.d(TAG, "Added exercise " + exerciseId + " to favorites");
                    }
                    exerciseLiveData.removeObserver(this);
                }
            }
        };
        exerciseLiveData.observeForever(observer);
    }
    private void insertExercises(List<Exercise> exercises) {
        executor.execute(() -> exerciseDao.insertExercises(exercises));
    }
    private void updateFavoriteStatus(String exerciseId, boolean isFavorite) {
        executor.execute(() -> exerciseDao.updateFavoriteStatus(exerciseId, isFavorite));
    }
    public void insertExercise(Exercise exercise) {
        executor.execute(() -> exerciseDao.insertExercises(exercise));
    }
    public void updateExercise(Exercise exercise) {
        executor.execute(() -> exerciseDao.updateExercise(exercise));
    }
    public void deleteExercise(Exercise exercise) {
        executor.execute(() -> exerciseDao.deleteExercise(exercise));
    }
    public void deleteAllExercises() {
        executor.execute(() -> exerciseDao.deleteAll());
    }
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAll();
    }
    public LiveData<Category> getCategoryById(int categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }
    public void insertCategories(List<Category> categories) {
        executor.execute(() -> categoryDao.insertCategories(categories));
    }
    public void insertCategory(Category category) {
        executor.execute(() -> categoryDao.insertCategories(category));
    }
}