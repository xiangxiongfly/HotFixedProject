package com.example.myapplication;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    FixMamager.loadFixDex(base);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
