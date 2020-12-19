package com.hk.smart.smartads.interstitial;

import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.hk.smart.BuildConfig;

public class AdMobAdListener extends AdListener {

    SmartInterstitialPreload smartInterstitialPreload;

    public AdMobAdListener(SmartInterstitialPreload mSmartInterstitialPreload) {
        smartInterstitialPreload = mSmartInterstitialPreload;
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
        smartInterstitialPreload.smartAdListener.onAdDisplayed();
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        smartInterstitialPreload.smartAdListener.onAdClosed();
        if (InterConst.PRELOAD) {
            smartInterstitialPreload.loadAdMobInterstitial();
        }
    }

    @Override
    public void onAdImpression() {
        super.onAdImpression();
        smartInterstitialPreload.smartAdListener.onAdImpressionLogged();
        if (smartInterstitialPreload.logEvents && BuildConfig.DEBUG)
            Toast.makeText(smartInterstitialPreload.mContext, "AdMob Interstitial Impression Logged", Toast.LENGTH_SHORT).show();
    }
}
