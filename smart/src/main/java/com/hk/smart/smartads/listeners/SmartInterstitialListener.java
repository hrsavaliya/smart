package com.hk.smart.smartads.listeners;

public abstract class SmartInterstitialListener {
    public void onInterstitialRequestSuccess() {
    }

    public void onInterstitialDisplayed() {
    }

    public void onInterstitialRequestFailed() {
    }

    public void onInterstitialRequestFailed(String error) {
    }

    public void onInterstitialImpressionLogged() {
    }

    public void onInterstitialClick() {
    }

    public void onInterstitialClose() {
    }

    public void onInterstitialNoAdFound() {
    }
}
