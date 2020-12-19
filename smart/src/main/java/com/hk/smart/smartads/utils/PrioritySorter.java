package com.hk.smart.smartads.utils;

import com.hk.smart.smartads.beans.AdNetwork;

import java.util.Comparator;

import timber.log.Timber;

public class PrioritySorter implements Comparator<AdNetwork> {
    @Override
    public int compare(AdNetwork o1, AdNetwork o2) {
        Timber.tag("PrioritySorter******").e(o1.getAdNetwork());
        return o1.getPriority().compareTo(o2.getPriority());
    }
}
