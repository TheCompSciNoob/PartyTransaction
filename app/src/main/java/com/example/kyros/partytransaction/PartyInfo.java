package com.example.kyros.partytransaction;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kyros on 12/31/2017.
 */

public class PartyInfo extends RealmObject {

    @PrimaryKey
    private long id;
    private String partyName, date, address;
    private int numAttenders;

    public PartyInfo() {
    }

    public long getId() {
        return id;
    }

    public String getPartyName() {
        return partyName;
    }

    public PartyInfo setPartyName(String partyName) {
        this.partyName = partyName;
        return this;
    }

    public String getDate() {
        return date;
    }

    public PartyInfo setDate(String date) {
        this.date = date;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PartyInfo setAddress(String address) {
        this.address = address;
        return this;
    }

    public int getNumAttenders() {
        return numAttenders;
    }

    public PartyInfo setNumAttenders(int numAttenders) {
        this.numAttenders = numAttenders;
        return this;
    }
}
