package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.model.Model;

import java.io.File;

public class PopupWindowWarningGroupDuplicate extends PopupWindowWarningDuplicate {

    private final Callback callback;
    private CheckBox checkBox;

    public PopupWindowWarningGroupDuplicate(Context context, Model originalFile, File targetPath, Callback callback) {
        super(context, originalFile, targetPath, null);
        this.callback = callback;
    }

    public static PopupWindowWarningGroupDuplicate show(Context context, View view, Model originalFile, File targetPath, Callback callback) {
        PopupWindowWarningGroupDuplicate popupWindow = new PopupWindowWarningGroupDuplicate(context, originalFile, targetPath, callback);
        initializePopupWindow(context, popupWindow, view);
        return popupWindow;
    }

    private static void initializePopupWindow(Context context, PopupWindowWarningGroupDuplicate popupWindow, View view) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_warning_group_form, null);

        popupWindow.setContent(menuView);

        popupWindow.setPosition(0, 50);
        popupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);

        popupWindow.initializeViews();
        popupWindow.setText();
        popupWindow.setImage();
    }

    @Override
    protected void handleButtonClick(int buttonId) {
        popupWindow.dismiss();
        if (buttonId == R.id.replace_button) {
            callback.onResume(checkBox.isChecked());
        } else if (buttonId == R.id.skip_button) {
            callback.onSkip(checkBox.isChecked());
        } else if (buttonId == R.id.cancel_button) {
            callback.onCancel();
        }
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        checkBox = popupWindow.getContentView().findViewById(R.id.check_box);
        checkBox.setOnClickListener(null);
    }

    public interface Callback {
        void onResume(boolean isApplyAll);
        void onSkip(boolean isApplyAll);

        void onCancel();
    }
}
