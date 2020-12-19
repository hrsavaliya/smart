package com.hk.smart.smartads.listeners;

public abstract class SmartAdListener {
    public void onAdRequestSuccess() {}

    public void onAdDisplayed() {}

    public void onAdRequestFailed() {}

    public void onAdRequestFailed(String error) {}

    public void onAdImpressionLogged() {}

    public void onAdClicked() {}

    public void onAdClosed() {}

    public void onAdNotFound() {}
}
