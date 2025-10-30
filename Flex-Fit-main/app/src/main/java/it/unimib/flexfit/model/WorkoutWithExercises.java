package it.unimib.flexfit.model;
import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;
public class WorkoutWithExercises {
    @Embedded
    public Workout workout;
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    public List<WorkoutExercise> exercises;
    public WorkoutWithExercises() {}
    public Workout getWorkout() {
        return workout;
    }
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }
    public List<WorkoutExercise> getExercises() {
        return exercises;
    }
    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }
    @Override
    public String toString() {
        return "WorkoutWithExercises{" +
                "workout=" + workout +
                ", exercises=" + (exercises != null ? exercises.size() : 0) + " exercises" +
                '}';
    }
}