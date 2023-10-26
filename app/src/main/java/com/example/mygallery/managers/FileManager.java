package com.example.mygallery.managers;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumGridActivity;
import com.example.mygallery.models.Image;
import com.example.mygallery.models.Video;
import com.example.mygallery.models.constructors.ImageFileConstructor;
import com.example.mygallery.popupWindow.PopupWindowProgress;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.interfaces.model.Model;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileManager {
    private static String cartPath;
    private final BaseViewModel<Model> viewModel;
    private final Context context;
    private int position;
    private boolean isGroupFile = false;

    public FileManager(Context context, BaseViewModel<Model> viewModel) {
        cartPath = context.getApplicationInfo().dataDir + "/Корзина/";
        this.context = context;
        this.viewModel = viewModel;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void moveFile(File destPath) {
        if (viewModel.totalCheckedCount() != 0)
            moveFilesTo(destPath);
        else if (position != -1)
            moveFileTo(viewModel.getItem(position), destPath);
        reset();
    }

    public void copyFile(File destPath) {
        if (viewModel.totalCheckedCount() != 0)
            copyFilesTo(destPath);
        else if (position != -1)
            copyFileTo(viewModel.getItem(position), destPath);
        reset();
    }

    public void removedFile() {
        if (viewModel.totalCheckedCount() != 0)
            removedFilesFrom();
        else if (position != -1)
            removedFileFrom(viewModel.getItem(position));
        reset();
    }

    public void renameFile(Model file, String newName) {
        File newPath = new File(viewModel.getPath(position).getParent() + "/" + newName);
        Model newFile = null;
        if (file instanceof Image) {
            Image image = (Image) file;
            newFile = ImageFileConstructor.initialized(image.id, newName, newPath, image.size, image.isFavorite);
        } else if (file instanceof Video)
            //TODO add video constructor

            this.renameFileTo(file, newFile.getPath());
        updateImageInViewModel(file.getId(), (Image) newFile);
    }


    // Перемещение файла
    private void moveFileTo(Model file, File destPath) {
        destPath = new File(destPath, file.getName());
        renameFileTo(file, destPath);
    }

    private void moveFilesTo(File destPath) {
        int i = 1;
        isGroupFile = true;
        PopupWindowProgress mPopupWindow = showProcess(R.string.moving);
        for (Model file : viewModel.getSelectedItems()) {
            mPopupWindow.updateProgress(i);
            if (i == viewModel.totalCheckedCount())
                isGroupFile = false;

            moveFileTo(file, destPath);
            i++;
        }
    }

    // Копирование файла
    private void copyFileTo(Model file, File destPath) {
        destPath = new File(destPath, file.getName());
        try {
            FileUtils.copyFile(file.getPath(), destPath);
            updateDatabaseWithNewData();
            if (!isGroupFile)
                updateMediaStoreWithNewFiles(new String[]{String.valueOf(destPath.getParent())});

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFilesTo(File destPath) {
        int i = 1;
        isGroupFile = true;
        PopupWindowProgress mPopupWindow = showProcess(R.string.coping);
        for (Model file : viewModel.getSelectedItems()) {
            mPopupWindow.updateProgress(i);
            if (i == viewModel.totalCheckedCount())
                isGroupFile = false;

            copyFileTo(file, destPath);
            i++;
        }
    }

    // Удаление файла
    private void removedFileFrom(Model file) {
        File trashPath = new File(cartPath);
        if (!file.getPath().equals(trashPath)) {
            renameFileTo(file, trashPath);
        } else {
            try {
                FileUtils.delete(file.getPath());
                if (!isGroupFile)
                    updateMediaStoreWithNewFiles(new String[]{String.valueOf(file.getPath())});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void removedFilesFrom() {
        int i = 1;
        isGroupFile = true;
        PopupWindowProgress mPopupWindow = showProcess(R.string.removing);
        for (Model file : viewModel.getSelectedItems()) {
            mPopupWindow.updateProgress(i);
            if (i == viewModel.totalCheckedCount())
                isGroupFile = false;

            removedFileFrom(file);
            i++;
        }
    }


    // Переименование файла
    private void renameFileTo(Model file, File newFile) {
        try {
            FileUtils.copyFile(file.getPath(), newFile);
            FileUtils.delete(file.getPath());
            if (!Objects.equals(newFile.getParent(), file.getPath().getParent()))
                updateViewModel(file);
            if (!isGroupFile)
                updateMediaStoreWithNewFiles(new String[]{String.valueOf(file.getPath()), String.valueOf(newFile.getParent())});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateImageInViewModel(int id, Image image) {
        viewModel.updateData(id, image);
    }

    private void updateDatabaseWithNewData() {
        viewModel.updateDatabase();
    }

    private void updateMediaStoreWithNewFiles(String[] pathList) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> MediaScannerConnection.scanFile(context, pathList, null, null);
        handler.post(runnable);
    }

    private void updateViewModel(Model file) {
        viewModel.removeItem(file.getId());
        updateDatabaseWithNewData();
    }

    private PopupWindowProgress showProcess(int idText) {
        return PopupWindowProgress.show(context, ((AlbumGridActivity) context).findViewById(R.id.fragmentContainer), idText, viewModel.totalCheckedCount());
    }

    private void reset() {
        this.position = -1;
    }
}