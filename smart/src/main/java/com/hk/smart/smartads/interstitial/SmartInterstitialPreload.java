package com.hk.smart.smartads.interstitial;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.hk.smart.BuildConfig;
import com.hk.smart.smartads.listeners.SmartAdListener;
import com.hk.smart.smartads.utils.AdLoadingDialog;
import com.hk.smart.smartads.utils.KeyManager;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.model.AdPreferences;

import timber.log.Timber;

public class SmartInterstitialPreload {

    private String TAG = SmartInterstitialPreload.class.getSimpleName();
    AppCompatActivity mContext;

    boolean logEvents = false;

    private AdLoadingDialog progressDialog;

    private boolean adShowing = false;

    private int mShowPriority = -1;

    private SmartAdListener mSmartAdListener = null;

    SmartAdListener smartAdListener = new SmartAdListener() {
        @Override
        public void onAdRequestSuccess() {
            Timber.tag(TAG).d("onAdRequestSuccess...");
            if (mSmartAdListener != null)
                mSmartAdListener.onAdRequestSuccess();
        }

        @Override
        public void onAdDisplayed() {
            Timber.tag(TAG).d("onAdDisplayed...");
            adShowing = false;
            mShowPriority = -1;
            removeProgressDialog();

            if (mSmartAdListener != null)
                mSmartAdListener.onAdDisplayed();
        }

        @Override
        public void onAdRequestFailed() {
            if (mSmartAdListener != null)
                mSmartAdListener.onAdRequestFailed();
        }

        @Override
        public void onAdRequestFailed(String error) {
            Timber.tag(TAG).e("%s onAdRequestFailed...", error);
            if (error.toLowerCase().contains("admob")) {
                if (InterConst.adMobInterstitialAd != null)
                    InterConst.adMobInterstitialAd = null;
            } else if (error.toLowerCase().contains("facebook")) {
                if (InterConst.facebookInterstitialAd != null)
                    InterConst.facebookInterstitialAd.destroy();
                InterConst.facebookInterstitialAd = null;
            } else if (error.toLowerCase().contains("startapp")) {
                if (InterConst.startAppInterstitialAd != null)
                    InterConst.startAppInterstitialAd = null;
            }

            if (mSmartAdListener != null) {
                mSmartAdListener.onAdRequestFailed(error);
                mSmartAdListener.onAdRequestFailed();
            }
        }

        @Override
        public void onAdImpressionLogged() {
            Timber.tag(TAG).d("onAdImpressionLogged...");
            if (mSmartAdListener != null)
                mSmartAdListener.onAdImpressionLogged();
        }

        @Override
        public void onAdClicked() {
            Timber.tag(TAG).d("onAdClicked...");
            if (mSmartAdListener != null)
                mSmartAdListener.onAdClicked();
        }

        @Override
        public void onAdClosed() {
            Timber.tag(TAG).d("onAdClose...");
            if (mSmartAdListener != null)
                mSmartAdListener.onAdClosed();

            mSmartAdListener = null;
        }

        @Override
        public void onAdNotFound() {
            super.onAdNotFound();
            Timber.tag(TAG).d("onAdNotFound...");
            if (mSmartAdListener != null)
                mSmartAdListener.onAdNotFound();
        }
    };

    public SmartInterstitialPreload(AppCompatActivity context) {
        mContext = context;
        progressDialog = new AdLoadingDialog(mContext);
    }

    public void showInterstitial() {
        showInterstitial(false);
    }

    public void showInterstitial(SmartAdListener smartAdListener) {
        showInterstitial(false);
        mSmartAdListener = smartAdListener;
    }

