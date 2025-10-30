package it.unimib.flexfit.ui.viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.content.Context;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.CategoryItem;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.repository.IExerciseRepository;
import it.unimib.flexfit.util.Constants;
import it.unimib.flexfit.util.ServiceLocator;
public class ExerciseViewModel extends AndroidViewModel {
    private final IExerciseRepository exerciseRepository;
    private final MutableLiveData<String> globalSearchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isSearching = new MutableLiveData<>(false);
    private final MediatorLiveData<List<Exercise>> globalSearchResults = new MediatorLiveData<>();
    private final MediatorLiveData<List<CategoryItem>> categoriesWithCounts = new MediatorLiveData<>();
    private final MutableLiveData<String> listSearchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Set<String>> selectedFilters = new MutableLiveData<>(new HashSet<>());
    private final MediatorLiveData<List<Exercise>> filteredExercises = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> hasActiveFilters = new MutableLiveData<>(false);
    private List<Exercise> currentCategoryExercises = new ArrayList<>();
    public ExerciseViewModel(@NonNull Application application) {
        super(application);
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        exerciseRepository = serviceLocator.getExerciseRepository(application);
        fetchAllExercises();
        setupGlobalSearch();
        setupCategoriesWithCounts();
        initializeCategoryItems(application);
        setupFilteredExercises();
    }
    public ExerciseViewModel(IExerciseRepository exerciseRepository) {
        super(null);
        this.exerciseRepository = exerciseRepository;
        if (exerciseRepository != null) {
            setupGlobalSearch();
            setupCategoriesWithCounts();
            setupFilteredExercises();
        }
    }
    public void fetchAllExercises() {
        exerciseRepository.fetchAllExercises();
    }
    public LiveData<List<Exercise>> getAllExercises() {
        return exerciseRepository.getAllExercises();
    }
    public LiveData<List<Exercise>> getExercisesByCategory(String category) {
        return exerciseRepository.getExercisesByCategory(category);
    }
    public LiveData<List<Exercise>> getFavoriteExercises() {
        return exerciseRepository.getFavoriteExercises();
    }
    public LiveData<Exercise> getExerciseById(String exerciseId) {
        return exerciseRepository.getExerciseById(exerciseId);
    }
    public LiveData<List<Exercise>> getExercisesByLevel(String level) {
        return exerciseRepository.getExercisesByLevel(level);
    }
    public LiveData<List<Exercise>> getExercisesByEquipment(String equipment) {
        return exerciseRepository.getExercisesByEquipment(equipment);
    }
    public LiveData<List<Exercise>> searchExercises(String query) {
        return exerciseRepository.searchExercises(query);
    }
    public void addToFavorites(String exerciseId) {
        exerciseRepository.addToFavorites(exerciseId);
    }
    public void removeFromFavorites(String exerciseId) {
        exerciseRepository.removeFromFavorites(exerciseId);
    }
    public void toggleFavorite(Exercise exercise) {
        toggleFavoriteById(exercise.getId());
    }
    public void toggleFavoriteById(String exerciseId) {
        exerciseRepository.toggleFavorite(exerciseId);
    }
    private void setupGlobalSearch() {
        globalSearchResults.addSource(globalSearchQuery, query -> updateGlobalSearchResults());
        globalSearchResults.addSource(getAllExercises(), exercises -> updateGlobalSearchResults());
    }
    private void setupCategoriesWithCounts() {
        categoriesWithCounts.addSource(getAllExercises(), exercises -> {
            List<CategoryItem> currentCategories = categoriesWithCounts.getValue();
            if (currentCategories != null) {
                updateCategoryCountsWithItems(currentCategories, exercises);
            }
        });
    }
    public void setGlobalSearchQuery(String query) {
        String trimmedQuery = query != null ? query.toLowerCase().trim() : "";
        globalSearchQuery.setValue(trimmedQuery);
        isSearching.setValue(!trimmedQuery.isEmpty());
    }
    public LiveData<Boolean> isSearching() {
        return isSearching;
    }
    public LiveData<List<Exercise>> getGlobalSearchResults() {
        return globalSearchResults;
    }
    public LiveData<List<CategoryItem>> getCategoriesWithCounts() {
        return categoriesWithCounts;
    }
    private void updateGlobalSearchResults() {
        String query = globalSearchQuery.getValue();
        List<Exercise> allExercises = getAllExercises().getValue();
        if (query == null || query.isEmpty()) {
            return;
        }
        if (allExercises == null) {
            globalSearchResults.setValue(new ArrayList<>());
            return;
        }
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            boolean matchesSearch = exercise.getName().toLowerCase().contains(query) ||
                (exercise.getPrimaryMuscles() != null &&
                 exercise.getPrimaryMuscles().toString().toLowerCase().contains(query));
            if (matchesSearch) {
                filteredExercises.add(exercise);
            }
        }
        globalSearchResults.setValue(filteredExercises);
    }
    private void setCategoryItems(List<CategoryItem> items) {
        categoriesWithCounts.setValue(new ArrayList<>(items));
        List<Exercise> allExercises = getAllExercises().getValue();
        if (allExercises != null && !allExercises.isEmpty()) {
            updateCategoryCountsWithItems(items, allExercises);
        }
    }
    private void updateCategoryCounts() {
        List<Exercise> allExercises = getAllExercises().getValue();
        List<CategoryItem> currentCategories = categoriesWithCounts.getValue();
        if (currentCategories != null) {
            updateCategoryCountsWithItems(currentCategories, allExercises);
        }
    }
    private void updateCategoryCountsWithItems(List<CategoryItem> categoryItems, List<Exercise> allExercises) {
        if (allExercises == null || categoryItems == null) {
            categoriesWithCounts.setValue(new ArrayList<>());
            return;
        }
        for (CategoryItem category : categoryItems) {
            int count = 0;
            for (Exercise exercise : allExercises) {
                if (category.getId().equals(exercise.getCategory())) {
                    count++;
                }
            }
            category.setExerciseCount(count);
        }
        categoriesWithCounts.setValue(new ArrayList<>(categoryItems));
    }
    private void setupFilteredExercises() {
        filteredExercises.addSource(listSearchQuery, query -> updateFilteredExercises());
        filteredExercises.addSource(selectedFilters, filters -> updateFilteredExercises());
    }
    public void setListSearchQuery(String query) {
        listSearchQuery.setValue(query != null ? query.toLowerCase().trim() : "");
        updateActiveFiltersState();
    }
    public void addFilter(String filter) {
        Set<String> current = selectedFilters.getValue();
        if (current != null) {
            Set<String> newFilters = new HashSet<>(current);
            newFilters.add(filter);
            selectedFilters.setValue(newFilters);
            updateActiveFiltersState();
        }
    }
    public void removeFilter(String filter) {
        Set<String> current = selectedFilters.getValue();
        if (current != null) {
            Set<String> newFilters = new HashSet<>(current);
            newFilters.remove(filter);
            selectedFilters.setValue(newFilters);
            updateActiveFiltersState();
        }
    }
    public void clearAllFilters() {
        listSearchQuery.setValue("");
        selectedFilters.setValue(new HashSet<>());
        updateActiveFiltersState();
    }
    public LiveData<List<Exercise>> getFilteredExercisesByCategory(String categoryId) {
        filteredExercises.addSource(getExercisesByCategory(categoryId), exercises -> {
            currentCategoryExercises = exercises != null ? new ArrayList<>(exercises) : new ArrayList<>();
            updateFilteredExercises();
        });
        return filteredExercises;
    }
    public LiveData<Boolean> hasActiveFilters() {
        return hasActiveFilters;
    }
    private void initializeCategoryItems(Context context) {
        List<CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoryItem(
            Constants.CATEGORY_STRENGTH,
            context.getString(R.string.category_strength),
            context.getString(R.string.category_strength_desc),
            R.drawable.ic_launcher_foreground
        ));
        categoryItems.add(new CategoryItem(
            Constants.CATEGORY_CARDIO,
            context.getString(R.string.category_cardio),
            context.getString(R.string.category_cardio_desc),
            R.drawable.ic_launcher_foreground
        ));
        categoryItems.add(new CategoryItem(
            Constants.CATEGORY_STRETCHING,
            context.getString(R.string.category_stretching),
            context.getString(R.string.category_stretching_desc),
            R.drawable.ic_launcher_foreground
        ));
        categoryItems.add(new CategoryItem(
            Constants.CATEGORY_POWERLIFTING,
            context.getString(R.string.category_powerlifting),
            context.getString(R.string.category_powerlifting_desc),
            R.drawable.ic_launcher_foreground
        ));
        categoryItems.add(new CategoryItem(
            Constants.CATEGORY_STRONGMAN,
            context.getString(R.string.category_strongman),
            context.getString(R.string.category_strongman_desc),
            R.drawable.ic_launcher_foreground
        ));
        categoryItems.add(new CategoryItem(
            Constants.CATEGORY_PLYOMETRICS,
            context.getString(R.string.category_plyometrics),
            context.getString(R.string.category_plyometrics_desc),
            R.drawable.ic_launcher_foreground
        ));
        setCategoryItems(categoryItems);
    }
    private void updateActiveFiltersState() {
        String query = listSearchQuery.getValue();
        Set<String> filters = selectedFilters.getValue();
        boolean hasFilters = (query != null && !query.isEmpty()) || (filters != null && !filters.isEmpty());
        hasActiveFilters.setValue(hasFilters);
    }
    private void updateFilteredExercises() {
        String query = listSearchQuery.getValue();
        Set<String> filters = selectedFilters.getValue();
        if (currentCategoryExercises == null) {
            filteredExercises.setValue(new ArrayList<>());
            return;
        }
        List<Exercise> filtered = new ArrayList<>();
        for (Exercise exercise : currentCategoryExercises) {
            boolean matchesSearch = query == null || query.isEmpty() ||
                exercise.getName().toLowerCase().contains(query) ||
                (exercise.getPrimaryMuscles() != null &&
                 exercise.getPrimaryMuscles().toString().toLowerCase().contains(query));
            boolean matchesFilters = filters == null || filters.isEmpty() || matchesAnyFilter(exercise, filters);
            if (matchesSearch && matchesFilters) {
                filtered.add(exercise);
            }
        }
        filteredExercises.setValue(filtered);
    }
    private boolean matchesAnyFilter(Exercise exercise, Set<String> filters) {
        for (String filter : filters) {
            if (matchesFilter(exercise, filter)) {
                return true;
            }
        }
        return false;
    }
    private boolean matchesFilter(Exercise exercise, String filter) {
        String level = exercise.getLevel() != null ? exercise.getLevel().toLowerCase() : "";
        String equipment = exercise.getEquipment() != null ? exercise.getEquipment().toLowerCase() : "";
        switch (filter) {
            case "beginner":
                return level.contains("beginner");
            case "intermediate":
                return level.contains("intermediate");
            case "expert":
                return level.contains("expert");
            case "body weight":
                return equipment.contains("body weight") || equipment.contains("bodyweight") ||
                       equipment.equals("none") || equipment.isEmpty();
            case "dumbbell":
                return equipment.contains("dumbbell");
            case "barbell":
                return equipment.contains("barbell");
            case "machine":
                return equipment.contains("machine");
            default:
                return false;
        }
    }
}