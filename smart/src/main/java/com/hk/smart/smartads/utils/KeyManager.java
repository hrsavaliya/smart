package com.hk.smart.smartads.utils;

import com.hk.smart.BuildConfig;

import java.util.ArrayList;

public class KeyManager {

    public static final String TEST_FB_BANNER = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"; // YOUR_PLACEMENT_ID
    public static final String TEST_FB_INTERSTITIAL = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"; //
    public static final String TEST_FB_NATIVE = "VID_HD_9_16_39S_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static final String TEST_FB_NATIVE_BANNER = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    public static final String TEST_FB_MEDIUM_RECTANGLE = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

    public static final String TEST_AM_BANNER = "ca-app-pub-3940256099942544/6300978111";//
    public static final String TEST_AM_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";// /6499/example/interstitial
    public static final String TEST_AM_NATIVE = "ca-app-pub-3940256099942544/2247696110";//
    public static final String TEST_AM_REWARD = "ca-app-pub-3940256099942544/5224354917";//

    private static final ArrayList<String> AD_NETWORK_PRIORITY = new ArrayList<>();

    private static final ArrayList<String> LIVE_AM_BANNER = new ArrayList<>();
    private static final ArrayList<String> LIVE_AM_INTERSTITIAL = new ArrayList<>();
    private static final ArrayList<String> LIVE_AM_NATIVE = new ArrayList<>();
    private static final ArrayList<String> LIVE_AM_REWARD = new ArrayList<>();

    private static final ArrayList<String> LIVE_FB_BANNER = new ArrayList<>();
    private static final ArrayList<String> LIVE_FB_INTERSTITIAL = new ArrayList<>();
    private static final ArrayList<String> LIVE_FB_NATIVE = new ArrayList<>();
    private static final ArrayList<String> LIVE_FB_NATIVE_BANNER = new ArrayList<>();
    private static final ArrayList<String> LIVE_FB_MEDIUM_RECTANGLE = new ArrayList<>();

    private static String LIVE_ST = "";
    private static String TEST_ST = "";

    public static int AM_BANNER_SIZE = 0;
    public static int AM_INTERSTITIAL_SIZE = 0;
    public static int AM_NATIVE_SIZE = 0;
    public static int AM_REWARD_SIZE = 0;

    public static int FB_BANNER_SIZE = 0;
    public static int FB_INTERSTITIAL_SIZE = 0;
    public static int FB_NATIVE_SIZE = 0;
    public static int FB_NATIVE_BANNER_SIZE = 0;
    public static int FB_MEDIUM_RECTANGLE_SIZE = 0;

    public static int getNextId(int maxIndex) {
        return ((int) (Math.random() * (maxIndex)));
    }

    // Ad Network Priority
    public static ArrayList<String> getAdNetworkPriority() {
        return AD_NETWORK_PRIORITY;
    }

    public static void setAdNetworkPriority(String adNetworkPriority) {
        if (!AD_NETWORK_PRIORITY.contains(adNetworkPriority))
            AD_NETWORK_PRIORITY.add(adNetworkPriority);
    }

