package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.CategoryAdapter;
import it.unimib.flexfit.adapter.ExerciseAdapter;
import it.unimib.flexfit.model.CategoryItem;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.ui.viewmodel.ExerciseViewModel;
import it.unimib.flexfit.util.Constants;
public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerCategories;
    private RecyclerView recyclerSearchResults;
    private SearchView globalSearchView;
    private TextView emptySearchView;
    private ImageView logoutIcon;
    private CategoryAdapter categoryAdapter;
    private ExerciseAdapter searchResultsAdapter;
    private ExerciseViewModel exerciseViewModel;
    private List<CategoryItem> categoryItems;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        setupGlobalSearch(view);
        setupLogout(view);
        observeData();
    }
    private void setupRecyclerView(View view) {
        recyclerCategories = view.findViewById(R.id.recycler_categories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this::onCategoryClick);
        recyclerCategories.setAdapter(categoryAdapter);
        recyclerSearchResults = view.findViewById(R.id.recycler_search_results);
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsAdapter = new ExerciseAdapter(this::onExerciseClick, this::onFavoriteClick);
        recyclerSearchResults.setAdapter(searchResultsAdapter);
    }
    private void setupGlobalSearch(View view) {
        globalSearchView = view.findViewById(R.id.global_search_view);
        emptySearchView = view.findViewById(R.id.empty_search_view);
        globalSearchView.setIconifiedByDefault(false);
        globalSearchView.setIconified(false);
        globalSearchView.setFocusable(true);
        globalSearchView.setFocusableInTouchMode(true);
        globalSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                exerciseViewModel.setGlobalSearchQuery(newText);
                return true;
            }
        });
        globalSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                exerciseViewModel.setGlobalSearchQuery("");
            }
        });
    }
    private void setupLogout(View view) {
        logoutIcon = view.findViewById(R.id.logout_icon);
        logoutIcon.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
    private void showCategories() {
        recyclerCategories.setVisibility(View.VISIBLE);
        recyclerSearchResults.setVisibility(View.GONE);
        emptySearchView.setVisibility(View.GONE);
    }
    private void showSearchResults() {
        recyclerCategories.setVisibility(View.GONE);
    }
    private void observeData() {
        exerciseViewModel.getCategoriesWithCounts().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryAdapter.updateCategories(categories);
            }
        });
        exerciseViewModel.isSearching().observe(getViewLifecycleOwner(), isSearching -> {
            if (isSearching) {
                showSearchResults();
            } else {
                showCategories();
            }
        });
        exerciseViewModel.getGlobalSearchResults().observe(getViewLifecycleOwner(), exercises -> {
            if (exercises != null) {
                if (exercises.isEmpty()) {
                    recyclerSearchResults.setVisibility(View.GONE);
                    emptySearchView.setVisibility(View.VISIBLE);
                } else {
                    searchResultsAdapter.updateExercises(exercises);
                    recyclerSearchResults.setVisibility(View.VISIBLE);
                    emptySearchView.setVisibility(View.GONE);
                }
            }
        });
    }
    private void onCategoryClick(CategoryItem category) {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", category.getId());
        bundle.putString("category_name", category.getName());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_categories_to_exercises, bundle);
    }
    private void onExerciseClick(Exercise exercise) {
        Bundle bundle = new Bundle();
        bundle.putString("exercise_id", exercise.getId());
        bundle.putString("exercise_name", exercise.getName());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_categories_to_detail, bundle);
    }
    private void onFavoriteClick(Exercise exercise) {
        exerciseViewModel.toggleFavorite(exercise);
    }
}