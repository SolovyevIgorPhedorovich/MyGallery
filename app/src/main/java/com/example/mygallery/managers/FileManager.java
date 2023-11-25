package com.example.mygallery.managers;

import android.content.Context;
import android.media.MediaScannerConnection;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumGridActivity;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Cart;
import com.example.mygallery.models.Favorite;
import com.example.mygallery.models.Image;
import com.example.mygallery.models.Video;
import com.example.mygallery.popupWindow.PopupWindowProgress;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.CartViewModel;
import com.example.mygallery.viewmodel.ImageViewModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static File cartPath;
    private final BaseViewModel<Model> viewModel;
    private final Context context;
    private final List<String> updatePaths;
    private int position = -1;
    private boolean isMoveToCart = false;
    private OnFragmentInteractionListener listener;

    public FileManager(Context context, BaseViewModel<Model> viewModel) {
        cartPath = new File(context.getFilesDir(), "Корзина");
        this.context = context;
        this.viewModel = viewModel;
        this.updatePaths = new ArrayList<>();
    }

    public FileManager(Context context, BaseViewModel<Model> viewModel, OnFragmentInteractionListener listener) {
        this(context, viewModel);
        this.listener = listener;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void moveFile(File destPath) {
        Model currentModel = viewModel.getItem(position != -1 ? position : 0);
        if (viewModel.totalCheckedCount() != 0)
            moveFilesTo(destPath);
        else if (position != -1)
            moveFileTo(viewModel.getItem(position), destPath);

        updateViewModel();
        updateMediaStoreWithNewFiles();
        updateDatabaseWithNewData(currentModel.getPath().getParent(), String.valueOf(destPath));
        reset();
    }

    public void copyFile(File destPath) {
        if (viewModel.totalCheckedCount() != 0)
            copyFilesTo(destPath);
        else if (position != -1)
            copyFileTo(viewModel.getItem(position), destPath);
        updateMediaStoreWithNewFiles();
        updateDatabaseWithNewData(null, String.valueOf(destPath));
        reset();
    }

    public void removedFile() {
        if (viewModel instanceof CartViewModel) {
            if (viewModel.totalCheckedCount() != 0) {
                removedFilesFrom();
            } else if (position != -1) {
                Model model = viewModel.getItem(position);
                removedFileFrom(model.getPath());
            }
            updateCartDatabase();
            updateViewModel();
        } else {
            isMoveToCart = true;
            moveFile(cartPath);
        }
        reset();
    }

    private Model getNewModel(Model oldFile, File newPath) {
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

        renameFileTo(oldFile, newPath);
        updateViewModel(oldFile, getNewModel(oldFile, newPath));
        updateMediaStoreWithNewFiles();
    }

    // Перемещение файла
    private void moveFileTo(Model oldFile, File destPath) {
        destPath = new File(destPath, oldFile.getName());
        renameFileTo(oldFile, destPath);
    }

    private void moveFilesTo(File destPath) {
        int i = 1;
        PopupWindowProgress mPopupWindow = showProcess(R.string.moving);
        for (Model file : viewModel.getSelectedItems()) {
            mPopupWindow.updateProgress(i);
            moveFileTo(file, destPath);
            i++;
        }
    }

    // Копирование файла
    private void copyFileTo(Model file, File destPath) {
        destPath = new File(destPath, file.getName());
        try {
            FileUtils.copyFile(file.getPath(), destPath);
            updatePaths.add(String.valueOf(destPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFilesTo(File destPath) {
        int i = 1;
        PopupWindowProgress mPopupWindow = showProcess(R.string.coping);
        for (Model file : viewModel.getSelectedItems()) {
            mPopupWindow.updateProgress(i);
            copyFileTo(file, destPath);
            i++;
        }
    }

    // Удаление файла
    private void removedFileFrom(File pathFile) {
        try {
            FileUtils.delete(pathFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removedFilesFrom() {
        int i = 1;
        PopupWindowProgress mPopupWindow = showProcess(R.string.removing);
        for (Model file : viewModel.getSelectedItems()) {
            mPopupWindow.updateProgress(i);
            removedFileFrom(file.getPath());
            i++;
        }
    }

    // Переименование файла
    private void renameFileTo(Model oldFile, File newPath) {
        try {
            FileUtils.copyFile(oldFile.getPath(), newPath);
            FileUtils.delete(oldFile.getPath());

            updatePaths.add(String.valueOf(oldFile.getPath()));
            updatePaths.add(String.valueOf(newPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateDatabaseWithNewData(String curPath, String destPath) {
        viewModel.updateDatabase(curPath, destPath);
    }

    private void updateCartDatabase() {
        ((CartViewModel) viewModel).updateDatabase(position);
    }

    private String getMIME(String path) {
        String mimeType = null;
        try {
            URL url = new URL("file://" + path);
            URLConnection connection = url.openConnection();
            mimeType = connection.getContentType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mimeType;
    }

    private String[] getMIMEtypes() {
        String[] mimeType = new String[updatePaths.size()];
        for (int i = 0; i < updatePaths.size(); i++) {
            mimeType[i] = getMIME(updatePaths.get(i));
        }
        return mimeType;
    }

    private void updateMediaStoreWithNewFiles() {
        new Thread(() -> {
            String[] pathList = updatePaths.toArray(new String[0]);
            String[] mimeList = getMIMEtypes();
            MediaScannerConnection.scanFile(context, pathList, mimeList, (s, uri) -> updatePaths.clear());
        }).start();
    }

    private void updateViewModel(Model oldFile, Model newFile) {
        viewModel.updateData(oldFile.getId(), newFile);
    }

    private void updateViewModel() {
        if (isMoveToCart) {
            ((ImageViewModel) viewModel).moveToCart(position);
        }
        viewModel.removeItem(position);
    }

    private PopupWindowProgress showProcess(int idText) {
        return PopupWindowProgress.show(context, ((AlbumGridActivity) context).findViewById(R.id.fragment_container), idText, viewModel.totalCheckedCount());
    }

    private void reset() {
        isMoveToCart = true;
        this.position = -1;
        if (listener != null) {
            listener.onPermissionsGranted();
        }
    }
}