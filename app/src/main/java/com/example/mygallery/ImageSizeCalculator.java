package com.example.mygallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.*;

public class ImageSizeCalculator {
    private static final float ASPECT_RATIO = 1.5F;
    private final Context context;
    private final View view;
    private final int spanCount;
    private final Point result;

    // Конструктор класса
    public ImageSizeCalculator(Context context, View view, int spanCount) {
        this.context = context;
        this.view = view;
        this.spanCount = spanCount;
        this.result = new Point();
    }

    // Метод для расчета ширины экрана
    @SuppressWarnings("deprecation")
    private int calculateScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        // В зависимости от версии Android используется различный способ получения размеров экрана
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = context.getSystemService(WindowManager.class).getCurrentWindowMetrics();
            return windowMetrics.getBounds().width();
        } else {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            display.getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    // Метод для расчета отступа из параметров макета view
    private int calculateMargin() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return layoutParams.getMarginStart();
    }

    // Метод для расчета ширины изображения
    private int calculateImageWidth() {
        int screenWidth = calculateScreenWidth();
        int margin = calculateMargin();
        return (screenWidth - ((1 + spanCount) * margin)) / spanCount;
    }

    // Метод для расчета размеров квадратного изображения
    public void calculateSquareShareSize() {
        int widthImage = calculateImageWidth();
        result.set(widthImage, widthImage);
    }

    // Метод для расчета размеров прямоугольного изображения
    public void calculateRectangularShapeSize() {
        int widthImage = calculateImageWidth();
        int heightImage = (int) (widthImage * ASPECT_RATIO); // Расчёт высоты с учетом заданного соотношения сторон
        result.set(widthImage, heightImage);
    }

    // Метод для получения рассчитанных размеров изображения
    public Point getResult() {
        return result;
    }
}
