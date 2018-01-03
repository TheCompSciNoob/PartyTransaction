package com.example.kyros.partytransaction;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorInputViewHolder extends RecyclerView.ViewHolder {

    public TextView inputName, inputAmount;
    public AppCompatImageButton deleteEntryButton;

    public ContributorInputViewHolder(View itemView) {
        super(itemView);
        inputName = itemView.findViewById(R.id.contributor_input_name);
        inputAmount = itemView.findViewById(R.id.contributor_input_amount);
        deleteEntryButton = itemView.findViewById(R.id.delete_entry_button);
    }
}
