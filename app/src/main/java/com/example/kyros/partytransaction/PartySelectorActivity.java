package com.example.kyros.partytransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private boolean hasActionModeStarted;
    private Realm realm;
    private RealmList<PartyInfo> deletionList = new RealmList<>();
    private RealmResults<PartyInfo> displayResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_selector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //database
        realm = Realm.getDefaultInstance();
        displayResults = realm.where(PartyInfo.class).findAllAsync();

        //RecyclerView with database
        RecyclerView partySelectorRecyclerView = findViewById(R.id.party_selector_recycler_view);
        partySelectorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final PartySelectorAdapter partySelectorAdapter = new PartySelectorAdapter(realm, displayResults);
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

    private void addPartyToDatabase(final String partyName, final String date, final String address, final int numAttenders) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                PartyInfo partyInfo = realm.createObject(PartyInfo.class, System.currentTimeMillis());
                partyInfo.setPartyName(partyName)
                        .setDate(date)
                        .setAddress(address)
                        .setNumAttenders(numAttenders);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: saved to database");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError: failed to save to database \n" + error.getMessage(), error);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        displayResults.removeAllChangeListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
