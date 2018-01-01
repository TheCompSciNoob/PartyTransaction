package com.example.kyros.partytransaction;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Kyros on 12/31/2017.
 */

public class PartySelectorViewHolder extends RecyclerView.ViewHolder {

    public TextView partyName, date, address;
    public AppCompatImageView gmapsLocationImageView;

    public PartySelectorViewHolder(View itemView) {
        super(itemView);
        partyName = itemView.findViewById(R.id.party_name);
        date = itemView.findViewById(R.id.date);
        address = itemView.findViewById(R.id.address);
        gmapsLocationImageView = itemView.findViewById(R.id.gmaps_location_image_view);
    }
}
