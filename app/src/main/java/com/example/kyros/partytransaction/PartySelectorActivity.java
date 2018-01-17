package com.example.kyros.partytransaction;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PartySelectorActivity extends AppCompatActivity {

    private static final String TAG = "PartySelectorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_selector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //database from ViewModel
        PartyViewModel partyViewModel = ViewModelProviders.of(this).get(PartyViewModel.class);
        RealmLiveData<PartyInfo> partyInfoRealmLiveData = partyViewModel.getPartyInfoRealmLiveData();

        //RecyclerView with database
        RecyclerView partySelectorRecyclerView = findViewById(R.id.party_selector_recycler_view);
        partySelectorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final PartySelectorAdapter partySelectorAdapter = new PartySelectorAdapter(partyInfoRealmLiveData);
        partyInfoRealmLiveData.observe(this, new Observer<RealmResults<PartyInfo>>() {
            @Override
            public void onChanged(@Nullable RealmResults<PartyInfo> partyInfos) {
                partySelectorAdapter.notifyDataSetChanged();
            }
        });
        partySelectorRecyclerView.setAdapter(partySelectorAdapter);

        //adds party on click
        FloatingActionButton addPartyButton = (FloatingActionButton) findViewById(R.id.add_party_button);
        addPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: start new activity
                Intent newPartyIntent = new Intent(PartySelectorActivity.this, EditPartyActivity.class);
                newPartyIntent.putExtra(EditPartyActivity.GET_PARTY_ID_KEY
                        , System.currentTimeMillis());
                startActivity(newPartyIntent);
            }
        });
    }
}
