package com.example.mygallery.managers;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Cart;
import com.example.mygallery.models.Favorite;
import com.example.mygallery.models.Image;
import com.example.mygallery.models.Video;
import com.example.mygallery.popupWindow.PopupWindowProgress;
import com.example.mygallery.popupWindow.PopupWindowWarningDuplicate;
import com.example.mygallery.popupWindow.PopupWindowWarningGroupDuplicate;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.CartViewModel;
import com.example.mygallery.viewmodel.FavoritesViewModel;
import com.example.mygallery.viewmodel.ImageViewModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FileManager {
    private final List<File> updatePaths;
    private static File cartPath;
    private final BaseViewModel<Model> viewModel;
    private final Context context;
    private final Handler handler;
    private PopupWindowProgress mPopupWindow;
    private int countDuplicateReplace = 0;
    private int position = -1;
    private boolean isReplace = false;
    private boolean isMoveToCart = false;
    private boolean isApplyAll = false;
    private boolean isCancel = false;
    private boolean isSkip = false;
    private boolean isDuplicate = false;
    private CountDownLatch latch;
    private OnFragmentInteractionListener listener;
    public FileManager(Context context, BaseViewModel<Model> viewModel) {
        cartPath = new File(context.getFilesDir(), "Корзина");
        this.context = context;
        this.viewModel = viewModel;
        this.updatePaths = new ArrayList<>();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void moveFile(File destPath) {
        new FileOperationThread(() -> {
            Model currentModel = viewModel.getItem(position != -1 ? position : 0);
            if (viewModel.totalCheckedCount() != 0) {
                moveFilesTo(destPath);
            } else if (position != -1) {
                moveFileTo(viewModel.getItem(position), destPath);
            }
            if (!isCancel || viewModel.totalCheckedCount() != 0) {
                updateViewModel();
                updateMediaStoreWithNewFiles();
                updateDatabaseWithNewData(currentModel.getPath().getParent(), String.valueOf(destPath));
            }
            reset();
        }).start();
    }

    public FileManager(Context context, BaseViewModel<Model> viewModel, OnFragmentInteractionListener listener) {
        this(context, viewModel);
        this.listener = listener;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void copyFile(File destPath) {
        new FileOperationThread(() -> {
            if (viewModel.totalCheckedCount() != 0) {
                copyFilesTo(destPath);
            } else if (position != -1) {
                copyFileTo(viewModel.getItem(position), destPath);
            }
            if (!isCancel || viewModel.totalCheckedCount() != 0) {
                updateMediaStoreWithNewFiles();
                updateDatabaseWithNewData(null, String.valueOf(destPath));
            }
            reset();
        }).start();
    }

    public void removedFile() {
        if (viewModel instanceof CartViewModel) {
            new FileOperationThread(() -> {
                if (viewModel.totalCheckedCount() != 0) {
                    removedFilesFrom();
                } else if (position != -1) {
                    Model model = viewModel.getItem(position);
                    removedFileFrom(model.getPath());
                }
                updateCartDatabase();
                updateViewModel();
                reset();
            }).start();
        } else {
            isMoveToCart = true;
            moveFile(cartPath);
        }
    }

    public void renameFile(Model oldFile, String newName) {
        File newPath = new File(viewModel.getPath(position).getParent() + "/" + newName);

        renameFileTo(oldFile.getPath(), newPath);
        updateViewModel(oldFile, getNewModel(oldFile, newPath));
        updateMediaStoreWithNewFiles();
    }

    private File getDestFile(Model oldFile, File destPath) {
        if (isMoveToCart) {
            String[] parts = oldFile.getName().split("\\.");
            String extension = parts[parts.length - 1];
            return new File(destPath, oldFile.getPath().hashCode() + "." + extension);
        } else {
            return new File(destPath, oldFile.getName());
        }
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

    private void moveFileTo(Model oldFile, File destPath) {
        File finalDestPath = getDestFile(oldFile, destPath);
        if (!isMoveToCart && finalDestPath.exists()) {
            handleDuplicatesFile(oldFile, finalDestPath, Operation.MOVE);
        } else {
            isDuplicate = false;
            renameFileTo(oldFile.getPath(), finalDestPath);
        }
    }

    private void moveFilesTo(File destPath) {
        showProcess(R.string.moving);
        for (Model file : viewModel.getSelectedItems()) {
            if (isCancel) {
                if (handlerCancel(file)) continue;
                else break;
            }
            updateProgressBar();
            moveFileTo(file, destPath);
            handleDuplicates(file);
        }
    }

    // Копирование файла
    private void copyFileTo(Model file, File destPath) {
        File finalDestPath = new File(destPath, file.getName());
        if (finalDestPath.exists()) {
            handleDuplicatesFile(file, finalDestPath, Operation.COPY);
        } else {
            copyFileTo(file.getPath(), finalDestPath);
        }
    }

    private void copyFileTo(File curPath, File destPath) {
        try {
            FileUtils.copyFile(curPath, destPath);
            updatePaths.add(destPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFilesTo(File destPath) {
        showProcess(R.string.coping);
        for (Model file : viewModel.getSelectedItems()) {
            if (isCancel) {
                if (handlerCancel(file)) continue;
                else break;
            }
            updateProgressBar();
            copyFileTo(file, destPath);
            handleDuplicates(file);
        }
    }

    private void removedFileFrom(File pathFile) {
        try {
            FileUtils.delete(pathFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removedFilesFrom() {
        showProcess(R.string.removing);
        for (Model file : viewModel.getSelectedItems()) {
            updateProgressBar();
            removedFileFrom(file.getPath());
        }
    }

    // Переименование файла
    private void renameFileTo(File oldPath, File newPath) {
        try {
            FileUtils.copyFile(oldPath, newPath);
            FileUtils.delete(oldPath);

            updatePaths.add(oldPath);
            updatePaths.add(newPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean handlerCancel(Model file) {
        if (!updatePaths.isEmpty()) {
            skip(file);
            return true;
        } else {
            return false;
        }
    }

    private void handleDuplicates(Model file) {
        if (isDuplicate && isSkip) {
            skip(file);
        } else if (isDuplicate && isReplace) {
            countDuplicateReplace++;
        }
    }

    private void handleDuplicatesFile(Model file, File finalDestPath, Operation operation) {
        isDuplicate = true;

        if (isApplyAll && isSkip) {
        } else if (isApplyAll && isReplace) {
            if (operation == Operation.COPY) {
                copyFileTo(file.getPath(), finalDestPath);
            } else if (operation == Operation.MOVE) {
                renameFileTo(file.getPath(), finalDestPath);
            }
        } else if (viewModel.totalCheckedCount() > 1) {
            showWarningDuplicateGroup(file, finalDestPath, operation);
        } else {
            showWarningDuplicate(file, finalDestPath, operation);
        }
    }

    private void skip(Model file) {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            viewModel.toggleSelection(file);
            latch.countDown();
        });
        awaitLatch();
    }

    private String[] getMIMEtypes() {
        String[] mimeType = new String[updatePaths.size()];
        for (int i = 0; i < updatePaths.size(); i++) {
            mimeType[i] = getMIME(updatePaths.get(i).getPath());
        }
        return mimeType;
    }

    // Удаление файла

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

    private void updateMediaStoreWithNewFiles() {
        new Thread(() -> {
            String[] pathList = updatePaths.stream().map(File::toString).toArray(String[]::new);
            String[] mimeList = getMIMEtypes();
            MediaScannerConnection.scanFile(context, pathList, mimeList, (s, uri) -> {
                updatePaths.clear();
                Log.d("MediaScanUpdate", "Обновление завершено");
            });
        }).start();
    }

    private void updateDatabaseWithNewData(String curPath, String destPath) {
        viewModel.updateDatabase(curPath, destPath, countDuplicateReplace);
    }

    private void updateCartDatabase() {
        ((CartViewModel) viewModel).updateDatabase(position);
    }

    private void updateViewModel() {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            if (isMoveToCart) {
                if (viewModel instanceof ImageViewModel) {
                    ((ImageViewModel) viewModel).moveToCart(position);
                } else if (viewModel instanceof FavoritesViewModel) {
                    ((FavoritesViewModel) viewModel).moveToCart(position);
                }
            } else {
                if (viewModel instanceof ImageViewModel) {
                    ((ImageViewModel) viewModel).updateFavorites(position, updatePaths);
                } else if (viewModel instanceof FavoritesViewModel) {
                    ((FavoritesViewModel) viewModel).updateFavorites(position, updatePaths);
                }
            }
            viewModel.removeItem(position);
            latch.countDown();
        });
        awaitLatch();
    }

    private void updateViewModel(Model oldFile, Model newFile) {
        viewModel.updateData(oldFile.getId(), newFile);
    }

    private void awaitLatch() {
        if (latch != null) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void reset() {
        isMoveToCart = false;
        isCancel = false;
        isApplyAll = false;
        this.position = -1;
        if (listener != null) {
            handler.post(listener::onPermissionsGranted);
        }
    }

    private void showWarningDuplicate(Model file, File finalDestPath, Operation operation) {
        latch = new CountDownLatch(1);
        handler.post(() -> PopupWindowWarningDuplicate.show(context, null, file, finalDestPath, new PopupWindowWarningDuplicate.Callback() {
            @Override
            public void onResume() {
                isReplace = true;
                runOperation(file, finalDestPath, operation);
                latch.countDown();
            }

            @Override
            public void onCancel() {
                isCancel = true;
                latch.countDown();
            }
        }));
        awaitLatch();
    }

    private void showWarningDuplicateGroup(Model file, File finalDestPath, Operation operation) {
        latch = new CountDownLatch(1);
        handler.post(() -> PopupWindowWarningGroupDuplicate.show(context, null, file, finalDestPath, new PopupWindowWarningGroupDuplicate.Callback() {
            @Override
            public void onResume(boolean isChecked) {
                isReplace = true;
                isSkip = false;
                isApplyAll = isChecked;
                runOperation(file, finalDestPath, operation);
                latch.countDown();
            }

            @Override
            public void onSkip(boolean isChecked) {
                isSkip = true;
                isReplace = false;
                isApplyAll = isChecked;
                latch.countDown();
            }

            @Override
            public void onCancel() {
                isCancel = true;
                isSkip = true;
                latch.countDown();
            }
        }));
        awaitLatch();
    }

    private void showProcess(int idText) {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            mPopupWindow = PopupWindowProgress.show(context, null, idText, viewModel.totalCheckedCount());
            latch.countDown();
        });
        awaitLatch();
    }

    private void updateProgressBar() {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            mPopupWindow.updateProgress();
            latch.countDown();
        });
        awaitLatch();
    }

    private void runOperation(Model file, File finalDestPath, Operation operation) {
        switch (operation) {
            case COPY:
                copyFileTo(file.getPath(), finalDestPath);
                break;
            case MOVE:
                renameFileTo(file.getPath(), finalDestPath);
                break;
        }
    }

    private enum Operation {MOVE, COPY}

    private static class FileOperationThread extends Thread {
        private final Runnable operation;

        public FileOperationThread(Runnable operation) {
            this.operation = operation;
        }

        @Override
        public void run() {
            operation.run();
        }
    }
}