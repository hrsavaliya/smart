package com.hk.smart.smartads.banner;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hk.smart.BuildConfig;
import com.hk.smart.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.hk.smart.smartads.SmartAds;
import com.hk.smart.smartads.listeners.SmartBannerListener;
import com.hk.smart.smartads.utils.KeyManager;
import com.hk.smart.smartads.utils.ShimmerLayout;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.adsbase.model.AdPreferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.google.android.gms.ads.doubleclick.PublisherAdRequest.DEVICE_ID_EMULATOR;

public class SmartBanner {
    String TAG = SmartBanner.class.getSimpleName();
    private AppCompatActivity mContext;

    List<Integer> loadingViewIds = new ArrayList<>();
    ViewGroup adViewGroup;

    boolean logEvents = true;


    private boolean adRequesting = false;
    private boolean adMobRequesting = false;
    private boolean facebookRequesting = false;
    private boolean startAppRequesting = false;

    private AdView adMobBannerAd = null;
    private com.facebook.ads.AdView facebookBannerAd = null;
    private Banner startAppAdBannerAd = null;
    private Mrec startAppMrec = null;

    private int mPriority = -1;

    private SmartBannerListener mSmartBannerListener = null;

    private SmartBannerListener smartBannerListener = new SmartBannerListener() {
        @Override
        public void onBannerRequestSuccess() {
            mPriority = -1;
            adRequesting = false;
            adMobRequesting = false;
            facebookRequesting = false;
            startAppRequesting = false;

            Log.d(TAG, "onBannerRequestSuccess...");
            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerRequestSuccess();
        }

        @Override
        public void onBannerDisplayed() {
            Log.d(TAG, "onBannerDisplayed...");
            adRequesting = false;
            adMobRequesting = false;
            facebookRequesting = false;
            startAppRequesting = false;

            mPriority = -1;

            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerDisplayed();
        }

        @Override
        public void onBannerRequestFailed() {
            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerRequestFailed();
        }

        @Override
        public void onBannerRequestFailed(String error) {
            Log.e(TAG, error + " onBannerRequestFailed...");

            if (logEvents && BuildConfig.DEBUG)
                Toast.makeText(mContext, error + " Banner Ad Failed..", Toast.LENGTH_SHORT).show();


            if (error.toLowerCase().contains("admob")) {
                adMobRequesting = false;
                adMobBannerAd = null;
            } else if (error.toLowerCase().contains("facebook")) {
                facebookRequesting = false;
            } else if (error.toLowerCase().contains("startapp")) {
                startAppRequesting = false;
            }

            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerRequestFailed();
        }

        @Override
        public void onBannerImpressionLogged() {
            Log.d(TAG, "onBannerImpressionLogged...");
            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerImpressionLogged();
        }

        @Override
        public void onBannerClick() {
            Log.d(TAG, "onBannerClick...");
            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerClick();
        }

        @Override
        public void onBannerClose() {
            Log.d(TAG, "onBannerClose...");
            if (mSmartBannerListener != null)
                mSmartBannerListener.onBannerClose();
        }
    };

    public SmartBanner(AppCompatActivity context) {
        mContext = context;
    }

    public void setInterstitialListener(SmartBannerListener smartBannerListener) {
        mSmartBannerListener = smartBannerListener;
    }

    public void loadBanner(ViewGroup viewGroup, BannerType bannerType) {
        addLoadingView(viewGroup);
        loadBanner(false, bannerType);
    }


