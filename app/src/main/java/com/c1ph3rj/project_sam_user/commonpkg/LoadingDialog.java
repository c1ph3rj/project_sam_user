package com.c1ph3rj.project_sam_user.commonpkg;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;

import com.airbnb.lottie.LottieAnimationView;
import com.c1ph3rj.project_sam_user.R;

public class LoadingDialog extends Dialog {
    private final LottieAnimationView animationView;

    public LoadingDialog(Context context) {
        super(context);
        setContentView(R.layout.loading_layout);
        animationView = findViewById(R.id.loadingAnimation);
        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        animationView.playAnimation();
    }

    @Override
    public void dismiss() {
        animationView.cancelAnimation();
        super.dismiss();
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(false);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(false);
    }
}

