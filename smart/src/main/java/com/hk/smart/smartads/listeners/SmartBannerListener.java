package com.hk.smart.smartads.listeners;

public interface SmartBannerListener {
    public void onBannerRequestSuccess();

    public void onBannerDisplayed();

    public void onBannerRequestFailed();

    public void onBannerRequestFailed(String error);

    public void onBannerImpressionLogged();

    public void onBannerClick();

    public void onBannerClose();
}
