package com.hk.smart.smartads.interstitial;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdExtendedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.hk.smart.smartads.SmartAds;
import com.hk.smart.smartads.listeners.SmartInterstitialListener;
import com.hk.smart.smartads.utils.AdSettings;
import com.hk.smart.smartads.utils.KeyManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.model.AdPreferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.google.android.gms.ads.doubleclick.PublisherAdRequest.DEVICE_ID_EMULATOR;

class SmartInterstitial {

    String TAG = SmartInterstitial.class.getSimpleName();
    private AppCompatActivity mContext;

    KProgressHUD progressHUD;

    private boolean adRequesting = false;
    private boolean adMobRequesting = false;
    private boolean facebookRequesting = false;
    private boolean startAppRequesting = false;

    private boolean adShowing = false;
    private boolean adMobShowing = false;
    private boolean facebookShowing = false;
    private boolean startAppShowing = false;

    private InterstitialAd adMobInterstitialAd = null;
    private com.facebook.ads.InterstitialAd facebookInterstitialAd = null;
    private StartAppAd startAppInterstitialAd = null;

    private int mPriority = -1;
    private int mShowPriority = -1;

    private SmartInterstitialListener mSmartInterstitialListener = null;

    private SmartInterstitialListener smartInterstitialListener = new SmartInterstitialListener() {
        @Override
        public void onInterstitialRequestSuccess() {
            mPriority = -1;
            adRequesting = false;
            adMobRequesting = false;
            facebookRequesting = false;
            startAppRequesting = false;

            Timber.d("onInterstitialRequestSuccess...");
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialRequestSuccess();

        }

        @Override
        public void onInterstitialDisplayed() {
            Timber.d("onInterstitialDisplayed...");
            adRequesting = false;
            adMobRequesting = false;
            facebookRequesting = false;
            startAppRequesting = false;

            adShowing = false;
            adMobShowing = false;
            facebookShowing = false;
            startAppShowing = false;

            mPriority = -1;
            mShowPriority = -1;

            progressHUD.dismiss();
            progressHUD = null;

            //Toasty.normal(mContext, "Please have look for 5 second...").show();

            //Toasty.normal(mContext, "Please have look for 5 second...", Toast.LENGTH_LONG, true).show();
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialDisplayed();
        }

        @Override
        public void onInterstitialRequestFailed() {
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialRequestFailed();
        }

        @Override
        public void onInterstitialRequestFailed(String error) {
            Timber.e("%s onInterstitialRequestFailed...", error);
            if (error.toLowerCase().contains("admob")) {
                adMobRequesting = false;
                if (adMobInterstitialAd != null)
                    adMobInterstitialAd = null;
            } else if (error.toLowerCase().contains("facebook")) {
                facebookRequesting = false;
                if (facebookInterstitialAd != null)
                    facebookInterstitialAd.destroy();
                facebookInterstitialAd = null;
            } else if (error.toLowerCase().contains("startapp")) {
                startAppRequesting = false;
                if (startAppInterstitialAd != null)
                    startAppInterstitialAd = null;
            }

            if (mSmartInterstitialListener != null) {
                mSmartInterstitialListener.onInterstitialRequestFailed(error);
                mSmartInterstitialListener.onInterstitialRequestFailed();
            }
        }

        @Override
        public void onInterstitialImpressionLogged() {
            Timber.d("onInterstitialImpressionLogged...");
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialImpressionLogged();
        }

        @Override
        public void onInterstitialClick() {
            Timber.d("onInterstitialClick...");
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialClick();
        }

        @Override
        public void onInterstitialClose() {
            Timber.d("onInterstitialClose...");
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialClose();

            mSmartInterstitialListener = null;
        }

        @Override
        public void onInterstitialNoAdFound() {
            super.onInterstitialNoAdFound();
            Timber.d("onInterstitialNoAdFound...");
            if (mSmartInterstitialListener != null)
                mSmartInterstitialListener.onInterstitialNoAdFound();
        }
    };

    public SmartInterstitial(AppCompatActivity context) {
        mContext = context;

    }

    public void destroy() {
        if (mContext != null)
            mContext = null;
        if (mSmartInterstitialListener != null)
            mSmartInterstitialListener = null;
        if (adMobInterstitialAd != null)
            adMobInterstitialAd = null;
        if (facebookInterstitialAd != null)
            facebookInterstitialAd.destroy();
        if (startAppInterstitialAd != null)
            startAppInterstitialAd = null;
        if (smartInterstitialListener != null)
            smartInterstitialListener = null;
    }

    public void loadInterstitial() {
        loadInterstitial(false);
    }

