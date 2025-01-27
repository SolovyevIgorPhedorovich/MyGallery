package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumSelected;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.navigator.ActivityNavigator;
import com.example.mygallery.navigator.FragmentNavigator;

public class PopupWindowSyncMenu extends PopupWindowManager {

    private static final int DEFAULT_X_POSITION = 40;
    private static final int DEFAULT_Y_POSITION = 80;

    public PopupWindowSyncMenu(Context context) {
        super(context);
    }

    @Override
    protected boolean setConfiguration() {
        return false;
    }

    @Override
    protected void setAnimation() {
    }

    public static void run(Context context, View anchorView) {
        PopupWindowManager popupWindow = new PopupWindowSyncMenu(context);
        initializePopupWindow(context, popupWindow, anchorView);
    }

    private static void initializePopupWindow(Context context, PopupWindowManager popupWindow, View anchorView) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_share, null);

        popupWindow.setContent(menuView);
        popupWindow.setPosition(DEFAULT_X_POSITION, DEFAULT_Y_POSITION);
        popupWindow.showPopupWindow(anchorView, menuView, Gravity.TOP | Gravity.END);
    }

    @Override
    protected void handleButtonClick(int buttonId) {
        popupWindow.dismiss();

        if (buttonId == R.id.button_upload) {
            // openAlbumChooser();
            ActivityNavigator activityNavigator = new ActivityNavigator(context);
            activityNavigator.navigateToActivity(AlbumSelected.class);
            return;
        }
        else if (buttonId == R.id.button_load)
            // loadFileFromNextclouddd();
            return;
    }
}
