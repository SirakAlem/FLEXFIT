package it.unimib.flexfit.database;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import it.unimib.flexfit.model.Category;
@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    LiveData<List<Category>> getAll();
    @Query("SELECT * FROM category WHERE id = :categoryId")
    LiveData<Category> getCategoryById(int categoryId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(Category... categories);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> categoriesList);
    @Update
    void updateCategory(Category category);
    @Delete
    void deleteCategory(Category category);
    @Query("DELETE FROM category")
    void deleteAll();
}