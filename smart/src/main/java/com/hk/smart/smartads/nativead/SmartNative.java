package com.hk.smart.smartads.nativead;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;

import com.hk.smart.BuildConfig;
import com.hk.smart.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.hk.smart.smartads.listeners.SmartAdListener;
import com.hk.smart.smartads.utils.KeyManager;
import com.hk.smart.smartads.utils.ShimmerLayout;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SmartNative {

    private String TAG = SmartNative.class.getSimpleName();
    private Activity mContext;

    private boolean logEvents = false;

    private boolean loadRequest = false;
    private boolean adRequesting = false;
    private boolean adMobRequesting = false;
    private boolean facebookRequesting = false;
    private boolean startAppRequesting = false;

    private boolean adShowing = false;
    private boolean adMobShowing = false;
    private boolean facebookShowing = false;
    private boolean startAppShowing = false;

    private boolean adLoaded = false;
    private boolean adMobLoaded = false;
    private boolean facebookLoaded = false;
    private boolean startAppLoaded = false;

    List<Integer> loadingViewIds = new ArrayList<>();
    ViewGroup adViewGroup;

    private UnifiedNativeAd adMobNativeAd = null;
    private AdLoader adLoader = null;
    private NativeAd facebookNativeAd = null;
    private StartAppNativeAd startAppAdNativeAd = null;

    private int mPriority = -1;
    private int mShowPriority = -1;

    private SmartAdListener mSmartAdListener = null;

    private SmartAdListener smartAdListener = new SmartAdListener() {
        @Override
        public void onAdRequestSuccess() {
            Log.d(TAG, "onAdRequestSuccess...");

            mPriority = -1;
            adRequesting = false;
            adMobRequesting = false;
            facebookRequesting = false;
            startAppRequesting = false;

            adLoaded = true;

            if (mSmartAdListener != null)
                mSmartAdListener.onAdRequestSuccess();
        }

        @Override
        public void onAdDisplayed() {
            Log.d(TAG, "onAdDisplayed...");

            adRequesting = false;
            adMobRequesting = false;
            facebookRequesting = false;
            startAppRequesting = false;

            adShowing = false;
            adMobShowing = false;
            facebookShowing = false;
            startAppShowing = false;

            adLoaded = false;

            mPriority = -1;
            mShowPriority = -1;

//            Toasty.normal(mContext, "Please have look for 5 second...").show();

            if (mSmartAdListener != null)
                mSmartAdListener.onAdDisplayed();
        }

        @Override
        public void onAdRequestFailed() {
            Log.e(TAG, "onAdRequestFailed...");

            if (mSmartAdListener != null)
                mSmartAdListener.onAdRequestFailed();
        }

        @Override
        public void onAdRequestFailed(String error) {
            Log.e(TAG, "onAdRequestFailed... " + error);

            if (error.toLowerCase().contains("admob")) {
                adMobRequesting = false;
                adMobLoaded = false;
                adMobNativeAd = null;
            } else if (error.toLowerCase().contains("facebook")) {
                facebookRequesting = false;
                facebookLoaded = false;
                facebookShowing = false;
            } else if (error.toLowerCase().contains("startapp")) {
                startAppRequesting = false;
                startAppLoaded = false;
                startAppShowing = false;
            }

            if (mSmartAdListener != null)
                mSmartAdListener.onAdRequestFailed();
        }

        @Override
        public void onAdImpressionLogged() {
            Log.d(TAG, "onAdImpressionLogged...");

            if (mSmartAdListener != null)
                mSmartAdListener.onAdImpressionLogged();
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "onAdClicked...");

            if (mSmartAdListener != null)
                mSmartAdListener.onAdClicked();
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "onAdClosed...");

            if (mSmartAdListener != null)
                mSmartAdListener.onAdClosed();
        }
    };

    private void addLoadingView(ViewGroup viewGroup) {

        adViewGroup = viewGroup;
        adViewGroup.removeAllViews();

        ShimmerLayout shimmerLayout = new ShimmerLayout(mContext);
        int id = (int) viewGroup.getId() + 999;
        shimmerLayout.setId(id);

        ViewGroup.LayoutParams lpView = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        shimmerLayout.setLayoutParams(lpView);
        shimmerLayout.setBackgroundColor(mContext.getResources().getColor(R.color.dark_alpha70));
        shimmerLayout.setShimmerAnimationDuration(2000);
//        shimmerLayout.setShimmerDuration(2000);onAdDisplayed
//        shimmerLayout.setShimmerWidth(200);
//        shimmerLayout.setShimmerColor(mContext.getResources().getColor(R.color.dark_transparent_60));
        shimmerLayout.startShimmerAnimation();

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

    public SmartNative(Activity context) {
        mContext = context;
    }

    public void setAdListener(SmartAdListener smartAdListener) {
        mSmartAdListener = smartAdListener;
    }

    public void loadNativeAd(ViewGroup viewGroup) {
        adViewGroup = viewGroup;
        addLoadingView(viewGroup);
        loadNativeAd(false);
    }

    private void loadNativeAd(boolean isInternalCall) {
        if (!adRequesting || isInternalCall) {

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
                        Timber.tag(TAG).d("AdMob Request Started...");
                        adMobRequesting = true;
                        loadAdMobNative();
                    } else
                        loadNativeAd(true);
                    break;
                case "facebook":
                    if (!facebookRequesting) {
                        Timber.tag(TAG).d("Facebook Request Started...");
                        facebookRequesting = true;
                        loadFacebookNative();
                    } else
                        loadNativeAd(true);
                    break;
                case "startapp":
                    if (!startAppRequesting) {
                        Timber.tag(TAG).d("StartApp Request Started...");
                        startAppRequesting = true;
                        loadStartAppNative();
                    } else
                        loadNativeAd(true);
                    break;
                case "unity":
                    loadNativeAd(true);
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

    public void showNativeAd() {
        showNativeAd(false);
    }

    private void showNativeAd(boolean isInternalCall) {
        Thread thread = new Thread(() -> {
            showAds(isInternalCall);
        });
        thread.start();
    }

    private void showAds(boolean isInternalCall) {
        if (!adShowing || isInternalCall) {
            try {
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
                            Timber.tag(TAG).d("AdMob Waiting For Request...");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        showAdMobNative();
                        break;
                    case "facebook":
                        while (facebookRequesting) {
                            Timber.tag(TAG).d("Facebook Waiting For Request...");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        showFacebookNative();
                        break;
                    case "startapp":
                        while (startAppRequesting) {
                            Timber.tag(TAG).d("StartApp Waiting For Request...");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        showStartAppNative();
                        break;
                    case "unity":
                        showNativeAd(true);
                        break;
                    case "":
                        mShowPriority = -1;
                        adRequesting = false;
                        adShowing = false;
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adViewGroup.removeAllViews();
                            }
                        });
                        break;
                    default:
                        break;
                }
            } catch (Throwable throwable) {
                Timber.tag(TAG).e(throwable);
            }
        }
    }

    private void showAdMobNative() {
        try {
            if (adMobNativeAd != null && !adLoader.isLoading()) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        UnifiedNativeAdView adView = (UnifiedNativeAdView) inflater.inflate(R.layout.native_ad_admob, null);
                        populateUnifiedNativeAdView(adMobNativeAd, adView);

                        adViewGroup.removeAllViews();
                        adViewGroup.addView(adView);

                        TextView adStick = new TextView(new ContextThemeWrapper(mContext, R.style.AppTheme_AdAttribution_Left));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(3, 3, 0, 0);
                        adStick.setLayoutParams(layoutParams);

//                        adViewGroup.addView(adStick);

                        adMobShowing = true;
                        adShowing = false;
                        mShowPriority = -1;
                    }
                });

            } else {
                showNativeAd(true);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            showNativeAd(true);
        }
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
//            videoStatus.setText(String.format(Locale.getDefault(), "Video status: Ad contains a %.2f:1 video asset.", vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
//                    refresh.setEnabled(true);
//                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
//            videoStatus.setText("Video status: Ad does not contain a video asset.");
//            refresh.setEnabled(true);
        }
    }

    private void loadFacebookNative() {
        try {
            if (facebookNativeAd != null) {
                if (facebookNativeAd.isAdLoaded() && facebookNativeAd.isAdInvalidated()) {
                    return;
                } else {
                    facebookNativeAd = new NativeAd(mContext, KeyManager.getFbNativeKey());
                }
            } else {
                facebookNativeAd = new NativeAd(mContext, KeyManager.getFbNativeKey());
            }

            NativeAdBase.NativeAdLoadConfigBuilder nativeAdLoadConfigBuilder = facebookNativeAd.buildLoadAdConfig();
            nativeAdLoadConfigBuilder.withAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e(TAG, "Facebook Load Ad Failed...");

                    smartAdListener.onAdRequestFailed("Facebook");
                    loadNativeAd(true);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    facebookRequesting = false;
                    facebookLoaded = true;
                    facebookShowing = false;
                    adLoaded = true;

                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "Facebook Native Ad Loaded.", Toast.LENGTH_SHORT).show();

                    Timber.tag(TAG).d("Facebook Load Ad Successful...");
                    smartAdListener.onAdRequestSuccess();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    smartAdListener.onAdClicked();
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    smartAdListener.onAdImpressionLogged();
                }
            });

            nativeAdLoadConfigBuilder.withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL);
            nativeAdLoadConfigBuilder.build();

            facebookNativeAd.loadAd();
            facebookRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
            loadNativeAd(true);
        }
    }

    private void showFacebookNative() {
        try {
            if (facebookNativeAd != null && facebookNativeAd.isAdLoaded() && !facebookNativeAd.isAdInvalidated()) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adViewGroup.removeAllViews();
                        inflateAd(facebookNativeAd);
                        facebookShowing = true;
                        adShowing = false;
                        mShowPriority = -1;
                    }
                });
            } else {
                showNativeAd(true);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            showNativeAd(true);
        }
    }

    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();

        ViewGroup.LayoutParams lpView = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        com.facebook.ads.NativeAdLayout nativeAdLayout = new NativeAdLayout(mContext);
        nativeAdLayout.setLayoutParams(lpView);

        adViewGroup.addView(nativeAdLayout);

