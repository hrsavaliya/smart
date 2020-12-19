package com.hk.smart.smartads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hk.smart.BuildConfig;
import com.hk.smart.R;
import com.hk.smart.smartads.beans.AdNetwork;
import com.hk.smart.smartads.beans.Ads;
import com.hk.smart.smartads.beans.AdsData;
import com.hk.smart.smartads.beans.AppSetting;
import com.hk.smart.smartads.beans.BannerAdNetwork;
import com.hk.smart.smartads.beans.InterstitialAdNetwork;
import com.hk.smart.smartads.beans.MediumRectangleAdNetwork;
import com.hk.smart.smartads.beans.NativeAdNetwork;
import com.hk.smart.smartads.beans.NativeBannerAdNetwork;
import com.hk.smart.smartads.beans.VideoRewardAdNetwork;
import com.hk.smart.smartads.interstitial.InterConst;
import com.hk.smart.smartads.listeners.InitializeListener;
import com.hk.smart.smartads.utils.AdMobInitializeHelper;
import com.hk.smart.smartads.utils.Analytics;
import com.hk.smart.smartads.utils.AudienceNetworkInitializeHelper;
import com.hk.smart.smartads.utils.KeyManager;
import com.hk.smart.smartads.utils.Prefs;
import com.hk.smart.smartads.utils.PrefsKey;
import com.hk.smart.smartads.utils.PrioritySorter;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.google.android.gms.ads.doubleclick.PublisherAdRequest.DEVICE_ID_EMULATOR;

public class SmartAds {

    private static String TAG = SmartAds.class.getSimpleName();

    public static boolean IS_INITIALIZED = false;
    public static Ads ads;
    static RequestQueue queue;
    public static List<AppSetting> appSetting;


    public static void initialize(final Context context, InitializeListener initializeListener) {
//        appPreferences = new AppPreferences(context, appId);
        queue = Volley.newRequestQueue(context);

        String appId = context.getPackageName();
        ;

        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(appId)
                .setUseDefaultSharedPreference(true)
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        getAppDataFromApi(context, initializeListener, appId);
        getAdsDataFromApi();
    }

    private static void getAdsDataFromApi() {
        String url = "https://randomvideocall.com/m/" + "com.ads";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseString -> {
                    // Display the first 500 characters of the response string.
                    Timber.tag(TAG).e(responseString.toString());
                    if (responseString.contains("ads")) {
                        Prefs.putString(PrefsKey.ADS_DATA, responseString);

//                        appPreferences.setAdsData(responseString);
                        try {
                            if (!responseString.equals("")) {
                                Gson gson = new Gson();
                                ads = gson.fromJson(responseString, new TypeToken<Ads>() {
                                }.getType());
                            }
                        } catch (Exception ex) {
                            Timber.tag(TAG).e(ex);
                            ex.printStackTrace();
                        }
                    }
                }, error -> {

        });

