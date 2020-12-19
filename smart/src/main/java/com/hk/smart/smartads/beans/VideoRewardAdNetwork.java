package com.hk.smart.smartads.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoRewardAdNetwork {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("ad_network")
    @Expose
    private String adNetwork;
    @SerializedName("ad_unit_id")
    @Expose
    private String adUnitId;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAdNetwork() {
        return adNetwork;
    }

    public void setAdNetwork(String adNetwork) {
        this.adNetwork = adNetwork;
    }

    public String getAdUnitId() {
        return adUnitId;
    }

    public void setAdUnitId(String adUnitId) {
        this.adUnitId = adUnitId;
    }
}