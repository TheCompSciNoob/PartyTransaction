package com.example.kyros.partytransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class EditPartyActivity extends AppCompatActivity {

    public static final String GET_PARTY_ID_KEY = "get party id from intent";
    private static final String TAG = "EditPartyActivity";
    private Realm realm;
    private PartyInfo editPartyInfo;
    private RealmResults<ContributorInfo> partyContributorInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_party);

        //finds user location
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location userLocation = null;
        if (locationManager != null && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        StringBuilder addressBuilder = new StringBuilder("location unavailable");
        String gMapsURL = "";
        if (userLocation != null) { //set address if the location can be found
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Address address = geocoder.getFromLocation(userLocation.getLatitude(), userLocation.getLongitude(), 1).get(0);
                addressBuilder.setLength(0);
                addressBuilder
                        .append(address.getAddressLine(0)) //street address
                        .append(", ")
                        .append(address.getLocality()) //city
                        .append(", ")
                        .append(address.getAdminArea()) //state
                        .append(", ")
                        .append(address.getCountryName()); //country
                gMapsURL = "http://maps.google.com/maps/api/staticmap?center=" + userLocation.getLatitude() +
                        "," + userLocation.getLongitude() + "&zoom=15&size=720x360&markers=color:red%7C" +
                        userLocation.getLatitude() + "," + userLocation.getLongitude() + "&focus=false";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //database + retrieve party from party id
        realm = Realm.getDefaultInstance();
        long partyId = getIntent().getLongExtra(GET_PARTY_ID_KEY, 0);
        editPartyInfo = realm.where(PartyInfo.class).equalTo("id", partyId).findFirst();
        if (editPartyInfo == null) {
            editPartyInfo = new PartyInfo(partyId, "Untitled Party",
                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()),
                    addressBuilder.toString(), gMapsURL, 1);
        }
        wireWidgets();
    }

    private void wireWidgets() {
        //contributor input
        partyContributorInfos
                = realm.where(ContributorInfo.class).equalTo("partyId", editPartyInfo.getId()).findAllAsync();
        RecyclerView contributorInputRecyclerView = findViewById(R.id.contributor_input_recycler_view);
        contributorInputRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contributorInputRecyclerView.setAdapter(new ContributorInputAdapter(realm, partyContributorInfos));
        final RecyclerView netChangeRecyclerView = findViewById(R.id.contributors_net_change_recycler_view);
        netChangeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        netChangeRecyclerView.setAdapter(new ContributorNetChangeAdapter(partyContributorInfos, editPartyInfo));
        Button addContributorButton = findViewById(R.id.add_contributor_button);
        addContributorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPartyActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.contributor_input_edit_dialog, null);
                builder.setCancelable(false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                //TODO: change to AutoCompleteTextView
                final EditText inputEditName = dialogView.findViewById(R.id.contributor_name_dialog_edit_text);
                final EditText inputEditAmount = dialogView.findViewById(R.id.contributor_amount_dialog_edit_text);
                Button doneButton = dialogView.findViewById(R.id.input_dialog_done_button);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveContributorInfoToRealm(inputEditName, inputEditAmount);
                        alertDialog.dismiss();
                    }
                });
                Button addButton = dialogView.findViewById(R.id.input_dialog_add_button);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveContributorInfoToRealm(inputEditName, inputEditAmount);
                        inputEditName.setText("");
                        inputEditAmount.setText("");
                        inputEditName.requestFocus();
                    }
                });
                alertDialog.show();
            }

            private void saveContributorInfoToRealm(EditText inputEditName, EditText inputEditAmount) {
                String contributorName = inputEditName.getText().toString();
                double contributorAmount = 0;
                if (inputEditAmount.length() != 0) {
                    contributorAmount = Double.parseDouble(inputEditAmount.getText().toString());
                }
                if (contributorName.length() != 0 && contributorAmount != 0.0) {
                    realm.beginTransaction();
                    ContributorInfo newInfo = realm.createObject(ContributorInfo.class, System.currentTimeMillis());
                    newInfo.setName(contributorName)
                            .setAmountContributed(contributorAmount)
                            .setPartyId(editPartyInfo.getId());
                    realm.commitTransaction();
                }
            }
        });
        //general info
        TextView partyName = findViewById(R.id.edit_party_name);
        TextView date = findViewById(R.id.edit_date);
        TextView location = findViewById(R.id.edit_location);
        final TextView numAttenders = findViewById(R.id.edit_num_attenders);
        numAttenders.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    int inputNumAttenders = 0;
                    if (numAttenders.length() != 0) {
                        inputNumAttenders = Integer.parseInt(numAttenders.getText().toString());
                    }
                    if (inputNumAttenders == 0) {
                        numAttenders.setText("1");
                    }
                    editPartyInfo.setNumAttenders(Math.max(inputNumAttenders, 1));
                    netChangeRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasMetSaveRequirements()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insertOrUpdate(editPartyInfo);
                    Log.d(TAG, "execute: transaction executed");
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        partyContributorInfos.removeAllChangeListeners();
        if (!hasMetSaveRequirements()) {
            realm.beginTransaction();
            editPartyInfo.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean hasMetSaveRequirements() {
        return !editPartyInfo.getPartyName().equals("") || //if party name is not empty
                editPartyInfo.getNumAttenders() > 1 || //if number of attenders is not only 1
                partyContributorInfos.size() > 0; //if there are contributors
    }
}
