package it.unimib.flexfit.database;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import it.unimib.flexfit.model.User;
@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE id = :userId")
    LiveData<User> getUserById(String userId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
    @Update
    void updateUser(User user);
    @Delete
    void deleteUser(User user);
    @Query("DELETE FROM user")
    void deleteAll();
}