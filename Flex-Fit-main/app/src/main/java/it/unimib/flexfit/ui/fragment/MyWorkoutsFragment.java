package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.WorkoutAdapter;
import it.unimib.flexfit.model.Workout;
import it.unimib.flexfit.ui.viewmodel.WorkoutViewModel;
public class MyWorkoutsFragment extends Fragment implements 
    WorkoutAdapter.OnWorkoutClickListener, WorkoutAdapter.OnWorkoutMenuClickListener {
    private RecyclerView recyclerWorkouts;
    private SearchView searchWorkouts;
    private LinearLayout emptyWorkoutsView;
    private ProgressBar progressBar;
    private FloatingActionButton fabCreateWorkout;
    private MaterialButton btnCreateFirstWorkout;
    private WorkoutAdapter workoutAdapter;
    private WorkoutViewModel workoutViewModel;
    private String currentSearchQuery = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_workouts, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupSearchView();
        setupClickListeners();
        observeData();
    }
    private void initViews(View view) {
        recyclerWorkouts = view.findViewById(R.id.recycler_workouts);
        searchWorkouts = view.findViewById(R.id.search_workouts);
        emptyWorkoutsView = view.findViewById(R.id.empty_workouts_view);
        progressBar = view.findViewById(R.id.progress_bar);
        fabCreateWorkout = view.findViewById(R.id.fab_create_workout);
        btnCreateFirstWorkout = view.findViewById(R.id.btn_create_first_workout);
    }
    private void setupRecyclerView() {
        recyclerWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutAdapter = new WorkoutAdapter(this, this);
        recyclerWorkouts.setAdapter(workoutAdapter);
    }
    private void setupSearchView() {
        searchWorkouts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.trim();
                performSearch();
                return true;
            }
        });
    }
    private void setupClickListeners() {
        fabCreateWorkout.setOnClickListener(v -> navigateToCreateWorkout());
        btnCreateFirstWorkout.setOnClickListener(v -> navigateToCreateWorkout());
    }
    private void observeData() {
        showLoading(true);
        performSearch();
    }
    private void performSearch() {
        if (currentSearchQuery.isEmpty()) {
            workoutViewModel.getUserWorkouts().observe(getViewLifecycleOwner(), this::handleWorkouts);
        } else {
            workoutViewModel.searchUserWorkouts(currentSearchQuery).observe(getViewLifecycleOwner(), this::handleWorkouts);
        }
    }
    private void handleWorkouts(List<Workout> workouts) {
        showLoading(false);
        if (workouts != null && !workouts.isEmpty()) {
            workoutAdapter.updateWorkouts(workouts);
            showEmptyView(false);
        } else {
            showEmptyView(true);
        }
    }
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerWorkouts != null) {
            recyclerWorkouts.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void showEmptyView(boolean show) {
        if (emptyWorkoutsView != null) {
            emptyWorkoutsView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerWorkouts != null) {
            recyclerWorkouts.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (fabCreateWorkout != null) {
            fabCreateWorkout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void navigateToCreateWorkout() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_workouts_to_create_workout);
    }
    @Override
    public void onWorkoutClick(Workout workout) {
        Bundle bundle = new Bundle();
        bundle.putString("workout_id", workout.getId());
        bundle.putString("workout_name", workout.getName());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_workouts_to_workout_detail, bundle);
    }
    @Override
    public void onWorkoutMenuClick(Workout workout, View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.inflate(R.menu.workout_item_menu);
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit_workout) {
                editWorkout(workout);
                return true;
            } else if (itemId == R.id.menu_delete_workout) {
                deleteWorkout(workout);
                return true;
            } else if (itemId == R.id.menu_duplicate_workout) {
                duplicateWorkout(workout);
                return true;
            }
            return false;
        });
        popup.show();
    }
    private void editWorkout(Workout workout) {
        Bundle bundle = new Bundle();
        bundle.putString("workout_id", workout.getId());
        bundle.putString("workout_name", workout.getName());
        bundle.putBoolean("edit_mode", true);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_workouts_to_create_workout, bundle);
    }
    private void deleteWorkout(Workout workout) {
        workoutViewModel.deleteWorkout(workout.getId());
        workoutAdapter.removeWorkout(workout);
    }
    private void duplicateWorkout(Workout workout) {
    }
    @Override
    public void onResume() {
        super.onResume();
        performSearch();
    }
}