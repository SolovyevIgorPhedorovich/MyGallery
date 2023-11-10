package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.mygallery.R;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowAlbumContextMenu extends PopupWindowManager {

    public PopupWindowAlbumContextMenu(Context context) {
        super(context);
    }

    @Override
    protected boolean setConfiguration() {
        return false;
    }

    @Override
    protected void setAnimation() {

    }

    public static void run(Context context, View view) {
        PopupWindowManager mPopupWindow = new PopupWindowAlbumContextMenu(context);
        initialized(context, mPopupWindow, view);
    }

    private static void initialized(Context context, PopupWindowManager mPopupWindow, View view) {
        //Загрузка XML-макета
        LayoutInflater inflater = LayoutInflater.from(context);

        assert inflater != null;
        View menuView = inflater.inflate(R.layout.popup_window_album_context_menu, null);

        //Установка содержимого
        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(40, 80);
        mPopupWindow.showPopupWindow(view, menuView, Gravity.TOP | Gravity.END);

    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {

    }
}
