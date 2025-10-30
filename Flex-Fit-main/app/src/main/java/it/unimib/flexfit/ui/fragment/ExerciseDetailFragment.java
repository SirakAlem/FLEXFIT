package it.unimib.flexfit.ui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.adapter.InstructionAdapter;
import it.unimib.flexfit.model.Exercise;
import it.unimib.flexfit.ui.viewmodel.ExerciseViewModel;
import it.unimib.flexfit.util.ImageUtils;
public class ExerciseDetailFragment extends Fragment {
    private ImageView exerciseImageDetail;
    private TextView exerciseNameDetail;
    private TextView levelBadge;
    private TextView equipmentBadge;
    private TextView primaryMusclesDetail;
    private TextView secondaryMusclesDetail;
    private RecyclerView instructionsRecycler;
    private ImageView fabFavorite;
    private ExerciseViewModel exerciseViewModel;
    private String exerciseId;
    private String exerciseName;
    private Exercise currentExercise;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        if (getArguments() != null) {
            exerciseId = getArguments().getString("exercise_id");
            exerciseName = getArguments().getString("exercise_name");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        observeData();
        setupClickListeners();
    }
    private void initViews(View view) {
        exerciseImageDetail = view.findViewById(R.id.exercise_image_detail);
        exerciseNameDetail = view.findViewById(R.id.exercise_name_detail);
        levelBadge = view.findViewById(R.id.level_badge);
        equipmentBadge = view.findViewById(R.id.equipment_badge);
        primaryMusclesDetail = view.findViewById(R.id.primary_muscles_detail);
        secondaryMusclesDetail = view.findViewById(R.id.secondary_muscles_detail);
        instructionsRecycler = view.findViewById(R.id.instructions_recycler);
        fabFavorite = view.findViewById(R.id.fab_favorite);
    }
    private void setupRecyclerView() {
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void observeData() {
        if (exerciseId != null) {
            exerciseViewModel.getExerciseById(exerciseId).observe(getViewLifecycleOwner(), exercise -> {
                if (exercise != null) {
                    currentExercise = exercise;
                    populateViews(exercise);
                }
            });
        }
    }
    private void populateViews(Exercise exercise) {
        String imageUrl = ImageUtils.getExerciseImageUrl(exercise.getName(), exercise.getImages());
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .override(400, 400) 
                .centerCrop()
                .into(exerciseImageDetail);
        }
        exerciseNameDetail.setText(exercise.getName());
        if (exercise.getLevel() != null) {
            levelBadge.setText(exercise.getLevel().toUpperCase());
        }
        if (exercise.getEquipment() != null) {
            equipmentBadge.setText(exercise.getEquipment().toUpperCase());
        }
        if (exercise.getPrimaryMuscles() != null && !exercise.getPrimaryMuscles().isEmpty()) {
            StringBuilder muscles = new StringBuilder("Primary: ");
            for (int i = 0; i < exercise.getPrimaryMuscles().size(); i++) {
                if (i > 0) muscles.append(", ");
                muscles.append(exercise.getPrimaryMuscles().get(i));
            }
            primaryMusclesDetail.setText(muscles.toString());
        }
        if (exercise.getSecondaryMuscles() != null && !exercise.getSecondaryMuscles().isEmpty()) {
            StringBuilder muscles = new StringBuilder("Secondary: ");
            for (int i = 0; i < exercise.getSecondaryMuscles().size(); i++) {
                if (i > 0) muscles.append(", ");
                muscles.append(exercise.getSecondaryMuscles().get(i));
            }
            secondaryMusclesDetail.setText(muscles.toString());
            secondaryMusclesDetail.setVisibility(View.VISIBLE);
        } else {
            secondaryMusclesDetail.setVisibility(View.GONE);
        }
        if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
            InstructionAdapter adapter = new InstructionAdapter(exercise.getInstructions());
            instructionsRecycler.setAdapter(adapter);
        }
        updateFavoriteButton(exercise.isFavorite());
    }
    private void setupClickListeners() {
        fabFavorite.setOnClickListener(v -> {
            if (currentExercise != null) {
                exerciseViewModel.toggleFavoriteById(currentExercise.getId());
            }
        });
    }
    private void updateFavoriteButton(boolean isFavorite) {
        fabFavorite.setImageResource(
            isFavorite ? R.drawable.ic_favorite : R.drawable.ic_heart_outline_black
        );
    }
}