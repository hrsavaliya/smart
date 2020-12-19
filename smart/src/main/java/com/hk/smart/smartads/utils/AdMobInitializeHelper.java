package com.hk.smart.smartads.utils;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import timber.log.Timber;

public class AdMobInitializeHelper implements OnInitializationCompleteListener {

    String TAG = AdMobInitializeHelper.class.getSimpleName();

    /**
     * It's recommended to call this method from Application.onCreate().
     * Otherwise you can call it from all Activity.onCreate()
     * methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
    public static void initialize(Context context) {
        MobileAds.initialize(context, new AdMobInitializeHelper());
    }

    @Override
    public void onInitializationComplete(InitializationStatus initializationStatus) {
        Timber.tag(TAG).d("AdMob InitializationComplete");
    }
}
