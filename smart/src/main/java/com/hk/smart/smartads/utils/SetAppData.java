package com.hk.smart.smartads.utils;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SetAppData {

    JSONObject mainObj;
    List<String> list_adNetworks;


    public SetAppData(JSONObject jsonObject) {
        mainObj = jsonObject;
        list_adNetworks= new ArrayList<>();
    }

    public void set() throws JSONException {
        JSONArray ad_networks = mainObj.getJSONArray("ad_networks");
        Timber.tag("ad_networks").e(ad_networks.toString());
        Timber.tag("ad_networks_length").e(ad_networks.length() + "");

        for (int i = 1; i < ad_networks.length() + 1; i++) {
            JSONObject ad_network = ad_networks.getJSONObject(i);

            String ad_network_name = ad_network.getString("ad_network");
            String ad_network_enabled = ad_network.getString("enabled");
            String ad_network_priority = ad_network.getString("priority");
            String ad_network_api_key = ad_network.getString("api_key");





            //
            list_adNetworks.add(ad_network.getString("ad_network"));



            Prefs.putList("facebook_banner_ads", list_adNetworks);
            Prefs.putList("facebook_interstitial_ads", list_adNetworks);
            Prefs.putList("facebook_native_ads", list_adNetworks);
            Prefs.putList("facebook_native_banner_ads", list_adNetworks);
            Prefs.putList("facebook_medium_rectangle_ads", list_adNetworks);
            Prefs.putList("facebook_medium_video_reward_ads", list_adNetworks);
        }

        Prefs.putList("ad_networks", list_adNetworks);
    }

}
