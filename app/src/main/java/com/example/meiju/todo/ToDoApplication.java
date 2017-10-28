package com.example.meiju.todo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmCollection;
import io.realm.RealmConfiguration;


public class ToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
