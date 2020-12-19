package com.hk.smart.smartads.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import com.hk.smart.R;


public class ActivityHelper {

    public static DisplayMetrics displayMetrics;

    public static void startActivity(Context baseContext, Class nextActivity) {
        baseContext.startActivity(new Intent(baseContext, nextActivity));
        ((Activity) baseContext).overridePendingTransition(R.anim.actin, R.anim.actout);
    }

    public static void startActivity(Context baseContext, Class nextActivity, boolean withFinishParent) {
        if (withFinishParent) {
            Intent intent = new Intent(baseContext, nextActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            baseContext.startActivity(new Intent(baseContext, nextActivity));
            ((Activity) baseContext).finish();
            ((Activity) baseContext).overridePendingTransition(R.anim.actin, R.anim.actout);
        } else {
            startActivity(baseContext, nextActivity);
        }
    }

    public static void startActivity(Context baseContext, Intent intent) {
        baseContext.startActivity(intent);
        ((Activity) baseContext).overridePendingTransition(R.anim.actin, R.anim.actout);
    }

    public static void startActivity(Context baseContext, Intent intent, boolean withFinishParent) {
        if (withFinishParent) {
            startActivity(baseContext, intent);
            ((Activity) baseContext).finish();
            ((Activity) baseContext).overridePendingTransition(R.anim.actin, R.anim.actout);
        } else {
            startActivity(baseContext, intent);
        }
    }

    public static void onBackPressed(Context baseContext) {
        ((Activity) baseContext).overridePendingTransition(R.anim.actbackin, R.anim.actbackout);
    }

    public static void onBackPressed(Context baseContext, boolean withFinishParent) {
        if (withFinishParent) {
            ((Activity) baseContext).finish();
            onBackPressed(baseContext);
        } else {
            onBackPressed(baseContext);
        }
    }

}
