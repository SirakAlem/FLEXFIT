package it.unimib.flexfit.service;
import java.util.List;
import it.unimib.flexfit.model.Exercise;
import retrofit2.Call;
import retrofit2.http.GET;
public interface ExerciseApiService {
    @GET("yuhonas/free-exercise-db/main/dist/exercises.json")
    Call<List<Exercise>> getAllExercises();
}