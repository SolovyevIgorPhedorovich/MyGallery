package com.example.mygallery.activities.imageViewActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapter;
import com.example.mygallery.fragments.AlbumSelectionFragment;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.navigator.FragmentNavigator;
import com.example.mygallery.popupWindow.PopupWindowInputNameContextMenu;
import com.example.mygallery.viewPager.ConfigurationViewPager;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.ImageViewModel;

public class ImageFileManager {

    private final Model data;
    private final Context context;
    private final BaseViewModel<Model> viewModel;
    private Runnable runnable;
    private Handler handler;

    public ImageFileManager(Context context, BaseViewModel<Model> viewModel, Model data) {
        this.data = data;
        this.context = context;
        this.viewModel = viewModel;
    }

    // Запуск перемещения файла
    public void moveFile() {
        FragmentNavigator.openAlbumSelectionFragment(context, AlbumSelectionFragment.Action.MOVE, viewModel, data);
    }

    // Запуск копирования файла
    public void copyFile() {
        FragmentNavigator.openAlbumSelectionFragment(context, AlbumSelectionFragment.Action.COPY, viewModel, data);
    }

    // Создание фона для просмотра изображения
    public void createView() {
        View viewBackground = new View(context);
        setupBackgroundView(viewBackground);
    }

    // Настройка фона для просмотра изображения
    private void setupBackgroundView(View viewBackground) {
        viewBackground.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) ((Activity) context).getWindow().getDecorView()).addView(viewBackground);
        viewBackground.setOnClickListener(view -> {
            removeBackgroundView(viewBackground);
            toggleImageViewActivity();
        });
    }

    // Удаление фона для просмотра изображения
    private void removeBackgroundView(View viewBackground) {
        handler.removeCallbacks(runnable);
        ((ViewGroup) ((Activity) context).getWindow().getDecorView()).removeView(viewBackground);
    }

    // Переключение активности просмотра изображения
    private void toggleImageViewActivity() {
        ((ImageViewActivity) context).toggle();
    }

    // Запуск слайд-шоу изображений
    public void startSlidShow(ConfigurationViewPager viewPager) {
        createView();
        setupSlideShowHandler(viewPager);
        toggleImageViewActivity();
    }

    // Настройка обработчика для слайд-шоу
    private void setupSlideShowHandler(ConfigurationViewPager viewPager) {
        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            viewPager.slideShow();
            handler.postDelayed(runnable, 2000);
        };
        handler.postDelayed(runnable, 2000);
    }

    // Печать
    public void print() {
        // Реализация функциональности печати
    }

    // Переименование файла через контекстное меню
    public void rename() {
        PopupWindowInputNameContextMenu.show(context, null, data.getName(), this::rename);
    }

    // Переименование файла с новым именем
    public void rename(String newName) {
        if (!newName.isEmpty()) {
            FileManager fileManager = new FileManager(context, viewModel);
            fileManager.renameFile(data, newName);
        }
    }

    // Установка изображения в качестве
    public void setAs() {
    }

    // Поворот изображения в адаптере
    public void rotation(ImagePagerAdapter adapter) {
        adapter.rotateImage(data.getId() - 1);
    }

    // Выбор изображения (обновление альбома)
    public void choose() {
        if (viewModel instanceof ImageViewModel) {
            ((ImageViewModel) viewModel).setArtwork(data.getPath());
        }
    }

    // Интерфейс для установки нового имени
    public interface NewName {
        void setNewName(String newName);
    }
}