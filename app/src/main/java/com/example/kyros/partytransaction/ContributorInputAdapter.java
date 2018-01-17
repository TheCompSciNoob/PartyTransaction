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

public class ContributorInputAdapter extends RecyclerView.Adapter<ContributorInputViewHolder> {

    private static final String TAG = "ContributorInputAdapter";
    private final RealmLiveData<ContributorInfo> contributorInfoRealmLiveData;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;

    public ContributorInputAdapter(RealmLiveData<ContributorInfo> contributorInfoRealmLiveData) {
        this.contributorInfoRealmLiveData = contributorInfoRealmLiveData;
    }

    @Override
    public ContributorInputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContributorInputViewHolder(inflater.inflate(R.layout.contributor_input_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributorInputViewHolder holder, final int position) {
        ContributorInfo contributorInfo = contributorInfoRealmLiveData.getValue().get(position);
        holder.inputName.setText(contributorInfo.getName());
        holder.inputAmount.setText(contributorInfo.getAmountContributed() + "");
        holder.deleteEntryButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onDeleteButtonClickListener != null) {
                    onDeleteButtonClickListener.onDeleteButtonClicked(position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contributorInfoRealmLiveData.getValue().size();
    }

    public OnDeleteButtonClickListener getOnDeleteButtonClickListener() {
        return onDeleteButtonClickListener;
    }

    public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener onDeleteButtonClickListener) {
        this.onDeleteButtonClickListener = onDeleteButtonClickListener;
    }

    public interface OnDeleteButtonClickListener {
        public void onDeleteButtonClicked(int position);
    }
}
