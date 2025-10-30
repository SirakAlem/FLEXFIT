package it.unimib.flexfit.model;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "workout")
public class Workout {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String name;
    private String description;
    private long createdAt;
    private long lastModified;
    private String userId; 
    private int exerciseCount;
    public Workout() {
        this.id = "";
        this.name = "";
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.exerciseCount = 0;
    }
    @Ignore
    public Workout(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.exerciseCount = 0;
    }
    @NonNull
    public String getId() {
        return id;
    }
    public void setId(@NonNull String id) {
        this.id = id;
    }
    @NonNull
    public String getName() {
        return name;
    }
    public void setName(@NonNull String name) {
        this.name = name;
        this.lastModified = System.currentTimeMillis();
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
        this.lastModified = System.currentTimeMillis();
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    public long getLastModified() {
        return lastModified;
    }
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getExerciseCount() {
        return exerciseCount;
    }
    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workout workout = (Workout) o;
        return id.equals(workout.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public String toString() {
        return "Workout{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", exerciseCount=" + exerciseCount +
                '}';
    }
}