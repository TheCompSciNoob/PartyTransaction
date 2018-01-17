package com.example.kyros.partytransaction;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by per6 on 1/17/18.
 */

public class RealmLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {

    private RealmResults<T> results;
    private final RealmChangeListener<RealmResults<T>> listener = new RealmChangeListener<RealmResults<T>>() {
        @Override
        public void onChange(@NonNull RealmResults<T> results) {
            setValue(results);
        }
    };

    public RealmLiveData(RealmResults<T> results) {
        this.results = results;
    }

    @Override
    protected void onActive() {
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        results.removeChangeListener(listener);
    }
}
