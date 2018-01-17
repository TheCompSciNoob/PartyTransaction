package com.example.kyros.partytransaction;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import io.realm.Realm;

/**
 * Created by per6 on 1/17/18.
 */

public class PartyViewModel extends ViewModel {

    private static final String TAG = "PartyViewModel";
    private Realm realm;
    private RealmLiveData<PartyInfo> partyInfoRealmLiveData;

    public PartyViewModel() {
        super();
        realm = Realm.getDefaultInstance();
        partyInfoRealmLiveData = new RealmLiveData<>(realm.where(PartyInfo.class).findAllAsync());
    }

    public RealmLiveData<PartyInfo> getPartyInfoRealmLiveData() {
        return partyInfoRealmLiveData;
    }

    public void addOrUpdatePartyToDatabase(final PartyInfo addInfo) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(addInfo);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: party added/updated to database");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                Log.e(TAG, "onError: adding party to database failed " + error.getMessage(), error);
            }
        });
    }

    public void deletePartyFromDatabase(final PartyInfo deleteInfo) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                deleteInfo.deleteFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: party deleted from database");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError: deleting party from database failed " + error.getMessage(), error);
            }
        });
    }

    public PartyInfo findPartyById(long partyId) {
        return realm.where(PartyInfo.class).equalTo("id", partyId).findFirst();
    }

    public RealmLiveData<ContributorInfo> findContributorInfosById(long partyId) {
        return new RealmLiveData<ContributorInfo>(realm
                .where(ContributorInfo.class)
                .equalTo("partyId", partyId)
                .findAllAsync());
    }

    public void addContributorInfoToDatabase(final ContributorInfo addInfo) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealm(addInfo);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: add contributor info to database successful");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                Log.e(TAG, "onError: deleting contributor info failed " + error.getMessage(), error);
            }
        });
    }

    public void deleteContributorInfoFromDatabase(final ContributorInfo deleteInfo) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                deleteInfo.deleteFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: add contributor info to database successful");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                Log.e(TAG, "onError: deleting contributor info failed " + error.getMessage(), error);
            }
        });
    }

    @Override
    protected void onCleared() {
        realm.close();
        super.onCleared();
    }
}
