package com.example.kyros.partytransaction;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kyros on 1/2/2018.
 */

public class ContributorInfo extends RealmObject {

    @PrimaryKey
    private long id;
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

    public ContributorInfo setPartyId(long partyId) {
        this.partyId = partyId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ContributorInfo setName(String name) {
        this.name = name;
        return this;
    }

    public double getAmountContributed() {
        return amountContributed;
    }

    public ContributorInfo setAmountContributed(double amountContributed) {
        this.amountContributed = amountContributed;
        return this;
    }

    public long getId() {
        return id;
    }

    public ContributorInfo setId(long id) {
        this.id = id;
        return this;
    }
}
