package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.example.mygallery.R;
import com.example.mygallery.activities.imageViewActivity.ImageFileManager;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapter;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.models.Image;
import com.example.mygallery.viewPager.ConfigurationViewPager;
import com.example.mygallery.viewmodel.BaseViewModel;

public class PopupWindowActionFileContextMenu extends PopupWindowManager {

    private final ImageFileManager imageFileManager;
    private final ImagePagerAdapter adapter;
    private final ConfigurationViewPager viewPager;

    public PopupWindowActionFileContextMenu(Context context, Image image, BaseViewModel<Model> viewModel, ConfigurationViewPager viewPager) {
        super(context);
        this.imageFileManager = new ImageFileManager(context, viewModel, image);
        this.adapter = (ImagePagerAdapter) viewPager.getAdapter();
        this.viewPager = viewPager;
    }

    public static void show(Context context, View view, Image image, BaseViewModel<Model> viewModel, ConfigurationViewPager configurationViewPager) {
        PopupWindowManager mPopupWindow = new PopupWindowActionFileContextMenu(context, image, viewModel, configurationViewPager);
        initialized(context, mPopupWindow, view);
    }

    private static void initialized(Context context, PopupWindowManager mPopupWindow, View view) {
        //Загрузка XML-макета

        //Загрузка XML-макета
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_image_view_pager_context_menu, null);

        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(view.getWidth() / 8, view.getHeight() + 30);
        mPopupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM | Gravity.END);
    }

    @Override
    protected void setSpecificConfiguration() {
        popupWindow.setOutsideTouchable(true);
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupContextAnimation);
    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {
        button.setOnClickListener(view -> {
            popupWindow.dismiss();
            if (buttonId == R.id.moveFile) {
                imageFileManager.moveFile();
            } else if (buttonId == R.id.copy_file) {
                imageFileManager.copyFile();
            } else if (buttonId == R.id.slide_show) {
                imageFileManager.startSlidShow(viewPager);
            } else if (buttonId == R.id.print) {
                imageFileManager.print();
            } else if (buttonId == R.id.rename_file) {
                imageFileManager.rename(view);
            } else if (buttonId == R.id.set) {
                imageFileManager.setAs();
            } else if (buttonId == R.id.rotation) {
                imageFileManager.rotation(adapter);
            } else if (buttonId == R.id.choose) {
                imageFileManager.choose();
            }
        });
    }
}