        queue.add(stringRequest);
    }

    private static void getAppDataFromApi(final Context context, InitializeListener initializeListener, String appId) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://randomvideocall.com/m/" + appId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseString -> {
                    // Display the first 500 characters of the response string.
                    Timber.tag(TAG).e(responseString);
                    if (responseString.contains("ad_networks")) {

                        Prefs.putString(PrefsKey.APP_PACKAGE, appId);
                        Prefs.putString(PrefsKey.APP_DATA, responseString);

//                        appPreferences.setAppData(responseString);
                    }
                    loadAdNetworks(context, initializeListener, appId);
                }, error -> {
            Timber.tag(TAG).e(error);

            loadAdNetworks(context, initializeListener, appId);

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private static void loadAdNetworks(final Context context, InitializeListener initializeListener, String appId) {
        try {
            String appAppData = Prefs.getString(PrefsKey.APP_DATA);// appPreferences.getAppData();
            String appPkg = Prefs.getString(PrefsKey.APP_PACKAGE);// appPreferences.getAppData();

            if (!appAppData.equals("") && !appPkg.equals("")) {
                Gson gson = new Gson();
                AdsData adsData = gson.fromJson(appAppData, new TypeToken<AdsData>() {
                }.getType());


                Collections.sort(adsData.getAdNetworks(), new PrioritySorter());

                setAppSettings(adsData);
                fillKeyManager(adsData);
                boolean result = initSDKs(context, adsData.getAdNetworks());
                if (result) {
                    ((Activity) context).runOnUiThread(() -> {

                        IS_INITIALIZED = true;
                        initializeListener.onSmartAdInitializeSuccess();
                    });
                } else {
                    ((Activity) context).runOnUiThread(() -> initializeListener.onSmartAdInitializeFailed("initSDKs failed"));
                }
            } else {
                getAppDataFromApi(context, initializeListener, "com.base_app.baseguide");
//                ((Activity) context).runOnUiThread(() -> initializeListener.onSmartAdInitializeFailed("Ads data not stored in preferences, contact admin"));
            }
        } catch (Exception ex) {
            Timber.tag(TAG).e(ex);
            ex.printStackTrace();
            ((Activity) context).runOnUiThread(() -> initializeListener.onSmartAdInitializeFailed(ex.getMessage()));
        }
    }

    private static void setAppSettings(AdsData adsData) {
        try {
            if (adsData != null) {
                appSetting = adsData.getAppSetting();

                for (AppSetting appSetting : adsData.getAppSetting()) {
                    if (appSetting.getType().equals("bool"))
                        Prefs.putBoolean(appSetting.getKey(), Boolean.parseBoolean(appSetting.getValue()));
                    if (appSetting.getType().equals("string"))
                        Prefs.putString(appSetting.getKey(), appSetting.getValue());
                    if (appSetting.getType().equals("int"))
                        Prefs.putInt(appSetting.getKey(), Integer.parseInt(appSetting.getValue()));
                }


                InterConst.IMPRESSION_MSG = Prefs.getBoolean("impression_msg", false);
                InterConst.PRELOAD = Prefs.getBoolean("preload_inter", false);

                Prefs.putString("abc", "YES TRUE!!!!");
            }
        } catch (Exception ex) {
            Timber.tag(TAG).e(Objects.requireNonNull(ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private static void fillKeyManager(AdsData adsData) {
        try {
            if (adsData != null) {
                for (AdNetwork adNetwork : adsData.getAdNetworks()) {
                    if (adNetwork.getEnabled()) {
                        KeyManager.setAdNetworkPriority(adNetwork.getAdNetwork());

                        if (adNetwork.getAdNetwork().toLowerCase().equals("facebook")) {
                            if (adNetwork.getBannerAds() != null) {
                                for (BannerAdNetwork bannerAdNetwork : adNetwork.getBannerAds()) {
                                    if (bannerAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setFbBannerKey(KeyManager.TEST_FB_BANNER);
                                        else
                                            KeyManager.setFbBannerKey(bannerAdNetwork.getAdUnitId());

                                        KeyManager.FB_BANNER_SIZE++;
                                    }
                                }
                            }

                            if (adNetwork.getInterstitialAds() != null) {
                                for (InterstitialAdNetwork interstitialAdNetwork : adNetwork.getInterstitialAds()) {
                                    if (interstitialAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setFbInterstitialKey(KeyManager.TEST_FB_INTERSTITIAL);
                                        else {
                                            KeyManager.setFbInterstitialKey(interstitialAdNetwork.getAdUnitId());
                                        }

                                        KeyManager.FB_INTERSTITIAL_SIZE++;
                                    }
                                }
                            }
                            if (adNetwork.getNativeAds() != null) {
                                for (NativeAdNetwork nativeAdNetwork : adNetwork.getNativeAds()) {
                                    if (nativeAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setFbNativeKey(KeyManager.TEST_FB_NATIVE);
                                        else
                                            KeyManager.setFbNativeKey(nativeAdNetwork.getAdUnitId());

                                        KeyManager.FB_NATIVE_SIZE++;
                                    }
                                }
                            }

                            if (adNetwork.getNativeBannerAds() != null) {
                                for (NativeBannerAdNetwork nativeBannerAdNetwork : adNetwork.getNativeBannerAds()) {
                                    if (nativeBannerAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setFbNativeBannerKey(KeyManager.TEST_FB_NATIVE_BANNER);
                                        else
                                            KeyManager.setFbNativeBannerKey(nativeBannerAdNetwork.getAdUnitId());

                                        KeyManager.FB_NATIVE_BANNER_SIZE++;
                                    }
                                }
                            }
                            if (adNetwork.getMediumRectangleAds() != null) {
                                for (MediumRectangleAdNetwork mediumRectangleAdNetwork : adNetwork.getMediumRectangleAds()) {
                                    if (mediumRectangleAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setFbMediumRectangleKey(KeyManager.TEST_FB_MEDIUM_RECTANGLE);
                                        else
                                            KeyManager.setFbMediumRectangleKey(mediumRectangleAdNetwork.getAdUnitId());

                                        KeyManager.FB_MEDIUM_RECTANGLE_SIZE++;
                                    }
                                }
                            }
                        } else if (adNetwork.getAdNetwork().toLowerCase().equals("google")) {
                            if (adNetwork.getBannerAds() != null) {
                                for (BannerAdNetwork bannerAdNetwork : adNetwork.getBannerAds()) {
                                    if (bannerAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setAdMobBannerKey(KeyManager.TEST_AM_BANNER);
                                        else
                                            KeyManager.setAdMobBannerKey(bannerAdNetwork.getAdUnitId());

                                        KeyManager.AM_BANNER_SIZE++;
                                    }
                                }
                            }
                            if (adNetwork.getInterstitialAds() != null) {
                                for (InterstitialAdNetwork interstitialAdNetwork : adNetwork.getInterstitialAds()) {
                                    if (interstitialAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setAdMobInterstitialKey(KeyManager.TEST_AM_INTERSTITIAL);
                                        else
                                            KeyManager.setAdMobInterstitialKey(interstitialAdNetwork.getAdUnitId());

                                        KeyManager.AM_INTERSTITIAL_SIZE++;
                                    }
                                }
                            }
                            if (adNetwork.getNativeAds() != null) {
                                for (NativeAdNetwork nativeAdNetwork : adNetwork.getNativeAds()) {
                                    if (nativeAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setAdMobNativeKey(KeyManager.TEST_AM_NATIVE);
                                        else
                                            KeyManager.setAdMobNativeKey(nativeAdNetwork.getAdUnitId());

                                        KeyManager.AM_NATIVE_SIZE++;
                                    }
                                }
                            }
                            if (adNetwork.getVideoRewardAds() != null) {
                                for (VideoRewardAdNetwork videoRewardAdNetwork : adNetwork.getVideoRewardAds()) {
                                    if (videoRewardAdNetwork.getEnabled()) {
                                        if (BuildConfig.DEBUG)
                                            KeyManager.setAdMobRewardKey(KeyManager.TEST_AM_REWARD);
                                        else
                                            KeyManager.setAdMobRewardKey(videoRewardAdNetwork.getAdUnitId());

                                        KeyManager.AM_REWARD_SIZE++;
                                    }
                                }
                            }
                        } else if (adNetwork.getAdNetwork().toLowerCase().equals("startapp")) {
                            if (adNetwork.getEnabled()) {
                                KeyManager.setStartAppKey(adNetwork.getApiKey());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Timber.tag(TAG).e(Objects.requireNonNull(ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private static boolean initSDKs(Context context, List<AdNetwork> adSDKs) {
        boolean isInitSuccessFull = false;
        try {
            for (int i = 0; i < adSDKs.size(); i++) {
                AdNetwork adNetwork = adSDKs.get(i);
                switch (adNetwork.getAdNetwork().toLowerCase()) {
                    case "facebook":
                        if (adNetwork.getEnabled()) {
                            AudienceNetworkInitializeHelper.initialize(context);

                            AdSettings.addTestDevice("0022c4ca-0bad-4e61-b327-2c06a3541f59");

                            if (InterConst.PRELOAD) {
                                InterConst.facebookInterstitialAd = new InterstitialAd(context, KeyManager.getFbInterstitialKey());
                                com.facebook.ads.InterstitialAd.InterstitialAdLoadConfigBuilder interstitialAdLoadConfigBuilder = InterConst.facebookInterstitialAd.buildLoadAdConfig();
                                interstitialAdLoadConfigBuilder.withAdListener(new AbstractAdListener() {
                                    @Override
                                    public void onError(Ad ad, AdError error) {
                                        super.onError(ad, error);
                                        InterConst.isFbLoading = false;
                                        if (BuildConfig.DEBUG)
                                            Toast.makeText(context, "Facebook Interstitial Ad Failed..", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        super.onAdLoaded(ad);
                                        InterConst.isFbLoading = false;
                                        if (BuildConfig.DEBUG)
                                            Toast.makeText(context, "Facebook Interstitial Ad Loaded..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                InterConst.facebookInterstitialAd.loadAd();
                                InterConst.isFbLoading = true;
                            }
                        }
                        break;
                    case "google":
                        if (adNetwork.getEnabled()) {
                            AdMobInitializeHelper.initialize(context);

                            List<String> testDevices = new ArrayList<>();
                            testDevices.add(DEVICE_ID_EMULATOR);

                            RequestConfiguration requestConfiguration
                                    = new RequestConfiguration.Builder()
                                    .setTestDeviceIds(testDevices)
                                    .build();
                            MobileAds.setRequestConfiguration(requestConfiguration);

                            if (InterConst.PRELOAD) {
                                InterConst.adMobInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
                                InterConst.adMobInterstitialAd.setAdUnitId(KeyManager.getAdMobInterstitialKey());
                                InterConst.adMobInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                        if (BuildConfig.DEBUG)
                                            Toast.makeText(context, "AdMob Interstitial Ad Loaded", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        if (BuildConfig.DEBUG)
                                            Toast.makeText(context, "AdMob Interstitial Ad Failed", Toast.LENGTH_SHORT).show();

                                        Timber.e(loadAdError.getMessage());
                                    }
                                });
                                InterConst.adMobInterstitialAd.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
                            }
                        }
                        break;
                    case "startapp":
                        if (adNetwork.getEnabled()) {

                            StartAppSDK.init(context, KeyManager.getStartAppKey(), true);
                            StartAppAd.disableSplash();

                            if (BuildConfig.DEBUG)
                                StartAppSDK.setTestAdsEnabled(true);

                            if (InterConst.PRELOAD) {
                                InterConst.startAppInterstitialAd = new StartAppAd(context);
                                InterConst.startAppInterstitialAd.loadAd(new AdEventListener() {
                                    @Override
                                    public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                        if (BuildConfig.DEBUG)
                                            Toast.makeText(context, "StartApp Interstitial Ad Loaded", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                        if (BuildConfig.DEBUG)
                                            Toast.makeText(context, "StartApp Interstitial Ad Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            isInitSuccessFull = true;
        } catch (Exception ex) {
            Timber.tag(TAG).e(Objects.requireNonNull(ex.getMessage()));
            ex.printStackTrace();
        }
        return isInitSuccessFull;
    }

    public static void disableSplash() {
        StartAppAd.disableSplash();
    }
}