    private void addLoadingView(ViewGroup viewGroup) {
        adViewGroup = viewGroup;

        ShimmerLayout shimmerLayout = new ShimmerLayout(mContext);
        int id = (int) viewGroup.getId() + 999;
        shimmerLayout.setId(id);

        ViewGroup.LayoutParams lpView = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        shimmerLayout.setLayoutParams(lpView);
        shimmerLayout.setBackgroundColor(mContext.getResources().getColor(R.color.dark_alpha70));

//        shimmerLayout.setShimmerColor(mContext.getResources().getColor(R.color.dark_transparent_60));

//        FrameLayout frameLayout = new FrameLayout(mContext);
//        frameLayout.setLayoutParams(lpView);
//        frameLayout.setBackgroundColor(mContext.getResources().getColor(R.color.dark_transparent_40));

//        shimmerLayout.addView(frameLayout);

//        frameLayout.addView(progressBar);

        TextView textView = new TextView(mContext);
        textView.setText("PLEASE WAIT LOADING...");
        textView.setTextSize(24.0f);
        textView.setLayoutParams(lpView);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(mContext.getResources().getColor(R.color.dark_alpha70));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);

        shimmerLayout.addView(textView);
//      frameLayout.addView(textView);

        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        LinearLayout ll = new LinearLayout(mContext);
        ll.setBackgroundColor(mContext.getResources().getColor(R.color.dark_alpha70));
        ll.setGravity(Gravity.CENTER);
        ll.addView(progressBar);

        viewGroup.addView(ll);

