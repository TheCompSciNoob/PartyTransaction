package com.example.kyros.partytransaction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmResults;

public class PartySelectorActivity extends AppCompatActivity {

    private static final String TAG = "PartySelectorActivity";
    private Realm realm;
    private ActionMode deleteActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_selector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //database
        realm = Realm.getDefaultInstance();
        RealmResults<PartyInfo> displayResults = realm.where(PartyInfo.class).findAllAsync();

        //RecyclerView with database
        RecyclerView partySelectorRecyclerView = findViewById(R.id.party_selector_recycler_view);
        partySelectorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partySelectorRecyclerView.setAdapter(new PartySelectorAdapter(displayResults));
        partySelectorRecyclerView
                .addOnItemTouchListener(new RecyclerViewTouchHandler(partySelectorRecyclerView,
                        new RecyclerViewTouchHandler.OnRecyclerViewTouchListener() {
                            @Override
                            public void onClick(View child, int position) {

                            }

                            @Override
                            public void onLongClick(View child, int position) {

                            }
                        }));

        //adds party on click
        FloatingActionButton addPartyButton = (FloatingActionButton) findViewById(R.id.add_party_button);
        addPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPartyToDatabase("Test", "2/3/2017", "somewhere", 20);
                //TODO: start new activity
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
