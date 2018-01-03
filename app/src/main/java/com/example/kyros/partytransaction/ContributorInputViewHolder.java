package com.example.kyros.partytransaction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorInputViewHolder extends RecyclerView.ViewHolder {

    public TextView inputName, inputAmount;

    public ContributorInputViewHolder(View itemView) {
        super(itemView);
        inputName = itemView.findViewById(R.id.contributor_input_name);
        inputAmount = itemView.findViewById(R.id.contributor_input_amount);
    }
}
