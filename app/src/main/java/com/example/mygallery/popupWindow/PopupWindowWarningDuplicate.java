package com.example.mygallery.popupWindow;

import android.content.Context;
import android.graphics.Point;
import android.icu.text.SimpleDateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.viewimage.LoadImage;

import java.io.File;
import java.util.Date;
import java.util.Locale;

public class PopupWindowWarningDuplicate extends PopupWindowManager {

    private final Model originalFile;
    private final File targetPath;
    private final Callback callback;
    private TextView nameDuplicate, originalSize, originalDateModified, targetSize, targetDateModified;
    private ImageView originalImage, targetImage;

    public PopupWindowWarningDuplicate(Context context, Model originalFile, File targetPath, Callback callback) {
        super(context);
        this.originalFile = originalFile;
        this.targetPath = targetPath;
        this.callback = callback;
    }

    public static PopupWindowWarningDuplicate show(Context context, View view, Model originalFile, File targetPath, Callback callback) {
        PopupWindowWarningDuplicate popupWindow = new PopupWindowWarningDuplicate(context, originalFile, targetPath, callback);
        initializePopupWindow(context, popupWindow, view);
        return popupWindow;
    }

    private static void initializePopupWindow(Context context, PopupWindowWarningDuplicate popupWindow, View view) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_warning_form, null);
        popupWindow.setContent(menuView);

        popupWindow.setPosition(0, 50);
        popupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);

        popupWindow.initializeViews();
        popupWindow.setText();
        popupWindow.setImage();
    }

    @Override
    protected boolean setConfiguration() {
        popupWindow.setTouchable(true);
        return false;
    }

    @Override
    protected void setAnimation() {
    }

    @Override
    protected void handleButtonClick(int buttonId) {
        popupWindow.dismiss();
        if (buttonId == R.id.replace_button) {
            callback.onResume();
        } else if (buttonId == R.id.cancel_button) {
            callback.onCancel();
        }
    }

    protected void setText() {
        String fileAlreadyExists = context.getResources().getString(R.string.file_already_exists, originalFile.getName());
        nameDuplicate.setText(fileAlreadyExists);
        originalSize.setText(context.getResources().getString(R.string.size, originalFile.getSize() + " байт"));
        originalDateModified.setText(context.getResources().getString(R.string.modified, originalFile.getDate()));
        targetSize.setText(context.getResources().getString(R.string.size, targetPath.length() + " байт"));
        targetDateModified.setText(context.getResources().getString(R.string.modified, getDateModified()));
    }

    protected void setImage() {
        int imageSize = context.getResources().getDimensionPixelSize(R.dimen.size_image_in_list);
        LoadImage.setImage(originalFile.getPath(), originalImage, new Point(imageSize, imageSize));
        LoadImage.setImage(targetPath, targetImage, new Point(imageSize, imageSize));
    }

    private String getDateModified() {
        long fileCreationDate = targetPath.lastModified();
        Date creationDate = new Date(fileCreationDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(creationDate);
    }

    protected void initializeViews() {
        nameDuplicate = popupWindow.getContentView().findViewById(R.id.duplicate_name);
        originalSize = popupWindow.getContentView().findViewById(R.id.original_file_size);
        originalDateModified = popupWindow.getContentView().findViewById(R.id.original_date_modified);
        originalImage = popupWindow.getContentView().findViewById(R.id.original_image);
        targetSize = popupWindow.getContentView().findViewById(R.id.target_file_size);
        targetDateModified = popupWindow.getContentView().findViewById(R.id.target_date_modified);
        targetImage = popupWindow.getContentView().findViewById(R.id.target_image);
    }

    public interface Callback {
        void onResume();

        void onCancel();
    }
}
