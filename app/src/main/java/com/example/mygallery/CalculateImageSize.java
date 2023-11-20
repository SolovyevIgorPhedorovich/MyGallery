package com.example.mygallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.*;

public class CalculateImageSize {
    private static final float ATTITUDE = 1.5F;
    private final Context context;
    private final View view;
    private final int spanCount;
    private final Point result;

    public CalculateImageSize(Context context, View view, int spanCount) {
        this.context = context;
        this.view = view;
        this.spanCount = spanCount;
        this.result = new Point();
    }

    @SuppressWarnings("deprecation")
    private int calculated() {
        Display display;
        DisplayMetrics displayMetrics = new DisplayMetrics();

        int screenWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = context.getSystemService(WindowManager.class).getCurrentWindowMetrics();
            Rect bounds = windowMetrics.getBounds();
            screenWidth = bounds.width();
        } else {
            display = ((Activity) context).getWindowManager().getDefaultDisplay();
            display.getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
        }

        int margin = getMargin();
        return (screenWidth - ((1 + spanCount) * margin)) / spanCount;
    }

    private int getMargin() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return layoutParams.getMarginStart();
    }

    public void squareShare() {
        int widthImage = calculated();
        result.set(widthImage, widthImage);
    }

    public void rectangularShape() {
        int widthImage = calculated();
        int heightImage = (int) (widthImage * ATTITUDE);
        result.set(widthImage, heightImage);
    }

    public Point getResult() {
        return result;
    }
}
