package com.example.kyros.partytransaction;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorInputAdapter extends RecyclerView.Adapter<ContributorInputViewHolder> implements RealmChangeListener<RealmResults<ContributorInfo>> {

    private static final String TAG = "ContributorInputAdapter";
    private RealmResults<ContributorInfo> contributorInputs;
    private Realm realm;

    public ContributorInputAdapter(Realm realm, RealmResults<ContributorInfo> contributorInputs) {
        this.contributorInputs = contributorInputs;
        this.realm = realm;
        contributorInputs.addChangeListener(this);
    }

    @Override
    public ContributorInputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContributorInputViewHolder(inflater.inflate(R.layout.contributor_input_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributorInputViewHolder holder, final int position) {
        holder.inputName.setText(contributorInputs.get(position).getName());
        holder.inputAmount.setText(contributorInputs.get(position).getAmountContributed() + "");
        holder.deleteEntryButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                realm.beginTransaction();
                contributorInputs.get(position).deleteFromRealm();
                realm.commitTransaction();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return contributorInputs.size();
        } catch (IllegalStateException e) {
            Log.e(TAG, "getItemCount: " + e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public void onChange(@NonNull RealmResults<ContributorInfo> contributorInfos) {
        notifyDataSetChanged();
    }
}
