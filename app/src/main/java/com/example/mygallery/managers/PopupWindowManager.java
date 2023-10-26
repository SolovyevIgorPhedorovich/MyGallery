package com.example.mygallery.managers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.core.content.ContextCompat;
import com.example.mygallery.R;

public abstract class PopupWindowManager {
    protected final PopupWindow popupWindow;
    protected final Context context;
    protected int x, y;

    public PopupWindowManager(Context context) {
        this.context = context;
        popupWindow = new PopupWindow(context);
        x = 0;
        y = 0;
        setDefaultConfiguration();
        setAnimation();
    }

    // Установка конфигурации PopupWindow по умолчанию
    private void setDefaultConfiguration() {
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popupbg));
    }

    // Установка содержимого PopupWindow и его конфигурации
    protected abstract void setSpecificConfiguration();

    // Установка анимации для PopupWindow
    protected abstract void setAnimation();

    // Установка позиции PopupWindow
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setContent(View contentView) {
        popupWindow.setContentView(contentView);
    }


    // Показ PopupWindow с заданной позицией
    public void showPopupWindow(View anchorView, View viewMenu, int gravity) {
        popupWindow.showAtLocation(anchorView, gravity, x, y);
        setButtonListeners(viewMenu);
        setSpecificConfiguration();
    }

    // Установка обработчиков для кнопок внутри PopupWindow
    private void setButtonListeners(View view) {
        if (view instanceof ViewGroup) {
            iterateButtons(view);
        }
    }

    // Рекурсивный обход всех кнопок внутри ViewGroup
    private void iterateButtons(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof Button) {
                    Button button = (Button) childView;
                    int buttonId = button.getId();
                    setButtonClickListener(button, buttonId);
                }
                if (childView instanceof LinearLayout) {
                    iterateButtons(childView);
                }
            }
        }
    }

    // Установка обработчика нажатия для кнопки
    protected abstract void setButtonClickListener(Button button, int buttonId);

}