package com.example.kyros.partytransaction;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Kyros on 12/31/2017.
 */

public class PartySelectorAdapter extends RecyclerView.Adapter<PartySelectorViewHolder> implements RealmChangeListener<RealmResults<PartyInfo>>{

    private static final String TAG = "PartySelectorAdapter";
    private RealmResults<PartyInfo> displayResults;

    public PartySelectorAdapter(RealmResults<PartyInfo> displayResults) {
        this.displayResults = displayResults;
        displayResults.addChangeListener(this);
    }

    @Override
    public PartySelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PartySelectorViewHolder(inflater.inflate(R.layout.party_selector_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(PartySelectorViewHolder holder, int position) {
        PartyInfo partyInfo = displayResults.get(position);
        holder.partyName.setText(partyInfo.getPartyName());
        holder.date.setText(partyInfo.getDate());
        holder.address.setText(partyInfo.getAddress());
        //TODO: gmaps location image
    }

    @Override
    public int getItemCount() {
        return displayResults.size();
    }

    @Override
    public void onChange(RealmResults<PartyInfo> partyInfos) {
        notifyDataSetChanged();
        Log.d(TAG, "onChange: RecyclerView in PartySelectorActivity updated");
    }
}
