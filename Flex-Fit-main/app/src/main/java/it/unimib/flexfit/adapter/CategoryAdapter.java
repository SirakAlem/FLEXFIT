package it.unimib.flexfit.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.unimib.flexfit.R;
import it.unimib.flexfit.model.CategoryItem;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<CategoryItem> categories;
    private final OnCategoryClickListener listener;
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem category);
    }
    public CategoryAdapter(List<CategoryItem> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem category = categories.get(position);
        holder.bind(category, listener);
    }
    @Override
    public int getItemCount() {
        return categories.size();
    }
    public void updateCategories(List<CategoryItem> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;
        private final TextView categoryDescription;
        private final TextView exerciseCount;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryDescription = itemView.findViewById(R.id.category_description);
            exerciseCount = itemView.findViewById(R.id.exercise_count);
        }
        public void bind(CategoryItem category, OnCategoryClickListener listener) {
            categoryName.setText(category.getName());
            categoryDescription.setText(category.getDescription());
            exerciseCount.setText(String.valueOf(category.getExerciseCount()));
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}