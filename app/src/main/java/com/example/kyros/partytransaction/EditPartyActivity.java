package com.example.kyros.partytransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

public class EditPartyActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<ContributorInfo>> {

    public static final String GET_PARTY_ID_KEY = "get party id from intent";
    private Realm realm;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //database + retrieve party from party id
        realm = Realm.getDefaultInstance();
        long partyId = getIntent().getLongExtra(GET_PARTY_ID_KEY, 0);
        PartyInfo editInfo = realm.where(PartyInfo.class).equalTo("id", partyId).findFirst();
        if (editInfo == null) {
            editInfo = new PartyInfo(partyId, "Untitled Party",
                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()),
                    addressBuilder.toString(),
                    "",
                    1);
        }
        final RealmResults<ContributorInfo> partyContributorInfos
                = realm.where(ContributorInfo.class).equalTo("partyId", partyId).findAllAsync();
        partyContributorInfos.addChangeListener(this);
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

    @Override
    public void onChange(@NonNull RealmResults<ContributorInfo> contributorInfos) {

    }
}
