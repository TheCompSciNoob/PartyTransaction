package com.example.kyros.partytransaction;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Kyros on 12/31/2017.
 */

public class PartySelectorAdapter extends RecyclerView.Adapter<PartySelectorViewHolder> implements RealmChangeListener<RealmResults<PartyInfo>> {

    private static final String TAG = "PartySelectorAdapter";
    private final RealmLiveData<PartyInfo> displayResults;
    /*private ActionMode.Callback deleteActionCallbacks = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("DELETE");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            realm.beginTransaction();
            for (PartyInfo deleteInfo : deletionList) {
                deleteInfo.deleteFromRealm();
            }
            realm.commitTransaction();
            deletionList.clear();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            hasActionModeStarted = false;
        }
    };*/

    public PartySelectorAdapter(RealmLiveData<PartyInfo> displayResults) {
        this.displayResults = displayResults;
    }

    @Override
    public PartySelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.party_selector_item_layout, parent, false);
        return new PartySelectorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PartySelectorViewHolder holder, final int position) {
        final PartyInfo partyInfo = displayResults.getValue().get(position);
        holder.partyName.setText(partyInfo.getPartyName());
        holder.date.setText(partyInfo.getDate());
        holder.address.setText(partyInfo.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editPartyIntent = new Intent(holder.itemView.getContext(), EditPartyActivity.class);
                editPartyIntent.putExtra(EditPartyActivity.GET_PARTY_ID_KEY
                        , displayResults.getValue().get(position).getId());
                holder.itemView.getContext().startActivity(editPartyIntent);
            }
        });
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasActionModeStarted) {
                    Intent editPartyIntent = new Intent(holder.itemView.getContext(), EditPartyActivity.class);
                    editPartyIntent.putExtra(EditPartyActivity.GET_PARTY_ID_KEY
                            , displayResults.get(position).getId());
                    holder.itemView.getContext().startActivity(editPartyIntent);
                } else {
                    if (!deletionList.contains(displayResults.get(position))) { //not selected -> select
                        deletionList.add(displayResults.get(position));
                        holder.itemView.setBackgroundColor(Color.LTGRAY);
                    } else { //already selected -> deselect
                        deletionList.remove(displayResults.get(position));
                        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!hasActionModeStarted) {
                    hasActionModeStarted = true;
                    ((AppCompatActivity) holder.itemView.getContext())
                            .startSupportActionMode(deleteActionCallbacks);
                    holder.itemView.setBackgroundColor(Color.LTGRAY);
                    deletionList.add(partyInfo);
                    return true;
                }
                return false;
            }
        });*/
        //TODO: gmaps location image
    }

    @Override
    public int getItemCount() {
        return displayResults.getValue().size();
    }

    @Override
    public void onChange(RealmResults<PartyInfo> partyInfos) {
        notifyDataSetChanged();
        Log.d(TAG, "onChange: RecyclerView in PartySelectorActivity updated");
    }
}
