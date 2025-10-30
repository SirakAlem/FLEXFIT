package it.unimib.flexfit.util;
import java.util.List;
public class ImageUtils {
    public static String getExerciseImageUrl(String exerciseName, List<String> images) {
        if (images == null || images.isEmpty()) {
            return getPlaceholderImageUrl();
        }
        String imagePath = images.get(0);
        return Constants.EXERCISES_IMAGES_BASE_URL + imagePath;
    }
    public static String getPlaceholderImageUrl() {
        return null;
    }
    private static String sanitizeExerciseName(String name) {
        if (name == null) return "unknown";
        return name.toLowerCase()
                  .replaceAll("[^a-zA-Z0-9\\s\\-_]", "_")
                  .replaceAll("\\s+", "_")
                  .replaceAll("_+", "_")
                  .replaceAll("^_|_$", "");
    }
}