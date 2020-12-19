package com.hk.smart.dialogs;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hk.smart.R;
import com.hk.smart.smartads.utils.Prefs;

public class RateStarDialog extends Dialog {
    public static final String RATED = "is_rate";

    public LinearLayout ivStartContainer;
    private ImageView[] iv_array = new ImageView[5];

    public Context mContext;

    public int mStar = -1;
    View.OnClickListener onClickListener = view -> {
        int id = view.getId();
        if (id == R.id.btExit) {
            RateStarDialog.this.dismiss();
        } else if (id == R.id.btNever) {
//                Prefs.putBoolean(Prefs.RATE_DONE, true);
            RateStarDialog.this.dismiss();
        } else if (id != R.id.btRate) {
            if (id == R.id.iv_star_1) {
                RateStarDialog.this.setStart(1);
            } else if (id == R.id.iv_star_2) {
                RateStarDialog.this.setStart(2);
            } else if (id == R.id.iv_star_3) {
                RateStarDialog.this.setStart(3);
            } else if (id == R.id.iv_star_4) {
                RateStarDialog.this.setStart(4);
            } else if (id == R.id.iv_star_5) {
                RateStarDialog.this.setStart(5);
            }
        } else if (RateStarDialog.this.mStar == -1) {
            RateStarDialog.this.ivStartContainer.startAnimation(AnimationUtils.loadAnimation(RateStarDialog.this.mContext, R.anim.zoom_in_out_no_loop));
        } else {
            if (RateStarDialog.this.mStar >= 4) {
                if (!Prefs.getBoolean("rate_done")) {
                    String packageName = RateStarDialog.this.mContext.getPackageName();
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    RateStarDialog.this.mContext.startActivity(intent);
                    Prefs.putBoolean("rate_done", true);
                }
                Toast.makeText(RateStarDialog.this.mContext, "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent3 = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "sajansargam9@gmail.com", null));
                intent3.putExtra("android.intent.extra.EMAIL", "sajansargam9@gmail.com");
                intent3.putExtra("android.intent.extra.SUBJECT", RateStarDialog.this.mContext.getApplicationInfo().name);
                intent3.putExtra("android.intent.extra.TEXT", "Share us your problem and we will fix it!" + "\n");
                try {
                    RateStarDialog.this.mContext.startActivity(Intent.createChooser(intent3, "Send Email").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Toast.makeText(RateStarDialog.this.mContext, "Share us your problem and we will fix it!", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException unused2) {
                    Toast.makeText(RateStarDialog.this.mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
//                    }
            }
//                Prefs.putBoolean(Prefs.RATE_DONE, true);
            RateStarDialog.this.dismiss();
        }
    };

    public RateStarDialog(Context context) {
        super(context, R.style.Theme_AppCompat_Dialog_Alert);

        this.mContext = context;
        requestWindowFeature(1);
        setContentView(R.layout.dialog_rate_star);
        setCanceledOnTouchOutside(true);
        this.ivStartContainer = findViewById(R.id.iv_star_container);
        ImageView iv1 = findViewById(R.id.iv_star_1);
        ImageView iv2 = findViewById(R.id.iv_star_2);
        ImageView iv3 = findViewById(R.id.iv_star_3);
        ImageView iv4 = findViewById(R.id.iv_star_4);
        ImageView iv5 = findViewById(R.id.iv_star_5);
        iv1.setOnClickListener(this.onClickListener);
        iv2.setOnClickListener(this.onClickListener);
        iv3.setOnClickListener(this.onClickListener);
        iv4.setOnClickListener(this.onClickListener);
        iv5.setOnClickListener(this.onClickListener);
        ImageView[] imageViewArr = this.iv_array;
        imageViewArr[0] = iv1;
        imageViewArr[1] = iv2;
        imageViewArr[2] = iv3;
        imageViewArr[3] = iv4;
        imageViewArr[4] = iv5;
        findViewById(R.id.btExit).setOnClickListener(this.onClickListener);
        findViewById(R.id.btNever).setOnClickListener(this.onClickListener);
        findViewById(R.id.btRate).setOnClickListener(this.onClickListener);
        try {
            show();
            RateStarDialog.this.ivStartContainer.startAnimation(AnimationUtils.loadAnimation(RateStarDialog.this.mContext, R.anim.zoom_in_out_no_loop));
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }


    public void setStart(int i) {
        this.mStar = i;
        for (int i2 = 0; i2 < i; i2++) {
            this.iv_array[i2].setImageResource(R.drawable.ic_star_yellow);
        }
        while (true) {
            ImageView[] imageViewArr = this.iv_array;
            if (i < imageViewArr.length) {
                imageViewArr[i].setImageResource(R.drawable.ic_star_blur);
                i++;
            } else {
                return;
            }
        }
    }
}
