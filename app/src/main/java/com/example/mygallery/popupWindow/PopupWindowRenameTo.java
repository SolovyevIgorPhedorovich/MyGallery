package com.example.mygallery.popupWindow;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowRenameTo extends PopupWindowManager {

    public PopupWindowRenameTo(Context context) {
        super(context);
    }

    @Override
    protected void setSpecificConfiguration() {
        popupWindow.setOutsideTouchable(false);
        // Установка флага, чтобы окно реагировало на изменение размера при открытии клавиатуры
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setLayoutListener();
        setFocusEditText();
    }

    @Override
    protected void setAnimation() {

    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {
        ImageViewActivity imageViewActivity = (ImageViewActivity) context;
        button.setOnClickListener(view -> {
            popupWindow.dismiss();
            if (buttonId == R.id.OK) {
                EditText name = popupWindow.getContentView().findViewById(R.id.newName);
                imageViewActivity.rename(name.getText().toString());
            }
        });
    }

    //Перерасчет позиции PopupWindow с учетом клавиатуры
    private void setLayoutListener() {
        View rootView = ((ImageViewActivity) context).getWindow().getDecorView().getRootView();

        // Слушатель для прослушивания изменений в дереве видов
        ViewTreeObserver.OnGlobalLayoutListener layoutListener = () -> {
            Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);

            // Вычисление высоты клавиатуры
            int keyboardHeight = rootView.getHeight() - rect.bottom;

            int newY = y + keyboardHeight;

            // Установка позицию PopupWindow
            popupWindow.update(0, newY, popupWindow.getWidth(), popupWindow.getHeight(), popupWindow.isShowing());
        };
    }

    private void setFocusEditText() {
        // Получение ссылки на поле ввода внутри вашего View
        EditText editText = popupWindow.getContentView().findViewById(R.id.newName); // Замените "yourEditTextId" на реальный ID вашего EditText

        // Установка фокуса на клавиатуру
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}
