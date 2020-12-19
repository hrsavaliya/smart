package com.hk.smart.smartads.interstitial;

import android.widget.Toast;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.hk.smart.BuildConfig;

public class FbAdListener extends AbstractAdListener {

    SmartInterstitialPreload smartInterstitialPreload;

    public FbAdListener(SmartInterstitialPreload mSmartInterstitialPreload) {
        smartInterstitialPreload = mSmartInterstitialPreload;
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        super.onInterstitialDismissed(ad);
        smartInterstitialPreload.smartAdListener.onAdClosed();
        if (InterConst.PRELOAD) {
            if (smartInterstitialPreload != null)
                smartInterstitialPreload.loadFacebookInterstitial();
        }
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        super.onInterstitialDisplayed(ad);
        smartInterstitialPreload.smartAdListener.onAdDisplayed();
    }

    @Override
    public void onError(Ad ad, AdError adError) {

    }

    @Override
    public void onAdLoaded(Ad ad) {

    }

    @Override
    public void onAdClicked(Ad ad) {
        smartInterstitialPreload.smartAdListener.onAdClicked();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        smartInterstitialPreload.smartAdListener.onAdImpressionLogged();
        if (smartInterstitialPreload.logEvents && BuildConfig.DEBUG)
            Toast.makeText(smartInterstitialPreload.mContext, "Facebook Interstitial Impression Logged", Toast.LENGTH_SHORT).show();

        if (InterConst.IMPRESSION_MSG)
            Toast.makeText(smartInterstitialPreload.mContext, "Impression", Toast.LENGTH_SHORT).show();
    }
}
