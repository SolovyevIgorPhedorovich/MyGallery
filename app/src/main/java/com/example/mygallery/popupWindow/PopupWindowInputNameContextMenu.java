package com.example.mygallery.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.CreateAlbumManager;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowInputNameContextMenu extends PopupWindowManager {
    private final boolean isLayoutListenerCallde = false;
    private EditText editText;

    public PopupWindowInputNameContextMenu(Context context) {
        super(context);
    }

    public static void show(Context context, View view, String name) {
        PopupWindowInputNameContextMenu mPopupWindow = new PopupWindowInputNameContextMenu(context);
        initialized(context, mPopupWindow, view, name);
    }

    private static void initialized(Context context, PopupWindowInputNameContextMenu mPopupWindow, View view, String name) {
        //Загрузка XML-макета
        LayoutInflater inflater = LayoutInflater.from(context);

        assert inflater != null;
        View menuView = inflater.inflate(R.layout.popup_window_new_name, null);

        EditText inputText = menuView.findViewById(R.id.input_name);
        inputText.setText(name);

        //Установка содержимого
        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(0, 50);
        mPopupWindow.setDismissListener();
        mPopupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);
    }

    @Override
    protected void setSpecificConfiguration() {
        popupWindow.setOutsideTouchable(false);
        editText = popupWindow.getContentView().findViewById(R.id.input_name);
        // Установка флага, чтобы окно реагировало на изменение размера при открытии клавиатуры
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setLayoutListener();
        setFocusEditText();
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupGlideAnimation);
    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {
        button.setOnClickListener(view -> {
            popupWindow.dismiss();
            if (buttonId == R.id.OK) {
                EditText name = popupWindow.getContentView().findViewById(R.id.input_name);
                if (context instanceof ImageViewActivity) {
                    //((ImageViewActivity) context).rename(name.getText().toString());
                } else
                    CreateAlbumManager.create(context, name.getText().toString());
            }
        });
    }

    //Перерасчет позиции PopupWindow с учетом клавиатуры
    private void setLayoutListener() {
        View rootView;
        rootView = ((Activity) context).getWindow().getDecorView().getRootView();

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                {

                    Log.d("KeyboaerOpen", "KeyboardOpen");
                    Rect r = new Rect();
                    rootView.getWindowVisibleDisplayFrame(r);

                    // Вычисление высоты клавиатуры
                    int keyboardHeight = rootView.getHeight() - r.bottom;

                    int newY = y + keyboardHeight;

                    // Установка позицию PopupWindow
                    popupWindow.update(0, newY, popupWindow.getWidth(), popupWindow.getHeight(), popupWindow.isShowing());
                }
        );
    }

    private void setFocusEditText() {

        // Установка фокуса на клавиатуру
        editText.requestFocus();

        editText.post(() -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void setDismissListener() {
        if (context instanceof ImageViewActivity) {

        }
    }
}
