package com.hk.smart.smartads.interstitial;

import com.google.android.gms.ads.InterstitialAd;
import com.startapp.sdk.adsbase.StartAppAd;


public class InterConst {

    public static InterstitialAd adMobInterstitialAd = null;
    public static com.facebook.ads.InterstitialAd facebookInterstitialAd = null;
    public static StartAppAd startAppInterstitialAd = null;

    public static long adMobLastLoad = 0;
    public static long facebookLastLoad = 0;

    public static int facebookAdsCount = 0;

    public static boolean IMPRESSION_MSG = false;
    public static boolean PRELOAD = false;

    public static boolean isFbLoading = false;

}
