package com.example.kyros.partytransaction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorNetChangeViewHolder extends RecyclerView.ViewHolder {

    public TextView combinedName, combinedAmount;

    public ContributorNetChangeViewHolder(View itemView) {
        super(itemView);
        combinedName = itemView.findViewById(R.id.contributor_net_change_name);
        combinedAmount = itemView.findViewById(R.id.contributor_net_change_amount);
    }
}
