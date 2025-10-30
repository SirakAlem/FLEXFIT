package it.unimib.flexfit.database;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import it.unimib.flexfit.model.Exercise;
@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    LiveData<List<Exercise>> getAll();
    @Query("SELECT * FROM exercise WHERE category = :category")
    LiveData<List<Exercise>> getExercisesByCategory(String category);
    @Query("SELECT * FROM exercise WHERE is_favorite = 1")
    LiveData<List<Exercise>> getFavoriteExercises();
    @Query("SELECT * FROM exercise WHERE id = :exerciseId")
    LiveData<Exercise> getExerciseById(String exerciseId);
    @Query("SELECT * FROM exercise WHERE level = :level")
    LiveData<List<Exercise>> getExercisesByLevel(String level);
    @Query("SELECT * FROM exercise WHERE equipment = :equipment")
    LiveData<List<Exercise>> getExercisesByEquipment(String equipment);
    @Query("SELECT * FROM exercise WHERE name LIKE '%' || :query || '%'")
    LiveData<List<Exercise>> searchExercises(String query);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExercises(Exercise... exercises);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExercises(List<Exercise> exercisesList);
    @Query("UPDATE exercise SET name = :name, category = :category, equipment = :equipment, " +
           "level = :level, force = :force, mechanic = :mechanic, primary_muscles = :primaryMuscles, " +
           "secondary_muscles = :secondaryMuscles, instructions = :instructions, images = :images " +
           "WHERE id = :id AND is_favorite = 0")
    void updateExerciseDataPreservingFavorites(String id, String name, String category, 
                                              String equipment, String level, String force, 
                                              String mechanic, String primaryMuscles, 
                                              String secondaryMuscles, String instructions, 
                                              String images);
    @Update
    void updateExercise(Exercise exercise);
    @Query("UPDATE exercise SET is_favorite = :isFavorite WHERE id = :exerciseId")
    void updateFavoriteStatus(String exerciseId, boolean isFavorite);
    @Delete
    void deleteExercise(Exercise exercise);
    @Query("DELETE FROM exercise")
    void deleteAll();
    @Query("DELETE FROM exercise WHERE is_favorite = 0")
    void deleteNonFavorites();
}