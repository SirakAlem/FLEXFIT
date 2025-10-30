package it.unimib.flexfit.model;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import it.unimib.flexfit.database.Converters;
@Entity(tableName = "exercise")
@TypeConverters({Converters.class})
public class Exercise {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    private String id;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;
    @ColumnInfo(name = "force")
    @SerializedName("force")
    @Expose
    private String force;
    @ColumnInfo(name = "level")
    @SerializedName("level")
    @Expose
    private String level;
    @ColumnInfo(name = "mechanic")
    @SerializedName("mechanic")
    @Expose
    private String mechanic;
    @ColumnInfo(name = "equipment")
    @SerializedName("equipment")
    @Expose
    private String equipment;
    @ColumnInfo(name = "primary_muscles")
    @SerializedName("primaryMuscles")
    @Expose
    private List<String> primaryMuscles;
    @ColumnInfo(name = "secondary_muscles")
    @SerializedName("secondaryMuscles")
    @Expose
    private List<String> secondaryMuscles;
    @ColumnInfo(name = "instructions")
    @SerializedName("instructions")
    @Expose
    private List<String> instructions;
    @ColumnInfo(name = "category")
    @SerializedName("category")
    @Expose
    private String category;
    @ColumnInfo(name = "images")
    @SerializedName("images")
    @Expose
    private List<String> images;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    public Exercise() {}
    @Ignore
    public Exercise(String id, String name, String force, String level, String mechanic,
                   String equipment, List<String> primaryMuscles, List<String> secondaryMuscles,
                   List<String> instructions, String category, List<String> images) {
        this.id = id;
        this.name = name;
        this.force = force;
        this.level = level;
        this.mechanic = mechanic;
        this.equipment = equipment;
        this.primaryMuscles = primaryMuscles;
        this.secondaryMuscles = secondaryMuscles;
        this.instructions = instructions;
        this.category = category;
        this.images = images;
        this.isFavorite = false;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getForce() {
        return force;
    }
    public void setForce(String force) {
        this.force = force;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getMechanic() {
        return mechanic;
    }
    public void setMechanic(String mechanic) {
        this.mechanic = mechanic;
    }
    public String getEquipment() {
        return equipment;
    }
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
    public List<String> getPrimaryMuscles() {
        return primaryMuscles;
    }
    public void setPrimaryMuscles(List<String> primaryMuscles) {
        this.primaryMuscles = primaryMuscles;
    }
    public List<String> getSecondaryMuscles() {
        return secondaryMuscles;
    }
    public void setSecondaryMuscles(List<String> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }
    public List<String> getInstructions() {
        return instructions;
    }
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    @Override
    public String toString() {
        return "Exercise{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", force='" + force + '\'' +
                ", level='" + level + '\'' +
                ", mechanic='" + mechanic + '\'' +
                ", equipment='" + equipment + '\'' +
                ", primaryMuscles=" + primaryMuscles +
                ", secondaryMuscles=" + secondaryMuscles +
                ", instructions=" + instructions +
                ", category='" + category + '\'' +
                ", images=" + images +
                ", isFavorite=" + isFavorite +
                '}';
    }
}