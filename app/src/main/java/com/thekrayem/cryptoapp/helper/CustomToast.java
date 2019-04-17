package com.thekrayem.cryptoapp.helper;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;


public class CustomToast{

    private static final long DEFAULT_DURATION_MILLIS = 5000;
    private static final float DEFAULT_RATIO = 0.25f;

    private CustomToast(){

    }

    /**
     * Shows a custom toast
     * @param container The container in which the toast is added. Usually this is the outermost ViewGroup in an activity
     * @param interpolator Interpolator for the entrance Animation. Can be null.
     */
    public static void show(final Context context, final ViewGroup container, int backgroundColorResId, String message, Interpolator interpolator){
        int actualColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            actualColor = context.getColor(backgroundColorResId);
        }else{
            actualColor = context.getResources().getColor(backgroundColorResId);
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View parentView = inflater.inflate(R.layout.custom_toast_layout, null);
        parentView.setBackgroundColor(actualColor);
        ViewGroup.LayoutParams parentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentView.setLayoutParams(parentParams);
        TextView messageTv=(TextView)parentView.findViewById(R.id.custom_toast_message_tv);
        messageTv.setText(message);
        messageTv.setTextColor(Color.WHITE);
        container.addView(parentView);
        parentView.bringToFront();
        Animation entranceAnim = AnimationUtils.loadAnimation(context, R.anim.slide_from_top);
        entranceAnim.setDuration((long)(DEFAULT_DURATION_MILLIS*DEFAULT_RATIO));
        entranceAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation exitAnim = AnimationUtils.loadAnimation(context, R.anim.slide_from_bottom);
                exitAnim.setStartOffset((long)(DEFAULT_DURATION_MILLIS*2*DEFAULT_RATIO));
                exitAnim.setDuration((long)(DEFAULT_DURATION_MILLIS*DEFAULT_RATIO));
                exitAnim.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        container.removeView(parentView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                parentView.startAnimation(exitAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if(interpolator != null){
            entranceAnim.setInterpolator(interpolator);
        }
        parentView.setVisibility(View.VISIBLE);
        parentView.startAnimation(entranceAnim);
    }
}
