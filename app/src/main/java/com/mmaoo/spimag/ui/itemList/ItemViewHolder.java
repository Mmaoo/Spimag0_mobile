package com.mmaoo.spimag.ui.itemList;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mmaoo.spimag.R;
import com.mmaoo.spimag.model.Item;

public class
ItemViewHolder extends RecyclerView.ViewHolder {

    View itemView;
    TextView name;
    TextView amount;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.name = itemView.findViewById(R.id.nameTextView);
        this.amount = itemView.findViewById(R.id.amountTextView);
    }

    /**
     * bind model to view
     * @param model
     */
    public void bind(final Item model){
        if(model.getName() != null){this.name.setText(model.getName());}
        this.amount.setText(Float.toString(model.getAmount()));
    }

    public View getItemView() {
        return itemView;
    }
}
