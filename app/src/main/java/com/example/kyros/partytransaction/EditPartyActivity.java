package com.example.kyros.partytransaction;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
    private PartyInfo editPartyInfo;
    private PartyViewModel partyViewModel;
    private RealmLiveData<ContributorInfo> contributorInfoRealmLiveData;

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
        partyViewModel = ViewModelProviders.of(this).get(PartyViewModel.class);
        long partyId = getIntent().getLongExtra(GET_PARTY_ID_KEY, 0);
        editPartyInfo = partyViewModel.findPartyById(partyId);
        if (editPartyInfo == null) {
            editPartyInfo = new PartyInfo(partyId, "Untitled Party",
                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()),
                    addressBuilder.toString(), gMapsURL, 1);
        }
        wireWidgets();
    }

    private void wireWidgets() {
        //database
        partyViewModel = ViewModelProviders.of(this).get(PartyViewModel.class);
        contributorInfoRealmLiveData = partyViewModel.findContributorInfosById(editPartyInfo.getId());
        //contributor input
        final ContributorInputAdapter contributorInputAdapter = new ContributorInputAdapter(contributorInfoRealmLiveData);
        RecyclerView contributorInputRecyclerView = findViewById(R.id.contributor_input_recycler_view);
        contributorInputRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contributorInputRecyclerView.setAdapter(contributorInputAdapter);
        contributorInputAdapter.setOnDeleteButtonClickListener(new ContributorInputAdapter.OnDeleteButtonClickListener() {
            @Override
            public void onDeleteButtonClicked(int position) {
                partyViewModel.deleteContributorInfoFromDatabase(contributorInfoRealmLiveData.getValue().get(position));
                contributorInputAdapter.notifyDataSetChanged();
            }
        });
        final RecyclerView netChangeRecyclerView = findViewById(R.id.contributors_net_change_recycler_view);
        final ContributorNetChangeAdapter contributorNetChangeAdapter = new ContributorNetChangeAdapter(contributorInfoRealmLiveData, editPartyInfo);
        netChangeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        netChangeRecyclerView.setAdapter(contributorNetChangeAdapter);
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
                    ContributorInfo addInfo = new ContributorInfo(editPartyInfo.getId(), contributorName, contributorAmount);
                    partyViewModel.addContributorInfoToDatabase(addInfo);
                }
            }
        });
        //general info
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
        //update both adapters when database is changed
        contributorInfoRealmLiveData.observe(this, new Observer<RealmResults<ContributorInfo>>() {
            @Override
            public void onChanged(@android.support.annotation.Nullable RealmResults<ContributorInfo> contributorInfoRealmResults) {
                contributorInputAdapter.notifyDataSetChanged();
                contributorNetChangeAdapter.combineResults();
                contributorNetChangeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasMetSaveRequirements()) {
            EditText partyName = findViewById(R.id.edit_party_name);
            editPartyInfo.setPartyName(partyName.getText().toString());
            EditText date = findViewById(R.id.edit_date);
            editPartyInfo.setDate(date.getText().toString());
            EditText location = findViewById(R.id.edit_location);
            editPartyInfo.setAddress(location.getText().toString());
            partyViewModel.addOrUpdatePartyToDatabase(editPartyInfo);
        }
    }

    private boolean hasMetSaveRequirements() {
        return !editPartyInfo.getPartyName().equals("") || //if party name is not empty
                editPartyInfo.getNumAttenders() > 1 || //if number of attenders is not only 1
                contributorInfoRealmLiveData.getValue().size() > 0; //if there are contributors
    }
}
