package com.hk.smart.smartads.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdNetwork {

    @SerializedName("ad_network")
    @Expose
    private String adNetwork;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("banner_ads")
    @Expose
    private List<BannerAdNetwork> bannerAds = null;
    @SerializedName("interstitial_ads")
    @Expose
    private List<InterstitialAdNetwork> interstitialAds = null;
    @SerializedName("native_ads")
    @Expose
    private List<NativeAdNetwork> nativeAds = null;
    @SerializedName("native_banner_ads")
    @Expose
    private List<NativeBannerAdNetwork> nativeBannerAds = null;
    @SerializedName("video_reward_ads")
    @Expose
    private List<VideoRewardAdNetwork> videoRewardAds = null;
    @SerializedName("medium_rectangle_ads")
    @Expose
    private List<MediumRectangleAdNetwork> mediumRectangleAds = null;

    public String getAdNetwork() {
        return adNetwork;
    }

    public void setAdNetwork(String adNetwork) {
        this.adNetwork = adNetwork;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<BannerAdNetwork> getBannerAds() {
        return bannerAds;
    }

    public void setBannerAds(List<BannerAdNetwork> bannerAds) {
        this.bannerAds = bannerAds;
    }

    public List<InterstitialAdNetwork> getInterstitialAds() {
        return interstitialAds;
    }

    public void setInterstitialAds(List<InterstitialAdNetwork> interstitialAds) {
        this.interstitialAds = interstitialAds;
    }

    public List<NativeAdNetwork> getNativeAds() {
        return nativeAds;
    }

    public void setNativeAds(List<NativeAdNetwork> nativeAds) {
        this.nativeAds = nativeAds;
    }

    public List<NativeBannerAdNetwork> getNativeBannerAds() {
        return nativeBannerAds;
    }

    public void setNativeBannerAds(List<NativeBannerAdNetwork> nativeBannerAds) {
        this.nativeBannerAds = nativeBannerAds;
    }

    public List<VideoRewardAdNetwork> getVideoRewardAds() {
        return videoRewardAds;
    }

    public void setVideoRewardAds(List<VideoRewardAdNetwork> videoRewardAds) {
        this.videoRewardAds = videoRewardAds;
    }

    public List<MediumRectangleAdNetwork> getMediumRectangleAds() {
        return mediumRectangleAds;
    }

    public void setMediumRectangleAds(List<MediumRectangleAdNetwork> mediumRectangleAds) {
        this.mediumRectangleAds = mediumRectangleAds;
    }

}