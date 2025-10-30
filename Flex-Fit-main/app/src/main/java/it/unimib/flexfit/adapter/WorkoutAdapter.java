package it.unimib.flexfit.adapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.Workout;
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts = new ArrayList<>();
    private final OnWorkoutClickListener onWorkoutClickListener;
    private final OnWorkoutMenuClickListener onMenuClickListener;
    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }
    public interface OnWorkoutMenuClickListener {
        void onWorkoutMenuClick(Workout workout, View anchor);
    }
    public WorkoutAdapter(OnWorkoutClickListener onWorkoutClickListener, 
                         OnWorkoutMenuClickListener onMenuClickListener) {
        this.onWorkoutClickListener = onWorkoutClickListener;
        this.onMenuClickListener = onMenuClickListener;
    }
    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout, onWorkoutClickListener, onMenuClickListener);
    }
    @Override
    public int getItemCount() {
        return workouts.size();
    }
    public void updateWorkouts(List<Workout> newWorkouts) {
        this.workouts.clear();
        if (newWorkouts != null) {
            this.workouts.addAll(newWorkouts);
        }
        notifyDataSetChanged();
    }
    public void addWorkout(Workout workout) {
        workouts.add(0, workout);
        notifyItemInserted(0);
    }
    public void removeWorkout(Workout workout) {
        int position = workouts.indexOf(workout);
        if (position >= 0) {
            workouts.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void updateWorkout(Workout workout) {
        int position = -1;
        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getId().equals(workout.getId())) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            workouts.set(position, workout);
            notifyItemChanged(position);
        }
    }
    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final ImageView workoutIcon;
        private final TextView workoutName;
        private final TextView workoutDescription;
        private final TextView exerciseCount;
        private final TextView lastModified;
        private final ImageButton menuButton;
        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutIcon = itemView.findViewById(R.id.workout_icon);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutDescription = itemView.findViewById(R.id.workout_description);
            exerciseCount = itemView.findViewById(R.id.exercise_count);
            lastModified = itemView.findViewById(R.id.last_modified);
            menuButton = itemView.findViewById(R.id.menu_button);
        }
        public void bind(Workout workout, OnWorkoutClickListener onWorkoutClickListener,
                        OnWorkoutMenuClickListener onMenuClickListener) {
            workoutName.setText(workout.getName());
            if (workout.getDescription() != null && !workout.getDescription().trim().isEmpty()) {
                workoutDescription.setText(workout.getDescription());
                workoutDescription.setVisibility(View.VISIBLE);
            } else {
                workoutDescription.setVisibility(View.GONE);
            }
            int count = workout.getExerciseCount();
            if (count == 1) {
                exerciseCount.setText("1 exercise");
            } else {
                exerciseCount.setText(count + " exercises");
            }
            String timeAgo = DateUtils.getRelativeTimeSpanString(
                workout.getLastModified(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString();
            lastModified.setText(timeAgo);
            itemView.setOnClickListener(v -> {
                if (onWorkoutClickListener != null) {
                    onWorkoutClickListener.onWorkoutClick(workout);
                }
            });
            menuButton.setOnClickListener(v -> {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onWorkoutMenuClick(workout, v);
                }
            });
        }
    }
}