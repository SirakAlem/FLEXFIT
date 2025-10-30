package it.unimib.flexfit.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.repository.FavoritesRepository;
import it.unimib.flexfit.util.ImageUtils;
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises = new ArrayList<>();
    private final OnExerciseClickListener onExerciseClick;
    private final OnFavoriteClickListener onFavoriteClick;
    private final FavoritesRepository favoritesRepository;
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Exercise exercise);
    }
    public ExerciseAdapter(OnExerciseClickListener onExerciseClick, OnFavoriteClickListener onFavoriteClick) {
        this.onExerciseClick = onExerciseClick;
        this.onFavoriteClick = onFavoriteClick;
        this.favoritesRepository = FavoritesRepository.getInstance();
    }
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, onExerciseClick, onFavoriteClick);
    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }
    public void updateExercises(List<Exercise> newExercises) {
        this.exercises.clear();
        if (newExercises != null) {
            int maxItems = Math.min(newExercises.size(), 50);
            List<Exercise> limitedExercises = newExercises.subList(0, maxItems);
            for (Exercise exercise : limitedExercises) {
                exercise.setFavorite(favoritesRepository.isFavorite(exercise.getId()));
            }
            this.exercises.addAll(limitedExercises);
        }
        notifyDataSetChanged();
    }
    public void updateFavoriteExercises(List<Exercise> favoriteExercises) {
        this.exercises.clear();
        if (favoriteExercises != null) {
            for (Exercise exercise : favoriteExercises) {
                exercise.setFavorite(true);
            }
            this.exercises.addAll(favoriteExercises);
        }
        notifyDataSetChanged();
    }
    public void removeExercise(Exercise exerciseToRemove) {
        for (int i = 0; i < exercises.size(); i++) {
            if (exercises.get(i).getId().equals(exerciseToRemove.getId())) {
                exercises.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ImageView exerciseImage;
        private final TextView exerciseName;
        private final TextView exerciseLevel;
        private final TextView exerciseEquipment;
        private final TextView primaryMuscles;
        private final ImageView favoriteButton;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImage = itemView.findViewById(R.id.exercise_image);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseLevel = itemView.findViewById(R.id.exercise_level);
            exerciseEquipment = itemView.findViewById(R.id.exercise_equipment);
            primaryMuscles = itemView.findViewById(R.id.primary_muscles);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
        public void bind(Exercise exercise, OnExerciseClickListener onExerciseClick, OnFavoriteClickListener onFavoriteClick) {
            String imageUrl = ImageUtils.getExerciseImageUrl(exercise.getName(), exercise.getImages());
            if (imageUrl != null) {
                Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.bg_image_placeholder)
                    .override(200, 200) 
                    .centerCrop()
                    .into(exerciseImage);
            } else {
                exerciseImage.setImageResource(R.drawable.bg_image_placeholder);
            }
            exerciseName.setText(exercise.getName());
            if (exercise.getLevel() != null) {
                exerciseLevel.setText(exercise.getLevel());
            } else {
                exerciseLevel.setText(itemView.getContext().getString(R.string.not_specified));
            }
            if (exercise.getEquipment() != null) {
                exerciseEquipment.setText(exercise.getEquipment());
            } else {
                exerciseEquipment.setText(itemView.getContext().getString(R.string.equipment_none));
            }
            if (exercise.getPrimaryMuscles() != null && !exercise.getPrimaryMuscles().isEmpty()) {
                StringBuilder musclesText = new StringBuilder();
                for (int i = 0; i < exercise.getPrimaryMuscles().size(); i++) {
                    if (i > 0) musclesText.append(", ");
                    musclesText.append(exercise.getPrimaryMuscles().get(i));
                }
                primaryMuscles.setText(musclesText.toString());
            } else {
                primaryMuscles.setText(itemView.getContext().getString(R.string.various_muscles));
            }
            favoriteButton.setImageResource(
                exercise.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_heart_outline_black
            );
            itemView.setOnClickListener(v -> onExerciseClick.onExerciseClick(exercise));
            favoriteButton.setOnClickListener(v -> onFavoriteClick.onFavoriteClick(exercise));
        }
    }
}