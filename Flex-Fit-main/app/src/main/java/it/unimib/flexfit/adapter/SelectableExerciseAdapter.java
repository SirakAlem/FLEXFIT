package it.unimib.flexfit.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.util.ImageUtils;
public class SelectableExerciseAdapter extends RecyclerView.Adapter<SelectableExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises = new ArrayList<>();
    private final OnExerciseAddListener onExerciseAddListener;
    public interface OnExerciseAddListener {
        void onAddExercise(Exercise exercise);
    }
    public SelectableExerciseAdapter(OnExerciseAddListener onExerciseAddListener) {
        this.onExerciseAddListener = onExerciseAddListener;
    }
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_selectable, parent, false);
        return new ExerciseViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, onExerciseAddListener);
    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }
    public void updateExercises(List<Exercise> newExercises) {
        this.exercises.clear();
        if (newExercises != null) {
            this.exercises.addAll(newExercises);
        }
        notifyDataSetChanged();
    }
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ImageView exerciseImage;
        private final TextView exerciseName;
        private final TextView exerciseDetails;
        private final MaterialButton btnAdd;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.exercise_image);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseDetails = itemView.findViewById(R.id.exercise_details);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }
        public void bind(Exercise exercise, OnExerciseAddListener onExerciseAddListener) {
            exerciseName.setText(exercise.getName());
            StringBuilder details = new StringBuilder();
            if (exercise.getCategory() != null) {
                details.append(exercise.getCategory());
            }
            if (exercise.getEquipment() != null) {
                if (details.length() > 0) details.append(" • ");
                details.append(exercise.getEquipment());
            }
            if (exercise.getLevel() != null) {
                if (details.length() > 0) details.append(" • ");
                details.append(exercise.getLevel());
            }
            exerciseDetails.setText(details.toString());
            if (exercise.getImages() != null && !exercise.getImages().isEmpty()) {
                String imageUrl = ImageUtils.getExerciseImageUrl(exercise.getName(), exercise.getImages());
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_fitness_center)
                        .error(R.drawable.ic_fitness_center)
                        .override(200, 200)
                        .centerCrop()
                        .into(exerciseImage);
            } else {
                exerciseImage.setImageResource(R.drawable.ic_fitness_center);
            }
            btnAdd.setOnClickListener(v -> {
                if (onExerciseAddListener != null) {
                    onExerciseAddListener.onAddExercise(exercise);
                }
            });
        }
    }
}