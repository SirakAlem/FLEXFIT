package it.unimib.flexfit.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class ExerciseResponse {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("results")
    @Expose
    private List<Exercise> results;
    public ExerciseResponse() {}
    public ExerciseResponse(int count, String next, String previous, List<Exercise> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getNext() {
        return next;
    }
    public void setNext(String next) {
        this.next = next;
    }
    public String getPrevious() {
        return previous;
    }
    public void setPrevious(String previous) {
        this.previous = previous;
    }
    public List<Exercise> getResults() {
        return results;
    }
    public void setResults(List<Exercise> results) {
        this.results = results;
    }
    @Override
    public String toString() {
        return "ExerciseResponse{" +
                "count=" + count +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                ", results=" + results +
                '}';
    }
}