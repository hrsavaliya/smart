package com.hk.smart.smartads.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.hk.smart.R;

import timber.log.Timber;

public class AdLoadingDialog {
    private Dialog dialog;

    public AdLoadingDialog(Activity activity) {
        initializeDialog(activity);
    }

    public Activity mActivity;
    TextView txt_dialog;

    private void initializeDialog(Activity activity) {
        mActivity = activity;
        try {
            dialog = new Dialog(activity, R.style.AdLoadingDialogTheme);
            dialog.setContentView(R.layout.dialog_ad_loading);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.dark_alpha80)));
            dialog.setCancelable(false);
            txt_dialog = dialog.findViewById(R.id.txt_dialog);
        } catch (Exception exc) {
            Timber.e(exc);
        }
    }

    public void show() {
        dialog.show();
    }

    public void show(String message) {
        if (!message.equals("")) {
            if (txt_dialog != null) {
                txt_dialog.setText(message);
            }
        }

        if (dialog != null)
            dialog.show();
    }
    public void dismiss() {
        if (!mActivity.isDestroyed() && dialog.isShowing())
            dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
