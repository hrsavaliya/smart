package com.hk.smart.smartads.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hk.smart.R;
import com.hk.smart.smartads.nativead.SmartNative;

import java.util.ArrayList;

public class SmartDialog extends AppCompatActivity {

    private static final String EXTRA_APP_NAME = "app_name";
    private static final String EXTRA_FACEBOOK_KEY = "facebook_key";
    private static final String EXTRA_ADMOB_KEY = "admob_key";
    private static final String EXTRA_AD_PRIORITY_LIST = "ad_priority_list";
    private static final String EXTRA_SHOW_REVIEW_BUTTON = "show_review_button";
    private static final String EXTRA_ADMOB_NATIVE_TYPE = "admob_native_type";

    ViewGroup adViewContainer;
    TextView tvFinish;
    TextView tvReview;
    View dividerBtn;
    String appName;
    FrameLayout frameLayout;
    String facebookKey;
    String admobKey;
    boolean showReviewButton;
    ArrayList<Integer> adPriorityList;
    SmartNative smartNative;


    SmartDialogListener mSmartDialogListener;
    Activity activity;
    Dialog dialog;
    Button btnOne, btnTwo;
    ImageView imageViewDialog;
    TextView txtTitle, txtMessage, txtReward;
    FrameLayout nativeads;

    public SmartDialog(Activity activity) {
        initializeDialog(activity);
    }

    public SmartDialog(Activity activity, SmartDialogListener smartDialogListener) {
        initializeDialog(activity);
        mSmartDialogListener = smartDialogListener;
    }

    private void initializeDialog(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity, R.style.creativeDialogTheme);
        dialog.setContentView(R.layout.dialog_smart);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.dark_alpha70)));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCancelable(false);
        btnOne = this.dialog.findViewById(R.id.btn_one);
        btnTwo = this.dialog.findViewById(R.id.btn_two);
        imageViewDialog = this.dialog.findViewById(R.id.dialog_image);
        txtTitle = this.dialog.findViewById(R.id.dialog_title);
        txtMessage = this.dialog.findViewById(R.id.dialog_wait_message);
        txtReward = this.dialog.findViewById(R.id.reward_text);

        smartNative = new SmartNative(activity);
        nativeads = dialog.findViewById(R.id.ad_view_container_dialog);

        smartNative.loadNativeAd(nativeads);
        smartNative.showNativeAd();

        txtTitle.setTextSize(17);
        txtReward.setTextSize(17);
        txtMessage.setTextSize(17);

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSmartDialogListener != null)
                    mSmartDialogListener.onSmartDialogBtnOneClick();
                dialog.dismiss();
//                smartNative = new SmartNative(activity);
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSmartDialogListener != null)
                    mSmartDialogListener.onSmartDialogOkClick();
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(v -> {
            mSmartDialogListener = null;
        });

    }

    public Dialog getDialog() {
        return dialog;
    }

    public void showDialog() {

        if (!activity.isFinishing() && !activity.isDestroyed())
            if (dialog.isShowing())
                dialog.dismiss();

        if (!activity.isFinishing() && !activity.isDestroyed())
            this.dialog.show();

    }

    public void showDialog(String title, String rewardText) {
        imageViewDialog.setVisibility(View.GONE);
        nativeads.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        btnOne.setVisibility(View.GONE);
        showDialog();
    }

    public void showDialogWithOutAds(String title, String rewardText, String buttonText) {
        imageViewDialog.setVisibility(View.GONE);
        nativeads.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        txtReward.setMaxLines(100);
        btnOne.setVisibility(View.GONE);
        btnTwo.setText(buttonText);
        showDialog();
    }

    public void showdialogforhelp(String title, String rewardText, String buttonText) {
        imageViewDialog.setVisibility(View.GONE);
        nativeads.setVisibility(View.GONE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        txtReward.setMaxLines(100);
        btnOne.setVisibility(View.GONE);
        btnTwo.setText(buttonText);
        showDialog();
    }

    public void showDialog(String title, String rewardText, String buttonText) {
        imageViewDialog.setVisibility(View.GONE);
        nativeads.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        btnOne.setVisibility(View.GONE);
        btnTwo.setText(buttonText);
        showDialog();
    }

    public void showDialog(String title, String rewardText, String button1Text, String button2Text) {
        imageViewDialog.setVisibility(View.GONE);
        nativeads.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        btnOne.setVisibility(View.VISIBLE);
        btnOne.setText(button1Text);
        btnTwo.setText(button2Text);
        showDialog();
    }

    public void showDialog(String title, String rewardText, String buttonText, boolean isCancelable) {
        imageViewDialog.setVisibility(View.GONE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        btnOne.setVisibility(View.GONE);
        btnTwo.setText(buttonText);
        dialog.setCancelable(isCancelable);
        showDialog();
    }

    public void showDialog(String title, String rewardText, boolean moreAdButton) {
        imageViewDialog.setVisibility(View.GONE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        btnOne.setVisibility(View.VISIBLE);
        showDialog();
    }

    public void showDialog(String title, String rewardText, boolean moreAdButton, boolean isInterAds) {
        imageViewDialog.setVisibility(View.GONE);
        txtTitle.setText(title);
        txtReward.setText(rewardText);
        btnOne.setVisibility(View.VISIBLE);
        showDialog();
    }

    public void setListener(SmartDialogListener smartDialogListener) {
        mSmartDialogListener = smartDialogListener;
    }

    public static AlertDialog showAlertDialog(Context context, String title, String message, boolean finishActivity) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (finishActivity) {
                            ((Activity) context).finish();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(context.getString(R.string.app_name));

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            }
        });
        alert.show();
        return alert;
    }

    public static void startDialog(Activity activity) {
        Intent intent = new Intent(activity, SmartDialog.class);

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onCreate(savedInstanceState);
//
//        setupFromSavedInstanceState(savedInstanceState);
//
//        setContentView(R.layout.dialog_smart);
//
//        getWindow().setBackgroundDrawable(null);
//        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();
//        int minWidth = dpToPx(300);
//        getWindow().getAttributes().width = Math.min(width, minWidth);
//
//
//        setFinishOnTouchOutside(false);
//
//
//    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // 저장된 상태를 복구한다.
            appName = savedInstanceState.getString(EXTRA_APP_NAME);
            facebookKey = savedInstanceState.getString(EXTRA_FACEBOOK_KEY);
            admobKey = savedInstanceState.getString(EXTRA_ADMOB_KEY);
            adPriorityList = savedInstanceState.getIntegerArrayList(EXTRA_AD_PRIORITY_LIST);
            showReviewButton = savedInstanceState.getBoolean(EXTRA_SHOW_REVIEW_BUTTON);
        } else {

            // 새 객체를 위해 멤버 변수를 초기화 한다.
            appName = getIntent().getStringExtra(EXTRA_APP_NAME);
            facebookKey = getIntent().getStringExtra(EXTRA_FACEBOOK_KEY);
            admobKey = getIntent().getStringExtra(EXTRA_ADMOB_KEY);
            adPriorityList = getIntent().getIntegerArrayListExtra(EXTRA_AD_PRIORITY_LIST);
            showReviewButton = getIntent().getBooleanExtra(EXTRA_SHOW_REVIEW_BUTTON, false);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