    private void loadInterstitial(boolean isInternalCall) {
        if (SmartAds.IS_INITIALIZED && !adRequesting || isInternalCall) {
            int size = KeyManager.getAdNetworkPriority().size();
            if (mPriority > size)
                mPriority = 0;
            else
                mPriority++;

            String strPriority = "";
            if (mPriority == size)
                strPriority = "";
            else
                strPriority = KeyManager.getAdNetworkPriority().get(mPriority);

            adRequesting = true;

            switch (strPriority.toLowerCase()) {
                case "google":
                    if (!adMobRequesting && InterConst.adMobLastLoad + AdSettings.adMobInterAdInterval < System.currentTimeMillis()) {
                        Timber.d("AdMob Request Started...");

                        InterConst.adMobLastLoad = System.currentTimeMillis();
                        loadAdMobInterstitial();
                    } else
                        loadInterstitial(true);
                    break;
                case "facebook":
                    if (!facebookRequesting && InterConst.facebookLastLoad + AdSettings.facebookInterAdInterval < System.currentTimeMillis()) {
                        Timber.d("Facebook Request Started...");

                        InterConst.facebookLastLoad = System.currentTimeMillis();
                        loadFacebookInterstitial();
                    } else
                        loadInterstitial(true);
                    break;
                case "startapp":
                    if (!startAppRequesting) {
                        Timber.d("StartApp Request Started...");
                        loadStartAppInterstitial();
                    } else
                        loadInterstitial(true);
                    break;
                case "unity":
                    loadInterstitial(true);
                    break;
                case "":
                    mPriority = -1;
                    adRequesting = false;
                    break;
                default:
                    break;
            }
        }
    }

    public void showInterstitial() {
        showInterstitial(false);
    }

    public void showInterstitial(SmartInterstitialListener smartInterstitialListener) {
        showInterstitial(false);
        mSmartInterstitialListener = smartInterstitialListener;
    }

    private void showInterstitial(boolean isInternalCall) {

        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(mContext)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("Ads Loading")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }

