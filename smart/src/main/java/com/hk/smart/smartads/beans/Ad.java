package com.hk.smart.smartads.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ad {

    @SerializedName("ads_title_short")
    @Expose
    private String adsTitleShort;
    @SerializedName("ads_dev")
    @Expose
    private String adsDev;
    @SerializedName("ads_title")
    @Expose
    private String adsTitle;
    @SerializedName("ads_des")
    @Expose
    private String adsDes;
    @SerializedName("ads_icon")
    @Expose
    private String adsIcon;
    @SerializedName("ads_banner")
    @Expose
    private String adsBanner;
    @SerializedName("ads_pkg")
    @Expose
    private String adsPkg;
    @SerializedName("ads_time_skip")
    @Expose
    private String adsTimeSkip;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("priority")
    @Expose
    private Integer priority;

    public String getAdsTitleShort() {
        return adsTitleShort;
    }

    public void setAdsTitleShort(String adsTitleShort) {
        this.adsTitleShort = adsTitleShort;
    }

    public String getAdsDev() {
        return adsDev;
    }

    public void setAdsDev(String adsDev) {
        this.adsDev = adsDev;
    }

    public String getAdsTitle() {
        return adsTitle;
    }

    public void setAdsTitle(String adsTitle) {
        this.adsTitle = adsTitle;
    }

    public String getAdsDes() {
        return adsDes;
    }

    public void setAdsDes(String adsDes) {
        this.adsDes = adsDes;
    }

    public String getAdsIcon() {
        return adsIcon;
    }

    public void setAdsIcon(String adsIcon) {
        this.adsIcon = adsIcon;
    }

    public String getAdsBanner() {
        return adsBanner;
    }

    public void setAdsBanner(String adsBanner) {
        this.adsBanner = adsBanner;
    }

    public String getAdsPkg() {
        return adsPkg;
    }

    public void setAdsPkg(String adsPkg) {
        this.adsPkg = adsPkg;
    }

    public String getAdsTimeSkip() {
        return adsTimeSkip;
    }

    public void setAdsTimeSkip(String adsTimeSkip) {
        this.adsTimeSkip = adsTimeSkip;
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

}