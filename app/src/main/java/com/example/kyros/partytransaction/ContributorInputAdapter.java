package com.example.kyros.partytransaction;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorInputAdapter extends RecyclerView.Adapter<ContributorInputViewHolder> implements RealmChangeListener<RealmResults<ContributorInfo>>{

    private RealmResults<ContributorInfo> infos;

    public ContributorInputAdapter(RealmResults<ContributorInfo> infos) {
        this.infos = infos;
        infos.addChangeListener(this);
    }

    @Override
    public ContributorInputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContributorInputViewHolder(inflater.inflate(R.layout.contributor_input_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributorInputViewHolder holder, int position) {
        holder.inputName.setText(infos.get(position).getName());
        holder.inputAmount.setText(infos.get(position).getAmountContributed() + "");
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    @Override
    public void onChange(@NonNull RealmResults<ContributorInfo> contributorInfos) {
        notifyDataSetChanged();
    }
}
