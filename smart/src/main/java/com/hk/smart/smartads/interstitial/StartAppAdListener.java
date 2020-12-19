package com.hk.smart.smartads.interstitial;

import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;

public class StartAppAdListener implements AdDisplayListener {

    SmartInterstitialPreload smartInterstitialPreload;

    public StartAppAdListener(SmartInterstitialPreload mSmartInterstitialPreload){
        smartInterstitialPreload = mSmartInterstitialPreload;
    }

    @Override
    public void adHidden(Ad ad) {
        smartInterstitialPreload.smartAdListener.onAdClosed();
        if (InterConst.PRELOAD) {
//            smartInterstitialPreload.loadAdMobInterstitial();
//            loadFacebookInterstitial();
             smartInterstitialPreload.loadStartAppInterstitial();
        }
    }

    @Override
    public void adDisplayed(Ad ad) {
        smartInterstitialPreload.smartAdListener.onAdDisplayed();
    }

    @Override
    public void adClicked(Ad ad) {
        smartInterstitialPreload.smartAdListener.onAdClicked();
    }

    @Override
    public void adNotDisplayed(Ad ad) {
        smartInterstitialPreload.showInterstitial(true);
    }
}
