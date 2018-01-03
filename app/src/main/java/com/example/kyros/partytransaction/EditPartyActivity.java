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
import android.view.View;
import android.widget.Button;
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

public class EditPartyActivity extends AppCompatActivity {

    public static final String GET_PARTY_ID_KEY = "get party id from intent";
    private Realm realm;
    private PartyInfo editPartyInfo;
    private TextView partyName, date, location, numAttenders;

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
                    addressBuilder.toString(), gMapsURL,1);
        }

        //wireWidgets
        wireWidgets();
        updateUI();
    }

    private void wireWidgets() {
        //general info
        partyName = findViewById(R.id.edit_party_name);
        date = findViewById(R.id.edit_date);
        location = findViewById(R.id.edit_location);
        numAttenders = findViewById(R.id.edit_num_attenders);
        //contributor input
        RealmResults<ContributorInfo> partyContributorInfos
                = realm.where(ContributorInfo.class).equalTo("partyId", editPartyInfo.getId()).findAllAsync();
        RecyclerView contributorInputRecyclerView = findViewById(R.id.contributor_input_recycler_view);
        contributorInputRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contributorInputRecyclerView.setAdapter(new ContributorInputAdapter(partyContributorInfos));
        RecyclerView netChangeRecyclerView = findViewById(R.id.contributors_net_change_recycler_view);
        netChangeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //TODO: netChangeRecyclerView.setAdapter();
        Button addContributorButton = findViewById(R.id.add_contributor_button);
        addContributorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPartyActivity.this);
                //TODO: inflate alert dialog
            }
        });
    }

    private void updateUI() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        //save the info if it meets the requirements
    }

    @Override
    protected void onStop() {
        realm.close();
        super.onStop();
    }
}