    // Get Facebook Ids
    public static String getFbBannerKey() {
        if (FB_BANNER_SIZE > 0) {
            try {
                return LIVE_FB_BANNER.get(getNextId(FB_BANNER_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_FB_BANNER.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setFbBannerKey(String fbBannerKey) {
        if (!LIVE_FB_BANNER.contains(fbBannerKey) || BuildConfig.DEBUG)
            LIVE_FB_BANNER.add(fbBannerKey);
    }

    public static String getFbInterstitialKey() {
        if (FB_INTERSTITIAL_SIZE > 0) {
            try {
                return LIVE_FB_INTERSTITIAL.get(getNextId(FB_INTERSTITIAL_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_FB_INTERSTITIAL.get(0);
            }
        } else {
            return "";
        }
    }

    //
    public static void setFbInterstitialKey(String fbInterstitialKey) {
        if (!LIVE_FB_INTERSTITIAL.contains(fbInterstitialKey) || BuildConfig.DEBUG)
            LIVE_FB_INTERSTITIAL.add(fbInterstitialKey);
    }

    public static String getFbNativeKey() {
        if (FB_NATIVE_SIZE > 0) {
            try {
                return LIVE_FB_NATIVE.get(getNextId(FB_NATIVE_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_FB_NATIVE.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setFbNativeKey(String fbNativeKey) {
        if (!LIVE_FB_NATIVE.contains(fbNativeKey) || BuildConfig.DEBUG)
            LIVE_FB_NATIVE.add(fbNativeKey);
    }

    public static String getFbNativeBannerKey() {
        if (FB_NATIVE_BANNER_SIZE > 0) {
            try {
                return LIVE_FB_NATIVE_BANNER.get(getNextId(FB_NATIVE_BANNER_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_FB_NATIVE_BANNER.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setFbNativeBannerKey(String fbNativeBannerKey) {
        if (!LIVE_FB_NATIVE_BANNER.contains(fbNativeBannerKey) || BuildConfig.DEBUG)
            LIVE_FB_NATIVE_BANNER.add(fbNativeBannerKey);
    }

    public static String getFbMediumRectangleKey() {
        if (FB_MEDIUM_RECTANGLE_SIZE > 0) {
            try {
                return LIVE_FB_MEDIUM_RECTANGLE.get(getNextId(FB_MEDIUM_RECTANGLE_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_FB_MEDIUM_RECTANGLE.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setFbMediumRectangleKey(String fbMediumRectangleKey) {
        if (!LIVE_FB_MEDIUM_RECTANGLE.contains(fbMediumRectangleKey) || BuildConfig.DEBUG)
            LIVE_FB_MEDIUM_RECTANGLE.add(fbMediumRectangleKey);
    }

    // Get AdMob Ids
    public static String getAdMobBannerKey() {
        if (AM_BANNER_SIZE > 0) {
            try {
                return LIVE_AM_BANNER.get(getNextId(AM_BANNER_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_AM_BANNER.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setAdMobBannerKey(String adMobBannerKey) {
        if (!LIVE_AM_BANNER.contains(adMobBannerKey) || BuildConfig.DEBUG)
            LIVE_AM_BANNER.add(adMobBannerKey);
    }

    public static String getAdMobInterstitialKey() {
        if (AM_INTERSTITIAL_SIZE > 0) {
            try {
                return LIVE_AM_INTERSTITIAL.get(getNextId(AM_INTERSTITIAL_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_AM_INTERSTITIAL.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setAdMobInterstitialKey(String adMobInterstitialKey) {
        if (!LIVE_AM_INTERSTITIAL.contains(adMobInterstitialKey) || BuildConfig.DEBUG)
            LIVE_AM_INTERSTITIAL.add(adMobInterstitialKey);
    }

    public static String getAdMobNativeKey() {
        if (AM_NATIVE_SIZE > 0) {
            try {
                return LIVE_AM_NATIVE.get(getNextId(AM_NATIVE_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_AM_NATIVE.get(0);
            }
        } else {
            return "";
        }
    }


    public static void setAdMobNativeKey(String adMobNativeKey) {
        if (!LIVE_AM_NATIVE.contains(adMobNativeKey) || BuildConfig.DEBUG)
            LIVE_AM_NATIVE.add(adMobNativeKey);
    }

    public static String getAdMobRewardKey() {
        if (AM_REWARD_SIZE > 0) {
            try {
                return LIVE_AM_REWARD.get(getNextId(AM_REWARD_SIZE));
            } catch (Exception e) {
                e.printStackTrace();
                return LIVE_AM_REWARD.get(0);
            }
        } else {
            return "";
        }
    }

    public static void setAdMobRewardKey(String adMobRewardKey) {
        if (!LIVE_AM_REWARD.contains(adMobRewardKey) || BuildConfig.DEBUG)
            LIVE_AM_REWARD.add(adMobRewardKey);
    }

    //Get Startapp Ids
    public static String getStartAppKey() {
        if (BuildConfig.DEBUG) {
            return TEST_ST;
        } else {
            return LIVE_ST;
        }
    }

    public static void setStartAppKey(String startAppKey) {
        if (BuildConfig.DEBUG) {
            TEST_ST = "ca-app-pub-3940256099942544/5224354917";
        } else {
            LIVE_ST = startAppKey;
        }
    }


}