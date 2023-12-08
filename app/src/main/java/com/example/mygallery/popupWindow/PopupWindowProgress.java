package com.example.mygallery.popupWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.mygallery.R;
import com.example.mygallery.managers.PopupWindowManager;

public class PopupWindowProgress extends PopupWindowManager {
    private TextView textProgress;
    private ProgressBar progressBar;
    private int maxFile;

    public PopupWindowProgress(Context context) {
        super(context);
    }

    public static PopupWindowProgress show(Context context, View view, int titleId, int maxFile) {
        PopupWindowProgress popupWindow = new PopupWindowProgress(context);
        initialize(context, popupWindow, view, titleId);
        popupWindow.setMaxFile(maxFile);
        return popupWindow;
    }

    private static void initialize(Context context, PopupWindowProgress popupWindow, View view, int titleId) {
        // Загрузка макета XML
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_progress, null);

        // Установка заголовка и содержимого PopupWindowProgress
        TextView textProcess = menuView.findViewById(R.id.popup_text);
        textProcess.setText(titleId);
        popupWindow.setContent(menuView);

        // Установка позиции и отображение PopupWindowProgress
        popupWindow.setPosition(0, 50);
        popupWindow.showPopupWindow(view, menuView, Gravity.BOTTOM);

        // Инициализация представлений
        popupWindow.initializeViews(menuView);
    }

    private void initializeViews(View menuView) {
        textProgress = menuView.findViewById(R.id.count_file_progress);
        progressBar = menuView.findViewById(R.id.progress_bar);
    }

    // Устанавливает максимальное количество файлов для отслеживания прогресса
    private void setMaxFile(int maxFile) {
        this.maxFile = maxFile;
    }

    //Обновляет прогресс и текст в PopupWindowProgress
    public void updateProgress() {
        int progress = progressBar.getProgress() + 1;
        textProgress.setText(context.getString(R.string.count_file_progress_text).replace("{}", String.valueOf(progress)).replace("{}", String.valueOf(maxFile)));
        progressBar.setProgress(progress);
        if (progress == maxFile) {
            popupWindow.dismiss();
        }
    }

    @Override
    protected boolean setConfiguration() {
        popupWindow.setWidth(context.getResources().getDisplayMetrics().widthPixels - (2 * context.getResources().getDimensionPixelSize(R.dimen.layout_margin_10dp)));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popupbg));
        return true;
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupGlideAnimation);
    }

    @Override
    protected void handleButtonClick(int buttonId) {
    }
}