package it.unimib.flexfit.model;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(tableName = "workout_exercise",
        foreignKeys = {
                @ForeignKey(entity = Workout.class,
                        parentColumns = "id",
                        childColumns = "workoutId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("workoutId"), @Index("exerciseId")})
public class WorkoutExercise {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String workoutId;
    @NonNull
    private String exerciseId;
    private String exerciseName; 
    private int sets;
    private int reps;
    private double weight; 
    private int restTimeSeconds;
    private int orderInWorkout; 
    private String notes;
    public WorkoutExercise() {
        this.id = "";
        this.workoutId = "";
        this.exerciseId = "";
        this.sets = 1;
        this.reps = 1;
        this.weight = 0.0;
        this.restTimeSeconds = 60;
        this.orderInWorkout = 0;
    }
    @Ignore
    public WorkoutExercise(@NonNull String id, @NonNull String workoutId, @NonNull String exerciseId) {
        this.id = id;
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
        this.sets = 1;
        this.reps = 1;
        this.weight = 0.0;
        this.restTimeSeconds = 60;
        this.orderInWorkout = 0;
    }
    @NonNull
    public String getId() {
        return id;
    }
    public void setId(@NonNull String id) {
        this.id = id;
    }
    @NonNull
    public String getWorkoutId() {
        return workoutId;
    }
    public void setWorkoutId(@NonNull String workoutId) {
        this.workoutId = workoutId;
    }
    @NonNull
    public String getExerciseId() {
        return exerciseId;
    }
    public void setExerciseId(@NonNull String exerciseId) {
        this.exerciseId = exerciseId;
    }
    public String getExerciseName() {
        return exerciseName;
    }
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
    public int getSets() {
        return sets;
    }
    public void setSets(int sets) {
        this.sets = sets;
    }
    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public int getRestTimeSeconds() {
        return restTimeSeconds;
    }
    public void setRestTimeSeconds(int restTimeSeconds) {
        this.restTimeSeconds = restTimeSeconds;
    }
    public int getOrderInWorkout() {
        return orderInWorkout;
    }
    public void setOrderInWorkout(int orderInWorkout) {
        this.orderInWorkout = orderInWorkout;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutExercise that = (WorkoutExercise) o;
        return id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public String toString() {
        return "WorkoutExercise{" +
                "id='" + id + '\'' +
                ", workoutId='" + workoutId + '\'' +
                ", exerciseId='" + exerciseId + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                ", sets=" + sets +
                ", reps=" + reps +
                ", weight=" + weight +
                '}';
    }
}