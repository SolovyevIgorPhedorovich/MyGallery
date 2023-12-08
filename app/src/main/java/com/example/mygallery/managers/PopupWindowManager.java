package com.example.mygallery.managers;

import android.app.Activity;
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
    protected abstract boolean setConfiguration();


    // Установка анимации для PopupWindow
    protected abstract void setAnimation();

    // Установка позиции PopupWindow
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setContent(View contentView) {
        popupWindow.setContentView(contentView);
        if (!setConfiguration()) {
            setDefaultConfiguration();
        }
    }

    // Показ PopupWindow с заданной позицией
    public void showPopupWindow(View anchorView, View viewMenu, int gravity) {
        if (anchorView == null) {
            anchorView = ((Activity) context).findViewById(android.R.id.content);
        }
        popupWindow.showAtLocation(anchorView, gravity, x, y);
        setButtonListeners(viewMenu);
    }

    // Установка обработчиков для кнопок внутри PopupWindow
    private void setButtonListeners(View view) {
        iterateButtons(view);
    }

    // Рекурсивный обход всех кнопок внутри ViewGroup
    private void iterateButtons(View view) {
        if (view instanceof ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof Button) {
                    setButtonClickHandler((Button) childView);
                }
                if (childView instanceof LinearLayout) {
                    iterateButtons(childView);
                }
            }
        }
    }

    // Установка обработчика нажатия для кнопки
    private void setButtonClickHandler(Button button) {
        int buttonId = button.getId();
        button.setOnClickListener(v -> handleButtonClick(buttonId));
    }

    // Handle button click
    protected abstract void handleButtonClick(int buttonId);
}