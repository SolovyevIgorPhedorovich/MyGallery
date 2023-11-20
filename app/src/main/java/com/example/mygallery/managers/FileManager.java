package com.example.mygallery.managers;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumGridActivity;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.*;
import com.example.mygallery.models.constructors.AlbumConstructor;
import com.example.mygallery.popupWindow.PopupWindowProgress;
import com.example.mygallery.viewmodel.BaseViewModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static String cartPath;
    private final BaseViewModel<Model> viewModel;
    private final Context context;
    private int position;
    private boolean isGroupFile = false;
    private final Handler handler;
    private final List<Album> listenerChangeList;

    public FileManager(Context context, BaseViewModel<Model> viewModel) {
        cartPath = context.getApplicationInfo().dataDir + "/Корзина/";
        this.context = context;
        this.viewModel = viewModel;
        this.handler = new Handler(Looper.getMainLooper());
        this.listenerChangeList = new ArrayList<>();
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

    private Model getNewImage(Model oldFile, File newPath) {
        Model newFile = null;
        if (oldFile instanceof Image) {
            newFile = ((Image) oldFile).clone();
        } else if (oldFile instanceof Cart) {
            newFile = ((Cart) oldFile).clone();
        } else if (oldFile instanceof Video) {
            newFile = ((Video) oldFile).clone();
        } else if (oldFile instanceof Favorite) {
            newFile = ((Favorite) oldFile).clone();
        }
        assert newFile != null;
        newFile.setPath(newPath);
        return newFile;
    }

    public void renameFile(Model oldFile, String newName) {
        File newPath = new File(viewModel.getPath(position).getParent() + "/" + newName);
        this.renameFileTo(oldFile, getNewImage(oldFile, newPath));
    }

    private void setInfoChangeAlbum(File oldFile, File newFile) {
        int countChangeFile = viewModel.totalCheckedCount() != 0 ? viewModel.totalCheckedCount() : 1;

        if (oldFile != null) {
            Album initAlbum = AlbumConstructor.initialized(-1, oldFile.getName(), oldFile, -countChangeFile, null);
            listenerChangeList.add(initAlbum);
        }
        if (newFile != null) {
            Album destAlbum = AlbumConstructor.initialized(-1, newFile.getName(), newFile, countChangeFile, null);
            listenerChangeList.add(destAlbum);
        }
    }

    // Перемещение файла
    private void moveFileTo(Model oldFile, File destPath) {
        destPath = new File(destPath, oldFile.getName());
        renameFileTo(oldFile, getNewImage(oldFile, destPath));
        if (!isGroupFile) {
            setInfoChangeAlbum(oldFile.getPath().getParentFile(), destPath.getParentFile());
            updateDatabaseWithNewData();
        }
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
            if (!isGroupFile) {
                updateMediaStoreWithNewFiles(new String[]{String.valueOf(destPath.getParent())});
                setInfoChangeAlbum(null, destPath.getParentFile());
                updateDatabaseWithNewData();
            }
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
            renameFileTo(file, getNewImage(file, trashPath));
        } else {
            try {
                FileUtils.delete(file.getPath());
                if (!isGroupFile) {
                    updateMediaStoreWithNewFiles(new String[]{String.valueOf(file.getPath())});
                    setInfoChangeAlbum(file.getPath().getParentFile(), null);
                    updateDatabaseWithNewData();
                }
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
            if (i == viewModel.totalCheckedCount()) {
                isGroupFile = false;
            }
            removedFileFrom(file);
            i++;
        }
    }

    // Переименование файла
    private void renameFileTo(Model oldFile, Model newFile) {
        try {
            FileUtils.copyFile(oldFile.getPath(), newFile.getPath());
            FileUtils.delete(oldFile.getPath());
            updateViewModel(oldFile, newFile);
            if (!isGroupFile)
                updateMediaStoreWithNewFiles(new String[]{String.valueOf(oldFile.getPath()), String.valueOf(newFile.getPath().getParent())});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateDatabaseWithNewData() {
        viewModel.updateDatabase(listenerChangeList);
        listenerChangeList.clear();
    }

    private void updateMediaStoreWithNewFiles(String[] pathList) {
        Runnable runnable = () -> MediaScannerConnection.scanFile(context, pathList, null, null);
        handler.post(runnable);
    }

    private void updateViewModel(Model oldFile, Model newFile) {
        if (oldFile.equals(newFile)) {
            viewModel.updateData(oldFile.getId(), newFile);
        } else {
            viewModel.removeItem(oldFile.getId());
        }
    }

    private PopupWindowProgress showProcess(int idText) {
        return PopupWindowProgress.show(context, ((AlbumGridActivity) context).findViewById(R.id.fragment_container), idText, viewModel.totalCheckedCount());
    }

    private void reset() {
        this.position = -1;
    }
}