package it.unimib.flexfit.database;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
public class Converters {
    private static final Gson gson = new Gson();
    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(data, listType);
    }
    @TypeConverter
    public static String stringListToString(List<String> strings) {
        return gson.toJson(strings);
    }
}