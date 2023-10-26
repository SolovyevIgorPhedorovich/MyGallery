package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.mygallery.R;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewmodel.BaseViewModel;

public class PopupWindowRemovedContextMenu extends PopupWindowManager {
    private final FileManager fileManager;

    private PopupWindowRemovedContextMenu(Context context, BaseViewModel<Model> viewModel) {
        super(context);
        this.fileManager = new FileManager(context, viewModel);
    }

    public PopupWindowRemovedContextMenu(Context context, BaseViewModel<Model> viewModel, int position) {
        this(context, viewModel);
        fileManager.setPosition(position);
    }

    public static void run(Context context, View view, BaseViewModel<Model> viewModel, int position) {
        PopupWindowManager mPopupWindow = new PopupWindowRemovedContextMenu(context, viewModel, position);
        initialized(context, mPopupWindow, view, 1);
    }

    public static void run(Context context, View view, BaseViewModel<Model> viewModel) {
        PopupWindowManager mPopupWindow = new PopupWindowRemovedContextMenu(context, viewModel);
        initialized(context, mPopupWindow, view, viewModel.getSelectedItems().size());
    }

    private static void initialized(Context context, PopupWindowManager mPopupWindow, View view, int countSelect) {
        //Загрузка XML-макета
        LayoutInflater inflater = LayoutInflater.from(context);
        assert inflater != null;
        View menuView = inflater.inflate(R.layout.popup_window_removed_context_menu, null);

        ((TextView) menuView.findViewById(R.id.popup_text)).setText(context.getResources().getQuantityString(R.plurals.delete_elements, countSelect, countSelect));

        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(view.getWidth() / 8, view.getHeight() + 30);
        mPopupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);
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
            if (buttonId == R.id.removed_button) {
                fileManager.removedFile();
            }
        });
    }
}
