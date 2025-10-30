package it.unimib.flexfit.repository;
import androidx.lifecycle.LiveData;
import java.util.List;
import it.unimib.flexfit.model.Exercise;
public interface IExerciseRepository {
    void fetchAllExercises();
    LiveData<List<Exercise>> getAllExercises();
    LiveData<List<Exercise>> getExercisesByCategory(String category);
    LiveData<List<Exercise>> getFavoriteExercises();
    LiveData<Exercise> getExerciseById(String exerciseId);
    LiveData<List<Exercise>> getExercisesByLevel(String level);
    LiveData<List<Exercise>> getExercisesByEquipment(String equipment);
    LiveData<List<Exercise>> searchExercises(String query);
    void addToFavorites(String exerciseId);
    void removeFromFavorites(String exerciseId);
    void toggleFavorite(String exerciseId);
}