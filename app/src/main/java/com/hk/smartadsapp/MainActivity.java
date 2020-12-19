package com.hk.smartadsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.flashbar.Flashbar;
import com.hk.smart.smartads.SmartAds;
import com.hk.smart.smartads.dialog.SmartDialog;
import com.hk.smart.smartads.interstitial.SmartInterstitialPreload;
import com.hk.smart.smartads.listeners.InitializeListener;
import com.hk.smart.smartads.listeners.SmartInterstitialListener;
import com.hk.smart.smartads.utils.KeyManager;
import com.hk.smart.smartads.utils.Prefs;
import com.hk.smartads.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView txtTitle;

    SmartInterstitialPreload smartInterstitialPreload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmartAds.initialize(this, new InitializeListener() {
            @Override
            public void onSmartAdInitializeSuccess() {
                super.onSmartAdInitializeSuccess();
                Toast.makeText(MainActivity.this, "Initialize done!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSmartAdInitializeFailed(String error) {
                super.onSmartAdInitializeFailed(error);
                Toast.makeText(MainActivity.this, "Initialize error!", Toast.LENGTH_SHORT).show();
            }
        });

        txtTitle = findViewById(R.id.txt_title);
        smartInterstitialPreload = new SmartInterstitialPreload(this);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SmartDialog smartDialog = new SmartDialog(MainActivity.this);
//                smartDialog.showDialog();

                smartInterstitialPreload.showInterstitial();
                txtTitle.setText(Prefs.getString("abc"));

//                for (int i = 0; i < 100; i++) {
//                    int j = KeyManager.getNextId(i);
//                    ;
////            Log.e(MainActivity.this.getClass().getName(), "id: "+j);
//                    Log.e(MainActivity.this.getClass().getName(), "Targeting number: " + i + "key: " + KeyManager.getAdMobBannerKey());
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });


    }
}