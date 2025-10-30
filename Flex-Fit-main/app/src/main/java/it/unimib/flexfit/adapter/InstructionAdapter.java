package it.unimib.flexfit.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.unimib.flexfit.R;
public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder> {
    private final List<String> instructions;
    public InstructionAdapter(List<String> instructions) {
        this.instructions = instructions;
    }
    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instruction, parent, false);
        return new InstructionViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        String instruction = instructions.get(position);
        holder.bind(instruction, position + 1);
    }
    @Override
    public int getItemCount() {
        return instructions.size();
    }
    static class InstructionViewHolder extends RecyclerView.ViewHolder {
        private final TextView stepNumber;
        private final TextView instructionText;
        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.step_number);
            instructionText = itemView.findViewById(R.id.instruction_text);
        }
        public void bind(String instruction, int step) {
            stepNumber.setText(String.valueOf(step));
            instructionText.setText(instruction);
        }
    }
}