//        NativeAdLayout nativeAdLayout = (NativeAdLayout) ((Activity) mContext).findViewById(R.id.native_ad_container);
        FrameLayout adView = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.native_ad_facebook, (ViewGroup) adViewGroup, false);
        lpView.height = adViewGroup.getHeight();
        adView.setLayoutParams(lpView);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(mContext, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    private void loadAdMobNative() {
        try {

            AdLoader.Builder builder = new AdLoader.Builder(mContext, KeyManager.getAdMobNativeKey());

            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                // OnUnifiedNativeAdLoadedListener implementation.
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    if (adMobNativeAd != null) {
                        adMobNativeAd.destroy();
                    }
                    adMobNativeAd = unifiedNativeAd;
                }

            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adMobRequesting = false;
                    adMobLoaded = true;
                    adRequesting = false;
                    adLoaded = true;

                    if (logEvents && BuildConfig.DEBUG)
                        Toast.makeText(mContext, "AdMob Native Ad Loaded.", Toast.LENGTH_SHORT).show();

                    Timber.tag(TAG).d("AdMob Load Ad Successful...");
                    smartAdListener.onAdRequestSuccess();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    smartAdListener.onAdDisplayed();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    smartAdListener.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e(TAG, "AdMob Load Ad Failed...");
                    smartAdListener.onAdRequestFailed("AdMob");
                    loadNativeAd(true);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    smartAdListener.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    smartAdListener.onAdImpressionLogged();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();

                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
            adMobRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
            loadNativeAd(true);
        }
    }

    private void loadStartAppNative() {
        try {
            if (startAppAdNativeAd != null) {
                if (startAppAdNativeAd.isReady()) {
                    return;
                } else {
                    startAppAdNativeAd = new StartAppNativeAd(mContext);
                }
            } else {
                startAppAdNativeAd = new StartAppNativeAd(mContext);
            }

            NativeAdPreferences nativeAdPreferences = new NativeAdPreferences();
            nativeAdPreferences.setAdsNumber(1).setAutoBitmapDownload(true).setPrimaryImageSize(2);

            // StartAppAd.AdMode.AUTOMATIC, adPreferences,

            startAppAdNativeAd.loadAd(nativeAdPreferences, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    startAppRequesting = false;
                    startAppLoaded = true;
                    adRequesting = false;
                    adLoaded = true;

                    Log.d(TAG, "StartApp Load Ad Successful...");
                    smartAdListener.onAdRequestSuccess();
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    Log.e(TAG, "StartApp Load Ad Failed...");
                    smartAdListener.onAdRequestFailed("StartApp");
                    loadNativeAd(true);
                }
            });
            startAppRequesting = true;
        } catch (Exception e3) {
            e3.printStackTrace();
            loadNativeAd(true);
        }
    }

    private void showStartAppNative() {
        try {
            if (startAppAdNativeAd != null) {
                ArrayList<NativeAdDetails> nativeAds = startAppAdNativeAd.getNativeAds();
                final NativeAdDetails nativeAdDetails = nativeAds.size() > 0 ? nativeAds.get(0) : null;
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            adViewGroup.removeAllViews();
                            FrameLayout startAppNativeAddContainer = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.native_ad_startapp, (ViewGroup) adViewGroup, false);
                            adViewGroup.addView(startAppNativeAddContainer);

                            TextView txtNativeAdTitle = startAppNativeAddContainer.findViewById(R.id.native_ad_title);
                            TextView txtNativeAdBody = startAppNativeAddContainer.findViewById(R.id.native_ad_body);
                            TextView txtNativeAdSponsoredLabel = startAppNativeAddContainer.findViewById(R.id.native_ad_sponsored_label);

                            ImageView ivNativeAdMedia = startAppNativeAddContainer.findViewById(R.id.native_ad_media);
                            ImageView ivNativeAdIcon = startAppNativeAddContainer.findViewById(R.id.native_ad_icon);

                            txtNativeAdTitle.setText(nativeAdDetails.getTitle());
                            txtNativeAdBody.setText(nativeAdDetails.getDescription());
                            txtNativeAdSponsoredLabel.setText("Sponsored");
                            ivNativeAdIcon.setImageBitmap(nativeAdDetails.getImageBitmap());
                            ivNativeAdMedia.setImageBitmap(nativeAdDetails.getSecondaryImageBitmap());

                            nativeAdDetails.registerViewForInteraction(startAppNativeAddContainer);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                });
//                startAppAdNativeAd.showAd(new AdDisplayListener() {
//                    @Override
//                    public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
//                        startAppShowing = false;
//                        smartNativeListener.onNativeClose();
//                    }
//
//                    @Override
//                    public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
//                        smartNativeListener.onNativeDisplayed();
//                    }
//
//                    @Override
//                    public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
//                        smartNativeListener.onNativeClick();
//                    }
//
//                    @Override
//                    public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
//                        startAppShowing = false;
//
//                    }
//                });
                startAppShowing = true;
            } else {
                showNativeAd(true);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            showNativeAd(true);
        }
    }

}