    void showInterstitial(boolean isInternalCall) {
        if (progressDialog == null) {
            progressDialog = new AdLoadingDialog(mContext);
        }
        progressDialog.show();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showInterAds(isInternalCall);
        });
        thread.start();
    }

    private void showInterAds(boolean isInternalCall) {
        if (!adShowing || isInternalCall) {

            int size = KeyManager.getAdNetworkPriority().size();
            if (mShowPriority > size)
                mShowPriority = 0;
            else
                mShowPriority++;

            String strPriority;
            if (mShowPriority == size)
                strPriority = "";
            else
                strPriority = KeyManager.getAdNetworkPriority().get(mShowPriority);

            adShowing = true;

            switch (strPriority.toLowerCase()) {
                case "google":
                    showAdMobInterstitial();
                    break;
                case "facebook":
                    showFacebookInterstitial();
                    break;
                case "startapp":
                    showStartAppInterstitial();
                    break;
                case "unity":
                    showInterstitial(true);
                    break;
                case "":
                    adShowing = false;
                    mShowPriority = -1;
                    removeProgressDialog();
                    if (InterConst.PRELOAD) {
                        mContext.runOnUiThread(() -> {
                            loadAdMobInterstitial();
                            loadFacebookInterstitial();
                            loadStartAppInterstitial();
                        });
                    }
                    mContext.runOnUiThread(() -> smartAdListener.onAdNotFound());
                    break;
                default:
                    break;
            }
        } else {
            removeProgressDialog();
        }
    }

    void loadAdMobInterstitial() {
        if (!InterConst.PRELOAD || (InterConst.adMobInterstitialAd != null && !InterConst.adMobInterstitialAd.isLoading() && !InterConst.adMobInterstitialAd.isLoaded())) {
            InterConst.adMobInterstitialAd = new com.google.android.gms.ads.InterstitialAd(mContext);
            InterConst.adMobInterstitialAd.setAdUnitId(KeyManager.getAdMobInterstitialKey());

            InterConst.adMobInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    smartAdListener.onAdRequestSuccess();
                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "AdMob Interstitial Ad Loaded", Toast.LENGTH_SHORT).show();

                    if (!InterConst.PRELOAD) {
                        InterConst.adMobInterstitialAd.setAdListener(null);
                        InterConst.adMobInterstitialAd.setAdListener(new AdMobAdListener(SmartInterstitialPreload.this));
                        InterConst.adMobInterstitialAd.show();
                    }
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    smartAdListener.onAdRequestFailed();
                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "AdMob Interstitial Ad Failed", Toast.LENGTH_SHORT).show();

                    if (!InterConst.PRELOAD) {
                        showInterstitial(true);
                    }
                }
            });

            InterConst.adMobInterstitialAd.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
        }
    }

    private void showAdMobInterstitial() {
        try {
            mContext.runOnUiThread(() -> {
                if (!InterConst.PRELOAD) {
                    loadAdMobInterstitial();
                    return;
                }

                if (InterConst.adMobInterstitialAd != null && InterConst.adMobInterstitialAd.isLoaded()) {
                    InterConst.adMobInterstitialAd.setAdListener(new AdMobAdListener(this));
//                            new AdListener() {
//                        @Override
//                        public void onAdOpened() {
//                            super.onAdOpened();
//                            smartAdListener.onAdDisplayed();
//                        }
//
//                        @Override
//                        public void onAdClosed() {
//                            super.onAdClosed();
//                            smartAdListener.onAdClosed();
//                            if (InterConst.PRELOAD) {
//                                loadAdMobInterstitial();
//                            }
//                        }
//
//                        @Override
//                        public void onAdImpression() {
//                            super.onAdImpression();
//                            smartAdListener.onAdImpressionLogged();
//                            if (logEvents && BuildConfig.DEBUG)
//                                Toast.makeText(mContext, "AdMob Interstitial Impression Logged", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    InterConst.adMobInterstitialAd.show();
                } else {
                    showInterstitial(true);
                    if (InterConst.PRELOAD) {
                        loadAdMobInterstitial();
                    }
                }
            });
        } catch (Exception e3) {
            e3.printStackTrace();
            showInterstitial(true);
        }
    }

    protected void loadFacebookInterstitial() {
        if (!InterConst.PRELOAD || (InterConst.facebookInterstitialAd != null && !InterConst.isFbLoading && !InterConst.facebookInterstitialAd.isAdLoaded())) {
            if (InterConst.PRELOAD) {
                InterConst.facebookInterstitialAd.destroy();
            }
            InterConst.facebookInterstitialAd = new com.facebook.ads.InterstitialAd(mContext, KeyManager.getFbInterstitialKey());
            InterConst.isFbLoading = true;

            com.facebook.ads.InterstitialAd.InterstitialAdLoadConfigBuilder interstitialAdLoadConfigBuilder = InterConst.facebookInterstitialAd.buildLoadAdConfig();
            interstitialAdLoadConfigBuilder.withAdListener(new AbstractAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    super.onError(ad, error);
                    smartAdListener.onAdRequestFailed();
                    InterConst.isFbLoading = false;
                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "Facebook Interstitial Ad Failed", Toast.LENGTH_SHORT).show();

                    if (!InterConst.PRELOAD) {
                        showInterstitial(true);
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    super.onAdLoaded(ad);
                    smartAdListener.onAdRequestSuccess();
                    InterConst.facebookAdsCount++;
                    InterConst.isFbLoading = false;
                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "Facebook Interstitial Ad Loaded..", Toast.LENGTH_SHORT).show();

                    if (!InterConst.PRELOAD) {
                        InterConst.facebookInterstitialAd.show();

                        com.facebook.ads.InterstitialAd.InterstitialAdLoadConfigBuilder interstitialAdLoadConfigBuilder = InterConst.facebookInterstitialAd.buildLoadAdConfig();
                        interstitialAdLoadConfigBuilder.withAdListener(null);
                        interstitialAdLoadConfigBuilder.withAdListener(new FbAdListener(SmartInterstitialPreload.this));
                    }
                }
            });

            InterConst.facebookInterstitialAd.loadAd();
        }
        Timber.tag(TAG).e(String.valueOf(InterConst.facebookAdsCount));
    }

    private void showFacebookInterstitial() {
        try {
            if (!InterConst.PRELOAD) {
                loadFacebookInterstitial();
                return;
            }

            if (InterConst.facebookInterstitialAd != null && InterConst.facebookInterstitialAd.isAdLoaded() && !InterConst.facebookInterstitialAd.isAdInvalidated()) {
                com.facebook.ads.InterstitialAd.InterstitialAdLoadConfigBuilder interstitialAdLoadConfigBuilder = InterConst.facebookInterstitialAd.buildLoadAdConfig();
                interstitialAdLoadConfigBuilder.withAdListener(new FbAdListener(this));

//                        new AbstractAdListener() {
//                    @Override
//                    public void onInterstitialDisplayed(Ad ad) {
//                        super.onInterstitialDisplayed(ad);
//                        smartAdListener.onAdDisplayed();
//                    }
//
//                    @Override
//                    public void onInterstitialDismissed(Ad ad) {
//                        super.onInterstitialDismissed(ad);
//                        smartAdListener.onAdClosed();
//                        if (InterConst.PRELOAD) {
//                            loadFacebookInterstitial();
//                        }
//                    }
//
//                    @Override
//                    public void onLoggingImpression(Ad ad) {
//                        super.onLoggingImpression(ad);
//                        smartAdListener.onAdImpressionLogged();
//                        if (logEvents && BuildConfig.DEBUG)
//                            Toast.makeText(mContext, "Facebook Interstitial Impression Logged", Toast.LENGTH_SHORT).show();
//
//                        if (InterConst.IMPRESSION_MSG)
//                            Toast.makeText(mContext, "Impression", Toast.LENGTH_SHORT).show();
//                    }
//                });
                interstitialAdLoadConfigBuilder.withCacheFlags(CacheFlag.ALL);
                interstitialAdLoadConfigBuilder.build();
                InterConst.facebookInterstitialAd.show();
            } else {
                showInterstitial(true);
                if (InterConst.PRELOAD) {
                    loadFacebookInterstitial();
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            showInterstitial(true);
        }
    }

    void loadStartAppInterstitial() {
        if (!InterConst.PRELOAD || (InterConst.startAppInterstitialAd != null)) {
            AdPreferences adPreferences = new AdPreferences();
            InterConst.startAppInterstitialAd = new StartAppAd(mContext);
            InterConst.startAppInterstitialAd.loadAd(StartAppAd.AdMode.AUTOMATIC, adPreferences, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    smartAdListener.onAdRequestSuccess();
                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "StartApp Interstitial Ad Loaded..", Toast.LENGTH_SHORT).show();

                    if (!InterConst.PRELOAD) {
                        InterConst.startAppInterstitialAd.showAd(new StartAppAdListener(SmartInterstitialPreload.this));
//                                new AdDisplayListener() {
//                            @Override
//                            public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
//                                smartAdListener.onAdClosed();
//                            }
//
//                            @Override
//                            public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
//                                smartAdListener.onAdDisplayed();
//                            }
//
//                            @Override
//                            public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
//                                smartAdListener.onAdClicked();
//                            }
//
//                            @Override
//                            public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
//                                Timber.tag(TAG).e("StartApp adNotDisplayed...");
//                                showInterstitial(true);
//                            }
//                        });
                    }
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    smartAdListener.onAdRequestFailed();
                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "StartApp Interstitial Ad Failed..", Toast.LENGTH_SHORT).show();
                    if (!InterConst.PRELOAD) {
                        showInterstitial(true);
                    }
                }
            });
        }
    }

    private void showStartAppInterstitial() {
        try {
            mContext.runOnUiThread(() -> {
                if (!InterConst.PRELOAD) {
                    loadStartAppInterstitial();
                    return;
                }

                if (InterConst.startAppInterstitialAd != null) {
                    InterConst.startAppInterstitialAd.showAd(new StartAppAdListener(SmartInterstitialPreload.this));


//                            new AdDisplayListener() {
//                        @Override
//                        public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
//                            smartAdListener.onAdClosed();
//                            if (InterConst.PRELOAD) {
//                                loadAdMobInterstitial();
//                                loadFacebookInterstitial();
//                                loadStartAppInterstitial();
//                            }
//                        }
//
//                        @Override
//                        public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
//                            smartAdListener.onAdDisplayed();
//                        }
//
//                        @Override
//                        public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
//                            smartAdListener.onAdClicked();
//                        }
//
//                        @Override
//                        public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
//                            Timber.tag(TAG).e("StartApp adNotDisplayed...");
//                            showInterstitial(true);
//                        }
//                    });
                } else {
                    showInterstitial(true);
                    if (InterConst.PRELOAD) {
                        loadStartAppInterstitial();
                    }
                }
            });
        } catch (Exception e3) {
            e3.printStackTrace();
            showInterstitial(true);
        }
    }

    private void removeProgressDialog() {
        if (progressDialog != null && SmartInterstitialPreload.this.progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}