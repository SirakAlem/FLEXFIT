package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.ExerciseAdapter;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.repository.FavoritesRepository;
import it.unimib.flexfit.ui.viewmodel.ExerciseViewModel;
public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerFavorites;
    private ProgressBar progressBar;
    private LinearLayout emptyView;  
    private ExerciseAdapter exerciseAdapter;
    private FavoritesRepository favoritesRepository;
    private ExerciseViewModel exerciseViewModel;
    private com.google.android.material.button.MaterialButton browseButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoritesRepository = FavoritesRepository.getInstance();
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initViews(view);
            setupRecyclerView();
            loadFavorites();
        } catch (Exception e) {
            if (isAdded()) {
                showEmptyView(true);
            }
        }
    }
    private void initViews(View view) {
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        browseButton = view.findViewById(R.id.browse_exercises_button);
        if (browseButton != null) {
            browseButton.setOnClickListener(v -> {
                try {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.categoriesFragment);
                } catch (Exception e) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }
    private void setupRecyclerView() {
        if (recyclerFavorites != null && getContext() != null) {
            recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
            exerciseAdapter = new ExerciseAdapter(this::onExerciseClick, this::onFavoriteClick);
            recyclerFavorites.setAdapter(exerciseAdapter);
        } else {
        }
    }
    private void loadFavorites() {
        if (getContext() == null || !isAdded()) {
            return;
        }
        showLoading(true);
        favoritesRepository.getFavoriteExercisesWithDetails().observe(getViewLifecycleOwner(), favoriteExercises -> {
            if (exerciseAdapter == null) {
                if (recyclerFavorites != null && getContext() != null) {
                    setupRecyclerView();
                }
                if (exerciseAdapter == null) {
                    showLoading(false);
                    showEmptyView(true);
                    return;
                }
            }
            if (favoriteExercises != null && !favoriteExercises.isEmpty()) {
                exerciseAdapter.updateFavoriteExercises(favoriteExercises);
                showLoading(false);
                showEmptyView(false);
            } else {
                showLoading(false);
                showEmptyView(true);
            }
        });
    }
    private void onExerciseClick(Exercise exercise) {
        Bundle bundle = new Bundle();
        bundle.putString("exercise_id", exercise.getId());
        bundle.putString("exercise_name", exercise.getName());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_favorites_to_detail, bundle);
    }
    private void onFavoriteClick(Exercise exercise) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        exerciseViewModel.removeFromFavorites(exercise.getId());
        if (exerciseAdapter != null) {
            exerciseAdapter.removeExercise(exercise);
            if (exerciseAdapter.getItemCount() == 0) {
                showEmptyView(true);
            }
        }
        Toast.makeText(getContext(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
    }
    private void showLoading(boolean show) {
        if (!isAdded() || getView() == null) return;
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerFavorites != null) {
            recyclerFavorites.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void showEmptyView(boolean show) {
        if (!isAdded() || getView() == null) return;
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerFavorites != null) {
            recyclerFavorites.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}