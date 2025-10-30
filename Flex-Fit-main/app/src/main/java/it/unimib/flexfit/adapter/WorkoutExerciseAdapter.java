package it.unimib.flexfit.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.WorkoutExercise;
public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.WorkoutExerciseViewHolder> {
    private List<WorkoutExercise> exercises = new ArrayList<>();
    private final OnExerciseMenuClickListener menuClickListener;
    public interface OnExerciseMenuClickListener {
        void onEditExercise(WorkoutExercise exercise);
        void onDeleteExercise(WorkoutExercise exercise);
        void onExerciseClick(WorkoutExercise exercise);
    }
    public WorkoutExerciseAdapter(OnExerciseMenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }
    @NonNull
    @Override
    public WorkoutExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new WorkoutExerciseViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WorkoutExerciseViewHolder holder, int position) {
        WorkoutExercise exercise = exercises.get(position);
        holder.bind(exercise, menuClickListener);
    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }
    public void updateExercises(List<WorkoutExercise> newExercises) {
        this.exercises.clear();
        if (newExercises != null) {
            this.exercises.addAll(newExercises);
        }
        notifyDataSetChanged();
    }
    static class WorkoutExerciseViewHolder extends RecyclerView.ViewHolder {
        private final TextView exerciseName;
        private final TextView setsRepsWeight;
        private final TextView weightInfo;
        private final TextView restTime;
        private final ImageButton menuExercise;
        public WorkoutExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            setsRepsWeight = itemView.findViewById(R.id.sets_reps_weight);
            weightInfo = itemView.findViewById(R.id.weight_info);
            restTime = itemView.findViewById(R.id.rest_time);
            menuExercise = itemView.findViewById(R.id.menu_exercise);
        }
        public void bind(WorkoutExercise exercise, OnExerciseMenuClickListener menuClickListener) {
            exerciseName.setText(exercise.getExerciseName());
            setsRepsWeight.setText(exercise.getSets() + " × " + exercise.getReps());
            if (exercise.getWeight() > 0) {
                weightInfo.setText(exercise.getWeight() + " kg");
                weightInfo.setVisibility(View.VISIBLE);
            } else {
                weightInfo.setVisibility(View.GONE);
            }
            restTime.setText(itemView.getContext().getString(R.string.rest_seconds, exercise.getRestTimeSeconds()));
            itemView.setOnClickListener(v -> menuClickListener.onExerciseClick(exercise));
            menuExercise.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.workout_exercise_menu);
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit_exercise) {
                        menuClickListener.onEditExercise(exercise);
                        return true;
                    } else if (itemId == R.id.menu_delete_exercise) {
                        menuClickListener.onDeleteExercise(exercise);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}