//Анимация взята с "https://www.boltuix.com/2022/10/how-to-create-onboarding-screen-with.html?m=1"
package com.example.mygallery.viewPager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

//https://developer.android.com/develop/ui/views/animations/screen-slide-2#java
public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private final float MIN_SCALE = 0.98f; // Минимальный масштаб элемента
    private final float MIN_ALPHA = 0.8f; // Минимальная прозрачность элемента

    @Override
    public void transformPage(@NonNull View page, float position) {
        float pageWidth = page.getWidth(); // Ширина элемента страницы
        float pageHeight = page.getHeight(); // Высота элемента страницы

        if (position < -1) { // Если страница находится слева от текущей видимой страницы
            page.setAlpha(0f); // Устанавливаем полную прозрачность
        } else if (position <= 1) { // Если страница находится в области видимости
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position)); // Рассчитываем масштаб страницы на основе её позиции
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2; // Рассчитываем вертикальный отступ на основе масштаба
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2; // Рассчитываем горизонтальный отступ на основе масштаба

            if (position < 0) { // Если страница находится слева от текущей видимой страницы
                page.setTranslationX(horizontalMargin - verticalMargin / 2); // Устанавливаем горизонтальное смещение влево
            } else {
                page.setTranslationX(-horizontalMargin + verticalMargin / 2); // Устанавливаем горизонтальное смещение вправо
            }

            page.setScaleX(scaleFactor); // Устанавливаем масштаб по горизонтали
            page.setScaleY(scaleFactor); // Устанавливаем масштаб по вертикали
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)); // Устанавливаем прозрачность на основе масштаба
        } else { // Если страница находится справа от текущей видимой страницы
            page.setAlpha(0f); // Устанавливаем полную прозрачность
        }
    }
}
