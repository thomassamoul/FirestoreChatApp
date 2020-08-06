package com.thomas.firebasechatapp;

import android.app.Application;
import androidx.multidex.MultiDex;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(MyApp.this);

        MultiDex.install(this);


    }
}
