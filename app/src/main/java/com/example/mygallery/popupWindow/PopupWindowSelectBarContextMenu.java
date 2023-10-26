package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.example.mygallery.R;
import com.example.mygallery.fragments.ActionFileFragment;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.models.Image;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.navigator.FragmentManager;
import com.example.mygallery.viewmodel.BaseViewModel;

public class PopupWindowSelectBarContextMenu extends PopupWindowManager {

    private final Model file;
    private final BaseViewModel<Model> viewModel;


    public PopupWindowSelectBarContextMenu(Context context, BaseViewModel<Model> viewModel, Model file) {
        super(context);
        this.file = file;
        this.viewModel = viewModel;
    }

    public static void show(Context context, View view, BaseViewModel<Model> viewModel, Model file) {
        PopupWindowManager mPopupWindow = new PopupWindowSelectBarContextMenu(context, viewModel, file);
        initialized(context, mPopupWindow, view);
    }

    private static void initialized(Context context, PopupWindowManager mPopupWindow, View view) {
        //Загрузка XML-макета

        //Загрузка XML-макета
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_selectbar_context_menu, null);

        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(view.getWidth() / 8, view.getHeight() + 30);
        mPopupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM | Gravity.END);
    }

    @Override
    protected void setSpecificConfiguration() {

    }

    @Override
    protected void setAnimation() {

    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {
        button.setOnClickListener(view -> {
            popupWindow.dismiss();
            if (buttonId == R.id.copy_file) {
                FragmentManager.openActionFragment(context, ActionFileFragment.Action.COPY, viewModel, file);
            } else if (buttonId == R.id.print) {
                //fileManager.print();
            } else if (buttonId == R.id.information) {
                //
            }
        });
    }
}
