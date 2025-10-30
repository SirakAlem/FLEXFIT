package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.ExerciseAdapter;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.ui.viewmodel.ExerciseViewModel;
public class ExerciseListFragment extends Fragment {
    private RecyclerView recyclerExercises;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SearchView searchView;
    private ChipGroup chipGroupFilters;
    private MaterialButton btnClearFilters;
    private ExerciseAdapter exerciseAdapter;
    private ExerciseViewModel exerciseViewModel;
    private String categoryId;
    private String categoryName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        if (getArguments() != null) {
            categoryId = getArguments().getString("category_id");
            categoryName = getArguments().getString("category_name");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        observeData();
    }
    private void initViews(View view) {
        recyclerExercises = view.findViewById(R.id.recycler_exercises);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        searchView = view.findViewById(R.id.search_view);
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        setupSearchView();
        setupFilters();
    }
    private void setupRecyclerView() {
        recyclerExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new ExerciseAdapter(this::onExerciseClick, this::onFavoriteClick);
        recyclerExercises.setAdapter(exerciseAdapter);
    }
    private void observeData() {
        showLoading(true);
        exerciseViewModel.getFilteredExercisesByCategory(categoryId).observe(getViewLifecycleOwner(), exercises -> {
            showLoading(false);
            if (exercises != null && !exercises.isEmpty()) {
                exerciseAdapter.updateExercises(exercises);
                showEmptyView(false);
            } else {
                showEmptyView(true);
            }
        });
        exerciseViewModel.hasActiveFilters().observe(getViewLifecycleOwner(), hasFilters -> {
            btnClearFilters.setVisibility(hasFilters ? View.VISIBLE : View.GONE);
        });
    }
    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                exerciseViewModel.setListSearchQuery(newText);
                return true;
            }
        });
    }
    private void setupFilters() {
        setupChipListener(R.id.chip_beginner, "beginner");
        setupChipListener(R.id.chip_intermediate, "intermediate");
        setupChipListener(R.id.chip_expert, "expert");
        setupChipListener(R.id.chip_no_equipment, "body weight");
        setupChipListener(R.id.chip_dumbbell, "dumbbell");
        setupChipListener(R.id.chip_barbell, "barbell");
        setupChipListener(R.id.chip_machine, "machine");
        btnClearFilters.setOnClickListener(v -> clearAllFilters());
    }
    private void setupChipListener(int chipId, String filterValue) {
        Chip chip = chipGroupFilters.findViewById(chipId);
        if (chip != null) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    exerciseViewModel.addFilter(filterValue);
                } else {
                    exerciseViewModel.removeFilter(filterValue);
                }
            });
        }
    }
    private void clearAllFilters() {
        exerciseViewModel.clearAllFilters();
        searchView.setQuery("", false);
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            View child = chipGroupFilters.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }
    }
    private void onExerciseClick(Exercise exercise) {
        Bundle bundle = new Bundle();
        bundle.putString("exercise_id", exercise.getId());
        bundle.putString("exercise_name", exercise.getName());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_exercises_to_detail, bundle);
    }
    private void onFavoriteClick(Exercise exercise) {
        exerciseViewModel.toggleFavorite(exercise);
    }
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerExercises.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerExercises.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}