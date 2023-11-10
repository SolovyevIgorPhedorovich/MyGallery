package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.mygallery.R;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowProgress extends PopupWindowManager {
    private static int MAX_FILE;
    private TextView textProgress;
    private ProgressBar progressBar;

    public PopupWindowProgress(Context context) {
        super(context);
    }

    public static PopupWindowProgress show(Context context, View view, int titleId, int maxFile) {
        PopupWindowProgress popupWindow = new PopupWindowProgress(context);
        initialize(context, popupWindow, view, titleId);
        MAX_FILE = maxFile;
        return popupWindow;
    }

    private static void initialize(Context context, PopupWindowProgress popupWindow, View view, int titleId) {
        // Load the XML layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_progress, null);

        // Set the title and content of the popup window
        TextView textProcess = menuView.findViewById(R.id.popup_text);
        textProcess.setText(titleId);
        popupWindow.setContent(menuView);

        // Set the position and show the popup window
        popupWindow.setPosition(0, 50);
        popupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);

        // Initialize the views
        popupWindow.initializeViews();
    }

    private void initializeViews() {
        textProgress = popupWindow.getContentView().findViewById(R.id.count_file_progress);
        progressBar = popupWindow.getContentView().findViewById(R.id.progress_bar);
    }

    public void updateProgress(int progress) {
        textProgress.setText(context.getString(R.string.count_file_progress_text).replace("{}", String.valueOf(progress)).replace("{}", String.valueOf(MAX_FILE)));
        progressBar.setProgress(progress);
        if (progress == MAX_FILE) {
            popupWindow.dismiss();
        }
    }

    @Override
    protected boolean setConfiguration() {
        // Add any specific configuration here
        return false;
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupGlideAnimation);
    }

    @Override
    protected void setButtonClickListener(Button button, int buttonId) {
        // Add any button click listener here
    }
}