        if (!loadingViewIds.contains(id))
            loadingViewIds.add(id);
    }

    private void loadBanner(boolean isInternalCall, BannerType bannerType) {
        if (SmartAds.IS_INITIALIZED && !adRequesting || isInternalCall) {
//            if (!isInternalCall)
//                Log.e(TAG, "this call is from parent");

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
                    if (!adMobRequesting) {
                        Log.d(TAG, "AdMob Request Started...");
                        loadAdMobBanner(bannerType);
                    } else
                        loadBanner(true, bannerType);
                    break;
                case "facebook":
                    if (!facebookRequesting) {
                        Log.d(TAG, "Facebook Request Started...");
                        loadFacebookBanner(bannerType);
                    } else
                        loadBanner(true, bannerType);
                    break;
                case "startapp":
                    if (!startAppRequesting) {
                        Log.d(TAG, "StartApp Request Started...");
                        loadStartAppBanner(bannerType);
                    } else
                        loadBanner(true, bannerType);
                    break;
                case "unity":
                    loadBanner(true, bannerType);
                    break;
                case "":
                    mPriority = -1;
                    adRequesting = false;

                    if (adViewGroup != null) {
                        adViewGroup.removeAllViews();
                        adViewGroup.setBackgroundColor(Color.TRANSPARENT);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    private void loadAdMobBanner(BannerType bannerType) {
        try {
//            if (adMobBannerAd != null) {
//                if (adMobBannerAd.isLoaded())
//                    return;
//                else {
//                    adMobInterstitialAd = new InterstitialAd(mContext);
//                    adMobInterstitialAd.setAdUnitId(KeyManager.getAdMobInterstitialKey());
//                }
//            } else {
//                adMobInterstitialAd = new InterstitialAd(mContext);
//                adMobInterstitialAd.setAdUnitId(KeyManager.getAdMobInterstitialKey());
//            }

            List<String> testDevices = new ArrayList<>();
            testDevices.add(DEVICE_ID_EMULATOR);

            RequestConfiguration requestConfiguration
                    = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);

            AdSize adSize = getAdSize();

            adMobBannerAd = new AdView(mContext);
            if (bannerType == BannerType.SMART_BANNER)
                adMobBannerAd.setAdSize(adSize);
            else if (bannerType == BannerType.MEDIUM_RECTANGLE)
                adMobBannerAd.setAdSize(AdSize.MEDIUM_RECTANGLE);


            adMobBannerAd.setAdUnitId(KeyManager.getAdMobBannerKey());
            adMobBannerAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adMobRequesting = false;
                    adRequesting = false;

                    adViewGroup.removeAllViews();
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    adViewGroup.addView(adMobBannerAd, params);

                    if (BuildConfig.DEBUG)
                        Toast.makeText(mContext, "AdMob Banner Ad Loaded.", Toast.LENGTH_SHORT).show();

                    Timber.d("AdMob Load Ad Successful...");
                    smartBannerListener.onBannerRequestSuccess();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    smartBannerListener.onBannerDisplayed();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    smartBannerListener.onBannerClose();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Timber.tag(TAG).e("AdMob Load Ad Failed...");
                    smartBannerListener.onBannerRequestFailed("AdMob");
                    loadBanner(true, bannerType);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    smartBannerListener.onBannerClick();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    smartBannerListener.onBannerImpressionLogged();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();

                }
            });

            AdRequest adRequest = new AdRequest.Builder().build();
            adMobBannerAd.loadAd(adRequest);
            adMobRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = mContext.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mContext, adWidth);
    }

    private void loadFacebookBanner(BannerType bannerType) {
        try {
            if (bannerType == BannerType.SMART_BANNER) {
                facebookBannerAd = new com.facebook.ads.AdView(mContext, KeyManager.getFbBannerKey(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            } else {
                facebookBannerAd = new com.facebook.ads.AdView(mContext, KeyManager.getFbMediumRectangleKey(), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
            }

            com.facebook.ads.AdView.AdViewLoadConfigBuilder adViewLoadConfigBuilder = facebookBannerAd.buildLoadAdConfig();
            adViewLoadConfigBuilder.withAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e(TAG, "Facebook Load Ad Failed...");
                    smartBannerListener.onBannerRequestFailed("Facebook");
                    loadBanner(true, bannerType);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    facebookRequesting = false;

                    adViewGroup.removeAllViews();
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    adViewGroup.addView(facebookBannerAd, params);

                    if (BuildConfig.DEBUG)
                        Toast.makeText(mContext, "Facebook Banner Ad Loaded.", Toast.LENGTH_SHORT).show();

                    Timber.d("Facebook Load Ad Successful...");
                    smartBannerListener.onBannerRequestSuccess();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    smartBannerListener.onBannerClick();
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    smartBannerListener.onBannerImpressionLogged();
                }
            });

            facebookBannerAd.loadAd();
            facebookRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private void loadStartAppBanner(BannerType bannerType) {
        try {
//            if (startAppAdInterstitialAd != null) {
//                if (startAppAdInterstitialAd.isReady()) {
//                    return;
//                } else {
//                    startAppAdInterstitialAd = new StartAppAd(mContext);
//                }
//            } else {
//                startAppAdInterstitialAd = new StartAppAd(mContext);
//            }

            AdPreferences adPreferences = new AdPreferences();
            adPreferences.setMinCpm(1.0);

            // StartAppAd.AdMode.AUTOMATIC, adPreferences,

            if (bannerType == BannerType.SMART_BANNER) {
                startAppAdBannerAd = new Banner(mContext, new BannerListener() {
                    @Override
                    public void onReceiveAd(View view) {
                        startAppRequesting = false;
                        adRequesting = false;

                        adViewGroup.removeAllViews();
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        adViewGroup.addView(startAppAdBannerAd, params);

                        if (BuildConfig.DEBUG)
                            Toast.makeText(mContext, "StartApp Banner Ad Loaded.", Toast.LENGTH_SHORT).show();

                        Timber.d("StartApp Load Ad Successful...");
                        smartBannerListener.onBannerRequestSuccess();
                    }

                    @Override
                    public void onFailedToReceiveAd(View view) {
                        Timber.e("StartApp Load Ad Failed...");
                        smartBannerListener.onBannerRequestFailed("StartApp");
                        loadBanner(true, bannerType);
                    }

                    @Override
                    public void onImpression(View view) {
                        smartBannerListener.onBannerImpressionLogged();
                    }

                    @Override
                    public void onClick(View view) {
                        smartBannerListener.onBannerClick();
                    }
                });
                startAppAdBannerAd.loadAd();
            } else if (bannerType == BannerType.MEDIUM_RECTANGLE) {
                startAppMrec = new Mrec(mContext);
                adViewGroup.removeAllViews();
                adViewGroup.addView(startAppMrec);
            }

            startAppRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }
}