        Thread thread = new Thread(() -> {
            showInterAds(isInternalCall);
        });
        thread.start();
//        if (!isInternalCall) {
//        } else {
//            showInterAds(isInternalCall);
//        }
    }

    private void showInterAds(boolean isInternalCall) {
        if (!adShowing || isInternalCall) {

            int size = KeyManager.getAdNetworkPriority().size();
            if (mShowPriority > size)
                mShowPriority = 0;
            else
                mShowPriority++;

            String strPriority = "";
            if (mShowPriority == size)
                strPriority = "";
            else
                strPriority = KeyManager.getAdNetworkPriority().get(mShowPriority);

            adShowing = true;

            switch (strPriority.toLowerCase()) {
                case "google":
                    while (adMobRequesting) {
                        Timber.d("AdMob Waiting For Request...");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!adMobShowing)
                        showAdMobInterstitial();
                    else
                        showInterstitial(true);
                    break;
                case "facebook":
                    while (facebookRequesting) {
                        Timber.d("Facebook Waiting For Request...");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!facebookShowing)
                        showFacebookInterstitial();
                    else
                        showInterstitial(true);
                    break;
                case "startapp":
                    while (startAppRequesting) {
                        Timber.d("StartApp Waiting For Request...");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!startAppShowing)
                        showStartAppInterstitial();
                    else
                        showInterstitial(true);
                    break;
                case "unity":
                    showInterstitial(true);
                    break;
                case "":
                    mShowPriority = -1;
                    adRequesting = false;
                    adShowing = false;
                    progressHUD.dismiss();
                    progressHUD = null;
                    mContext.runOnUiThread(() -> {
                        smartInterstitialListener.onInterstitialNoAdFound();
                    });
                    break;
                default:
                    break;
            }
        } else {
            if (progressHUD != null && progressHUD.isShowing()) {
                progressHUD.dismiss();
                progressHUD = null;
            }
        }
    }

    private void showAdMobInterstitial() {
        try {
            mContext.runOnUiThread(() -> {
                if (adMobInterstitialAd != null && adMobInterstitialAd.isLoaded()) {
                    adMobInterstitialAd.show();
                    adMobShowing = true;
                } else {
                    showInterstitial(true);
                }
            });
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void showFacebookInterstitial() {
        try {
            if (facebookInterstitialAd != null && facebookInterstitialAd.isAdLoaded()) {
                facebookInterstitialAd.show();
                facebookShowing = true;
            } else {
                showInterstitial(true);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void showStartAppInterstitial() {
        try {
            mContext.runOnUiThread(() -> {
                if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {

                    startAppInterstitialAd.showAd(new AdDisplayListener() {
                        @Override
                        public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
                            startAppShowing = false;
                            smartInterstitialListener.onInterstitialClose();
                        }

                        @Override
                        public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                            smartInterstitialListener.onInterstitialDisplayed();
                        }

                        @Override
                        public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
                            smartInterstitialListener.onInterstitialClick();
                        }

                        @Override
                        public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                            Timber.e("StartApp adNotDisplayed...");
                            startAppShowing = false;
                            showInterstitial(true);
                        }
                    });
                    startAppShowing = true;
                } else {
                    showInterstitial(true);
                }
            });
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void loadAdMobInterstitial() {
        try {
            if (adMobInterstitialAd != null) {
                if (adMobInterstitialAd.isLoading() || adMobInterstitialAd.isLoaded())
                    return;
                else {
                    adMobInterstitialAd = new InterstitialAd(mContext);
                    adMobInterstitialAd.setAdUnitId(KeyManager.getAdMobInterstitialKey());
                }
            } else {
                adMobInterstitialAd = new InterstitialAd(mContext);
                adMobInterstitialAd.setAdUnitId(KeyManager.getAdMobInterstitialKey());
            }

            List<String> testDevices = new ArrayList<>();
            testDevices.add(DEVICE_ID_EMULATOR);

            RequestConfiguration requestConfiguration
                    = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);

            adMobInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adMobRequesting = false;
                    adRequesting = false;
                    Timber.d("AdMob Load Ad Successful...");
                    smartInterstitialListener.onInterstitialRequestSuccess();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    smartInterstitialListener.onInterstitialDisplayed();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    smartInterstitialListener.onInterstitialClose();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Timber.e("AdMob Load Ad Failed...");
                    smartInterstitialListener.onInterstitialRequestFailed("AdMob");
                    loadInterstitial(true);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    smartInterstitialListener.onInterstitialClick();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    smartInterstitialListener.onInterstitialImpressionLogged();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();

                }
            });
            adMobInterstitialAd.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
            adMobRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void loadFacebookInterstitial() {
        try {
            if (facebookInterstitialAd != null) {
                if (facebookInterstitialAd.isAdLoaded() && facebookInterstitialAd.isAdInvalidated()) {
                    return;
                } else {
                    facebookInterstitialAd = new com.facebook.ads.InterstitialAd(mContext, KeyManager.getFbInterstitialKey());
                }
            } else {
                facebookInterstitialAd = new com.facebook.ads.InterstitialAd(mContext, KeyManager.getFbInterstitialKey());
            }


            com.facebook.ads.InterstitialAd.InterstitialAdLoadConfigBuilder interstitialAdLoadConfigBuilder = facebookInterstitialAd.buildLoadAdConfig();
            interstitialAdLoadConfigBuilder.withAdListener(new InterstitialAdExtendedListener() {
                @Override
                public void onInterstitialActivityDestroyed() {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    smartInterstitialListener.onInterstitialDisplayed();
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    smartInterstitialListener.onInterstitialClose();
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Timber.e("Facebook Load Ad Failed...");
                    smartInterstitialListener.onInterstitialRequestFailed("Facebook");
                    loadInterstitial(true);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    facebookRequesting = false;
                    facebookShowing = false;

                    Timber.d("Facebook Load Ad Successful...");
                    smartInterstitialListener.onInterstitialRequestSuccess();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    smartInterstitialListener.onInterstitialClick();
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    smartInterstitialListener.onInterstitialImpressionLogged();
                }

                @Override
                public void onRewardedAdCompleted() {

                }

                @Override
                public void onRewardedAdServerSucceeded() {

                }

                @Override
                public void onRewardedAdServerFailed() {

                }
            });
            interstitialAdLoadConfigBuilder.withCacheFlags(CacheFlag.ALL);
            interstitialAdLoadConfigBuilder.build();

            facebookInterstitialAd.loadAd();
            facebookRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void loadStartAppInterstitial() {
        try {
            if (startAppInterstitialAd != null) {
                if (startAppInterstitialAd.isReady()) {
                    return;
                } else {
                    startAppInterstitialAd = new StartAppAd(mContext);
                }
            } else {
                startAppInterstitialAd = new StartAppAd(mContext);
            }

            AdPreferences adPreferences = new AdPreferences();
            adPreferences.setMinCpm(1.0);

            // StartAppAd.AdMode.AUTOMATIC, adPreferences,

            startAppInterstitialAd.loadAd(new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    startAppRequesting = false;
                    adRequesting = false;

                    Timber.d("StartApp Load Ad Successful...");
                    smartInterstitialListener.onInterstitialRequestSuccess();
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    Timber.e("StartApp Load Ad Failed...");
                    smartInterstitialListener.onInterstitialRequestFailed("StartApp");
                    loadInterstitial(true);
                }
            });
            startAppRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }
}
