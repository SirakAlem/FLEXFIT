package it.unimib.flexfit.repository;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import it.unimib.flexfit.model.Exercise;
public class FavoritesRepository {
    private static final String TAG = "FavoritesRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_FAVORITES = "favorites";
    private static final boolean FIREBASE_ENABLED = true;
    private static FavoritesRepository instance;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Set<String>> favoritesLiveData;
    private final MutableLiveData<Boolean> isLoadingLiveData;
    private FavoritesRepository() {
        FirebaseFirestore tempFirestore = null;
        FirebaseAuth tempAuth = null;
        try {
            if (FIREBASE_ENABLED) {
                tempFirestore = FirebaseFirestore.getInstance();
                tempAuth = FirebaseAuth.getInstance();
            }
        } catch (Exception e) {
            Log.w(TAG, "Firebase not available, using offline mode", e);
        }
        this.firestore = tempFirestore;
        this.firebaseAuth = tempAuth;
        this.favoritesLiveData = new MutableLiveData<>(new HashSet<>());
        this.isLoadingLiveData = new MutableLiveData<>(false);
    }
    public static synchronized FavoritesRepository getInstance() {
        if (instance == null) {
            instance = new FavoritesRepository();
        }
        return instance;
    }
    public LiveData<Set<String>> getFavoritesLiveData() {
        if (FIREBASE_ENABLED && firestore != null && firebaseAuth != null) {
            loadUserFavorites();
        } else {
            Log.d(TAG, "Firebase disabled, using local favorites only");
        }
        return favoritesLiveData;
    }
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }
    public boolean isFavorite(String exerciseId) {
        Set<String> favorites = favoritesLiveData.getValue();
        return favorites != null && favorites.contains(exerciseId);
    }
    public void addToFavorites(@NonNull Exercise exercise) {
        if (!FIREBASE_ENABLED || firestore == null || firebaseAuth == null) {
            Set<String> currentFavorites = new HashSet<>(favoritesLiveData.getValue());
            currentFavorites.add(exercise.getId());
            favoritesLiveData.setValue(currentFavorites);
            Log.d(TAG, "Added to local favorites: " + exercise.getName());
            return;
        }
        FirebaseUser currentUser;
        try {
            currentUser = firebaseAuth.getCurrentUser();
        } catch (Exception e) {
            Log.w(TAG, "Firebase auth error, using local storage", e);
            addToLocalFavorites(exercise);
            return;
        }
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, using local storage");
            addToLocalFavorites(exercise);
            return;
        }
        try {
            isLoadingLiveData.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "Error setting loading state", e);
        }
        DocumentReference favoriteRef = firestore
                .collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_FAVORITES)
                .document(exercise.getId());
        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("exerciseId", exercise.getId());
        favoriteData.put("name", exercise.getName());
        favoriteData.put("category", exercise.getCategory());
        favoriteData.put("primaryMuscles", exercise.getPrimaryMuscles());
        favoriteData.put("equipment", exercise.getEquipment());
        favoriteData.put("images", exercise.getImages());
        favoriteData.put("instructions", exercise.getInstructions());
        favoriteData.put("addedAt", System.currentTimeMillis());
        try {
            favoriteRef.set(favoriteData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Exercise added to favorites: " + exercise.getName());
                        try {
                            Set<String> currentFavorites = new HashSet<>(favoritesLiveData.getValue());
                            currentFavorites.add(exercise.getId());
                            favoritesLiveData.setValue(currentFavorites);
                            isLoadingLiveData.setValue(false);
                        } catch (Exception ex) {
                            Log.e(TAG, "Error updating favorites state", ex);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding to favorites", e);
                        try {
                            isLoadingLiveData.setValue(false);
                        } catch (Exception ex) {
                            Log.e(TAG, "Error setting loading state", ex);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Firebase connection error", e);
            try {
                isLoadingLiveData.setValue(false);
            } catch (Exception ex) {
                Log.e(TAG, "Error setting loading state", ex);
            }
        }
    }
    public void removeFromFavorites(String exerciseId) {
        if (!FIREBASE_ENABLED || firestore == null || firebaseAuth == null) {
            removeFromLocalFavorites(exerciseId);
            return;
        }
        FirebaseUser currentUser;
        try {
            currentUser = firebaseAuth.getCurrentUser();
        } catch (Exception e) {
            Log.w(TAG, "Firebase auth error, using local storage", e);
            removeFromLocalFavorites(exerciseId);
            return;
        }
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, using local storage");
            removeFromLocalFavorites(exerciseId);
            return;
        }
        isLoadingLiveData.setValue(true);
        firestore.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_FAVORITES)
                .document(exerciseId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Exercise removed from favorites: " + exerciseId);
                    Set<String> currentFavorites = new HashSet<>(favoritesLiveData.getValue());
                    currentFavorites.remove(exerciseId);
                    favoritesLiveData.setValue(currentFavorites);
                    isLoadingLiveData.setValue(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing from favorites", e);
                    isLoadingLiveData.setValue(false);
                });
    }
    public void toggleFavorite(@NonNull Exercise exercise) {
        if (isFavorite(exercise.getId())) {
            removeFromFavorites(exercise.getId());
        } else {
            addToFavorites(exercise);
        }
    }
    private void loadUserFavorites() {
        if (firebaseAuth == null) {
            Log.w(TAG, "FirebaseAuth not initialized");
            favoritesLiveData.setValue(new HashSet<>());
            return;
        }
        FirebaseUser currentUser;
        try {
            currentUser = firebaseAuth.getCurrentUser();
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user", e);
            favoritesLiveData.setValue(new HashSet<>());
            return;
        }
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated");
            favoritesLiveData.setValue(new HashSet<>());
            return;
        }
        try {
            isLoadingLiveData.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "Error setting loading state", e);
            favoritesLiveData.setValue(new HashSet<>());
            return;
        }
        try {
            firestore.collection(COLLECTION_USERS)
                    .document(currentUser.getUid())
                    .collection(COLLECTION_FAVORITES)
                    .get()
                    .addOnCompleteListener(task -> {
                        try {
                            isLoadingLiveData.setValue(false);
                            if (task.isSuccessful()) {
                                Set<String> favorites = new HashSet<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    favorites.add(document.getId());
                                }
                                favoritesLiveData.setValue(favorites);
                                Log.d(TAG, "Loaded " + favorites.size() + " favorites");
                            } else {
                                Log.e(TAG, "Error loading favorites", task.getException());
                                favoritesLiveData.setValue(new HashSet<>());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing favorites", e);
                            favoritesLiveData.setValue(new HashSet<>());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Firebase query error", e);
            try {
                isLoadingLiveData.setValue(false);
                favoritesLiveData.setValue(new HashSet<>());
            } catch (Exception ex) {
                Log.e(TAG, "Error in fallback", ex);
            }
        }
    }
    public void getUserFavoriteExercises(OnFavoriteExercisesLoadedListener listener) {
        Log.d(TAG, "getUserFavoriteExercises called, Firebase enabled: " + FIREBASE_ENABLED);
        if (!FIREBASE_ENABLED || firestore == null || firebaseAuth == null) {
            Log.d(TAG, "Using offline mode, posting callback");
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                Log.d(TAG, "Callback executed - returning empty list");
                listener.onSuccess(new java.util.ArrayList<>());
            });
            return;
        }
        FirebaseUser currentUser;
        try {
            currentUser = firebaseAuth.getCurrentUser();
        } catch (Exception e) {
            Log.w(TAG, "Firebase auth error", e);
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                listener.onSuccess(new java.util.ArrayList<>());
            });
            return;
        }
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated");
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                listener.onSuccess(new java.util.ArrayList<>());
            });
            return;
        }
        android.os.Handler timeoutHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            Log.w(TAG, "Firebase query timeout, returning empty list");
            listener.onError("Request timeout");
        };
        timeoutHandler.postDelayed(timeoutRunnable, 10000); 
        firestore.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_FAVORITES)
                .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    timeoutHandler.removeCallbacks(timeoutRunnable); 
                    if (task.isSuccessful()) {
                        java.util.List<Exercise> favoriteExercises = new java.util.ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Exercise exercise = new Exercise();
                                exercise.setId(document.getString("exerciseId"));
                                exercise.setName(document.getString("name"));
                                exercise.setCategory(document.getString("category"));
                                exercise.setEquipment(document.getString("equipment"));
                                @SuppressWarnings("unchecked")
                                java.util.List<String> instructions = (java.util.List<String>) document.get("instructions");
                                if (instructions != null) {
                                    exercise.setInstructions(instructions);
                                }
                                @SuppressWarnings("unchecked")
                                java.util.List<String> primaryMuscles = (java.util.List<String>) document.get("primaryMuscles");
                                if (primaryMuscles != null) {
                                    exercise.setPrimaryMuscles(primaryMuscles);
                                }
                                @SuppressWarnings("unchecked")
                                java.util.List<String> images = (java.util.List<String>) document.get("images");
                                if (images != null) {
                                    exercise.setImages(images);
                                }
                                favoriteExercises.add(exercise);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing favorite exercise", e);
                            }
                        }
                        listener.onSuccess(favoriteExercises);
                    } else {
                        Log.e(TAG, "Error loading favorite exercises", task.getException());
                        listener.onError("Failed to load favorites");
                    }
                });
    }
    private void addToLocalFavorites(Exercise exercise) {
        Set<String> currentFavorites = new HashSet<>(favoritesLiveData.getValue());
        currentFavorites.add(exercise.getId());
        favoritesLiveData.setValue(currentFavorites);
        Log.d(TAG, "Added to local favorites: " + exercise.getName());
    }
    private void removeFromLocalFavorites(String exerciseId) {
        Set<String> currentFavorites = new HashSet<>(favoritesLiveData.getValue());
        currentFavorites.remove(exerciseId);
        favoritesLiveData.setValue(currentFavorites);
        Log.d(TAG, "Removed from local favorites: " + exerciseId);
    }
    public LiveData<java.util.List<Exercise>> getFavoriteExercisesWithDetails() {
        MutableLiveData<java.util.List<Exercise>> exercisesLiveData = new MutableLiveData<>();
        getUserFavoriteExercises(new OnFavoriteExercisesLoadedListener() {
            @Override
            public void onSuccess(java.util.List<Exercise> exercises) {
                exercisesLiveData.setValue(exercises);
            }
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading favorite exercises: " + error);
                exercisesLiveData.setValue(new java.util.ArrayList<>());
            }
        });
        return exercisesLiveData;
    }
    public interface OnFavoriteExercisesLoadedListener {
        void onSuccess(java.util.List<Exercise> exercises);
        void onError(String error);
    }
}