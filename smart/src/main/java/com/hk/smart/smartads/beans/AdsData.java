package com.hk.smart.smartads.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdsData {

    @SerializedName("app_setting")
    @Expose
    private List<AppSetting> appSetting = null;

    @SerializedName("ad_networks")
    @Expose
    private List<AdNetwork> adNetworks = null;

    public List<AppSetting> getAppSetting() {
        return appSetting;
    }

    public void setAppSetting(List<AppSetting> appSetting) {
        this.appSetting = appSetting;
    }

    public List<AdNetwork> getAdNetworks() {
        return adNetworks;
    }

    public void setAdNetworks(List<AdNetwork> adNetworks) {
        this.adNetworks = adNetworks;
    }

}