package com.example.mygallery.popupWindow;

import android.content.Context;
import android.widget.Button;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowContextMenuActionFile extends PopupWindowManager {

    public PopupWindowContextMenuActionFile(Context context) {
        super(context);
    }

    @Override
    protected void setSpecificConfiguration() {
        popupWindow.setOutsideTouchable(true);
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupWindowAnimation);
    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {
        ImageViewActivity imageViewActivity = (ImageViewActivity) context;
        button.setOnClickListener(view -> {
            popupWindow.dismiss();
            if (buttonId == R.id.moveFile) {
                imageViewActivity.moveFile();
            } else if (buttonId == R.id.copyFile) {
                imageViewActivity.copyFile();
            } else if (buttonId == R.id.slide_show) {
                imageViewActivity.startSlaidShow();
            } else if (buttonId == R.id.print) {
                imageViewActivity.print();
            } else if (buttonId == R.id.rename_file) {
                imageViewActivity.rename(view);
            } else if (buttonId == R.id.set) {
                imageViewActivity.setAs();
            } else if (buttonId == R.id.rotation) {
                imageViewActivity.rotation();
            } else if (buttonId == R.id.choose) {
                imageViewActivity.choose();
            }
        });
    }
}
