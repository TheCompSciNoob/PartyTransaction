package com.example.kyros.partytransaction;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Kyros on 1/3/2018.
 */

public class ContributorNetChangeAdapter extends RecyclerView.Adapter<ContributorNetChangeViewHolder> {

    private RealmLiveData<ContributorInfo> contributorInfoRealmLiveData;
    private RealmList<ContributorInfo> combinedResults;
    private PartyInfo partyInfo;

    public ContributorNetChangeAdapter(RealmLiveData<ContributorInfo> contributorInfoRealmLiveData, PartyInfo partyInfo) {
        combinedResults = new RealmList<>();
        this.partyInfo = partyInfo;
        this.contributorInfoRealmLiveData = contributorInfoRealmLiveData;
    }

    @Override
    public ContributorNetChangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContributorNetChangeViewHolder(inflater.inflate(R.layout.contributor_net_change_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributorNetChangeViewHolder holder, int position) {
        ContributorInfo displayInfo = combinedResults.get(position);
        double amountNetChange = displayInfo.getAmountContributed() -
                combinedResults.sum("amountContributed").doubleValue() / partyInfo.getNumAttenders();
        holder.combinedName.setText(displayInfo.getName());
        holder.combinedAmount.setText(amountNetChange + "");
    }

    @Override
    public int getItemCount() {
        return combinedResults.size();
    }

    public void setCombinedResults(RealmList<ContributorInfo> combinedResults) {
        this.combinedResults = combinedResults;
    }

    public void combineResults() {
        RealmList<ContributorInfo> combinedResults = new RealmList<>();
        for (ContributorInfo rawInfo : contributorInfoRealmLiveData.getValue()) {
            boolean found = false;
            int i = 0;
            while (i < combinedResults.size() && !found) {
                ContributorInfo combinedInfo = combinedResults.get(i);
                if (combinedInfo.getName().equals(rawInfo.getName())) {
                    combinedInfo.setAmountContributed(combinedInfo.getAmountContributed() + rawInfo.getAmountContributed());
                    found = true;
                }
                i++;
            }
            if (!found) {
                combinedResults.add(new ContributorInfo(0, rawInfo.getName(), rawInfo.getAmountContributed()));
            }
        }
    }
}
