package com.example.kyros.partytransaction;

import io.realm.RealmObject;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorInfo extends RealmObject {

    private long partyId;
    private String name;
    private double amountContributed;

    public ContributorInfo(long partyId, String name, double amountContributed) {
        this.partyId = partyId;
        this.name = name;
        this.amountContributed = amountContributed;
    }

    public ContributorInfo() {
        this(0, "", 0);
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmountContributed() {
        return amountContributed;
    }

    public void setAmountContributed(double amountContributed) {
        this.amountContributed = amountContributed;
    }
}
