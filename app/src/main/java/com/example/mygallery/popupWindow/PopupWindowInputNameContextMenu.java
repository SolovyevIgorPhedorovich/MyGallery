package com.example.mygallery.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import com.example.mygallery.R;
import com.example.mygallery.activities.imageViewActivity.ImageFileManager;
import com.example.mygallery.activities.imageViewActivity.ImageViewActivity;
import com.example.mygallery.databinding.PopupWindowNewNameBinding;
import com.example.mygallery.managers.CreateAlbumManager;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowInputNameContextMenu extends PopupWindowManager {
    private EditText editText;
    private final PopupWindowNewNameBinding binding;

    private final ImageFileManager.NewName listener;

    public PopupWindowInputNameContextMenu(Context context, ImageFileManager.NewName listener) {
        super(context);
        this.binding = PopupWindowNewNameBinding.inflate(LayoutInflater.from(context));
        this.listener = listener;
        initializedViews();
    }

    private void initializedViews() {
        editText = binding.inputName;
    }

    public static void show(Context context, View view, String name, ImageFileManager.NewName listener) {
        PopupWindowInputNameContextMenu mPopupWindow = new PopupWindowInputNameContextMenu(context, listener);
        initialized(mPopupWindow, view, name);
    }

    private static void initialized(PopupWindowInputNameContextMenu mPopupWindow, View view, String name) {

        View menuView = mPopupWindow.binding.getRoot();

        mPopupWindow.editText.setText(name);

        //Установка содержимого
        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(0, 50);
        mPopupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);
    }

    @Override
    protected boolean setConfiguration() {
        popupWindow.setWidth(context.getResources().getDisplayMetrics().widthPixels - (2 * context.getResources().getDimensionPixelSize(R.dimen.layout_margin_10dp)));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popupbg));
        popupWindow.setOutsideTouchable(false);

        // Установка флага, чтобы окно реагировало на изменение размера при открытии клавиатуры
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setLayoutListener();
        setFocusEditText();
        return true;
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupGlideAnimation);
    }

    @Override
    protected void handleButtonClick(int buttonId) {
        popupWindow.dismiss();
            if (buttonId == R.id.OK) {
                String newName = editText.getText().toString();
                if (context instanceof ImageViewActivity) {
                    listener.setNewName(newName);
                } else {
                    CreateAlbumManager createAlbumManager = new CreateAlbumManager();
                    createAlbumManager.create(context, newName);
                }
            }
    }

    //Перерасчет позиции PopupWindow с учетом клавиатуры
    private void setLayoutListener() {
        View rootView;
        rootView = ((Activity) context).getWindow().getDecorView().getRootView();

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                {
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
}
