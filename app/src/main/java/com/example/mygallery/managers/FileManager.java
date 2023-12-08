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
    private boolean isMoveToCart = false;
    private boolean isReset = false;
    private boolean isReplace = false;
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

    public FileManager(Context context, BaseViewModel<Model> viewModel, OnFragmentInteractionListener listener) {
        this(context, viewModel);
        this.listener = listener;
    }

    /**
     * Метод для перемещения файла в указанный путь.
     *
     * @param destPath Путь, куда нужно переместить файл.
     */
    public void moveFile(File destPath) {
        new FileOperationThread(() -> {
            // Получение текущей модели из ViewModel
            Model currentModel = viewModel.getItem(position != -1 ? position : 0);

            // Проверка, есть ли выбранные элементы для перемещения
            if (viewModel.totalCheckedCount() != 0) {
                moveFilesTo(destPath);
            } else if (position != -1) {
                moveFileTo(viewModel.getItem(position), destPath);
            }

            // Если операция не отменена или есть выбранные элементы, обновление хранилища медиафайлов
            // и базы данных с новыми файлами, а также обновление ViewModel
            if (!isCancel || viewModel.totalCheckedCount() != 0) {
                updateMediaStoreWithNewFiles();
                updateDatabaseWithNewData(currentModel.getPath().getParent(), String.valueOf(destPath));
                updateViewModel();
            }
            // Сброс состояния
            reset();
        }).start();
    }

    /**
     * Метод для установки позиции в списке.
     *
     * @param position Позиция в списке, на которую нужно установить курсор.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Метод для копирования файла в указанный путь.
     *
     * @param destPath Путь, куда нужно скопировать файл.
     */
    public void copyFile(File destPath) {
        new FileOperationThread(() -> {
            // Проверка, есть ли выбранные элементы для копирования
            if (viewModel.totalCheckedCount() != 0) {
                copyFilesTo(destPath);
            } else if (position != -1) {
                copyFileTo(viewModel.getItem(position), destPath);
            }

            // Если операция не отменена или есть выбранные элементы, обновление хранилища медиафайлов
            // и базы данных с новыми файлами
            if (!isCancel || viewModel.totalCheckedCount() != 0) {
                updateMediaStoreWithNewFiles();
                updateDatabaseWithNewData(null, String.valueOf(destPath));
            }
            // Сброс состояния
            reset();
        }).start();
    }

    /**
     * Метод для удаления файла или файлов.
     * Если ViewModel является CartViewModel, то операция удаления выполняется над корзиной,
     * иначе файл перемещается в корзину.
     */
    public void removedFile() {
        if (viewModel instanceof CartViewModel) {
            new FileOperationThread(() -> {
                // Проверка, есть ли выбранные элементы для удаления
                if (viewModel.totalCheckedCount() != 0) {
                    removedFilesFrom();
                } else if (position != -1) {
                    Model model = viewModel.getItem(position);
                    removedFileFrom(model.getPath());
                }

                // Обновление базы данных корзины и ViewModel
                updateCartDatabase();
                updateViewModel();

                // Сброс состояния
                reset();
            }).start();
        } else {
            // Установка флага для перемещения в корзину и вызов метода moveFile
            isMoveToCart = true;
            moveFile(cartPath);
        }
    }

    /**
     * Метод для переименования файла.
     *
     * @param oldFile Старый файл, который нужно переименовать.
     * @param newName Новое имя файла.
     */
    public void renameFile(Model oldFile, String newName) {
        // Формирование нового пути с новым именем файла
        File newPath = new File(oldFile.getPath().getParent(), newName + getFormatFile(oldFile.getName()));

        // Вызов метода для переименования файла
        renameFileTo(oldFile.getPath(), newPath);

        // Обновление ViewModel с новой моделью и обновление хранилища медиафайлов
        updateViewModel(oldFile, getNewModel(oldFile, newPath));
        updateMediaStoreWithNewFiles();
    }

    /**
     * Метод для сброса файла.
     * Устанавливает флаг сброса и вызывает метод moveFile с аргументом null.
     */
    public void resetFile() {
        isReset = true;
        moveFile(null);
    }

    // Получение расширения файла из его имени
    private String getFormatFile(String name) {
        String[] parts = name.split("\\.");
        return "." + parts[parts.length - 1];
    }

    // Получение пути для нового файла в целевой директории
    private File getDestFile(Model oldFile, File destPath) {
        if (isMoveToCart) {
            return new File(destPath, oldFile.getPath().hashCode() + getFormatFile(oldFile.getName()));
        } else if (isReset) {
            return ((Cart) oldFile).initialPath;
        } else {
            return new File(destPath, oldFile.getName());
        }
    }

    // Получение новой модели файла с обновленным путем
    private Model getNewModel(Model oldFile, File newPath) {
        Model newFile = null;
        if (oldFile instanceof Image) {
            newFile = ((Image) oldFile).clone();
        } else if (oldFile instanceof Cart) {
            newFile = ((Cart) oldFile).clone();
        } else if (oldFile instanceof Video) {
            newFile = ((Video) oldFile).clone();
        }
        assert newFile != null;
        newFile.setPath(newPath);
        return newFile;
    }

    // Перемещение файла в целевую директорию
    private void moveFileTo(Model oldFile, File destPath) {
        File finalDestPath = getDestFile(oldFile, destPath);
        if (!isMoveToCart && finalDestPath.exists()) {
            // Обработка ситуации, если файл с таким именем уже существует
            handleDuplicatesFile(oldFile, finalDestPath, Operation.MOVE);
        } else {
            isDuplicate = false;
            renameFileTo(oldFile.getPath(), finalDestPath);
        }
    }

    // Перемещение выбранных файлов в целевую директорию
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

    // Копирование файла в целевую директорию
    private void copyFileTo(Model file, File destPath) {
        File finalDestPath = new File(destPath, file.getName());
        if (finalDestPath.exists()) {
            // Обработка ситуации, если файл с таким именем уже существует
            handleDuplicatesFile(file, finalDestPath, Operation.COPY);
        } else {
            copyFileTo(file.getPath(), finalDestPath);
        }
    }

    // Копирование файла с текущего пути на новый
    private void copyFileTo(File curPath, File destPath) {
        try {
            FileUtils.copyFile(curPath, destPath);
            updatePaths.add(destPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Копирование выбранных файлов в целевую директорию
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

    // Удаление файла из директории
    private void removedFileFrom(File pathFile) {
        try {
            FileUtils.delete(pathFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Удаление выбранных файлов из директории
    private void removedFilesFrom() {
        showProcess(R.string.removing);
        for (Model file : viewModel.getSelectedItems()) {
            updateProgressBar();
            removedFileFrom(file.getPath());
        }
    }

    // Переименование файла, заменяя его старый путь новым
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

    // Обработка отмены операции
    private boolean handlerCancel(Model file) {
        // Если есть файлы для обновления, пропустить текущий файл и вернуть true
        if (!updatePaths.isEmpty()) {
            skip(file);
            return true;
        } else {
            // Нет файлов для обновления, вернуть false
            return false;
        }
    }

    // Обработка дубликатов файлов
    private void handleDuplicates(Model file) {
        // Если файл является дубликатом и требуется пропустить
        if (isDuplicate && isSkip) {
            skip(file);
        } else if (isDuplicate && isReplace) {
            // Если файл является дубликатом и требуется заменить, увеличить счетчик
            countDuplicateReplace++;
        }
    }

    // Обработка дубликатов файла при операции
    private void handleDuplicatesFile(Model file, File finalDestPath, Operation operation) {
        // Установка флага дубликата в true
        isDuplicate = true;

        if (isApplyAll && isSkip) {
            // Если выбрано "Применить ко всем" и требуется пропустить
        } else if (isApplyAll && isReplace) {
            // Если выбрано "Применить ко всем" и требуется заменить
            if (operation == Operation.COPY) {
                // Если операция копирования, скопировать файл
                copyFileTo(file.getPath(), finalDestPath);
            } else if (operation == Operation.MOVE) {
                // Если операция перемещения, переименовать файл
                renameFileTo(file.getPath(), finalDestPath);
            }
        } else if (viewModel.totalCheckedCount() > 1) {
            // Если выбрано более одного файла, показать предупреждение о дубликатах в группе
            showWarningDuplicateGroup(file, finalDestPath, operation);
        } else {
            // Если выбран только один файл, показать предупреждение о дубликате
            showWarningDuplicate(file, finalDestPath, operation);
        }
    }

    // Пропустить файл
    private void skip(Model file) {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            // Переключение выделения для файла в модели представления
            viewModel.toggleSelection(file);
            latch.countDown();
        });
        // Ожидание завершения операции
        awaitLatch();
    }

    private String[] getMIMEtypes() {
        // Получение массива MIME-типов для каждого пути в списке обновлений
        String[] mimeType = new String[updatePaths.size()];
        for (int i = 0; i < updatePaths.size(); i++) {
            mimeType[i] = getMIME(updatePaths.get(i).getPath());
        }
        return mimeType;
    }

    // Удаление файла
    private String getMIME(String path) {
        // Получение MIME-типа файла по указанному пути
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

    // Обновление MediaStore новыми файлами
    private void updateMediaStoreWithNewFiles() {
        new Thread(() -> {
            // Преобразование списка путей в массив строк
            String[] pathList = updatePaths.stream().map(File::toString).toArray(String[]::new);
            // Получение MIME-типов для обновленных файлов
            String[] mimeList = getMIMEtypes();
            // Сканирование файлов в MediaStore
            MediaScannerConnection.scanFile(context, pathList, mimeList, (s, uri) -> {
                updatePaths.clear();
                Log.d("MediaScanUpdate", "Обновление завершено");
            });
        }).start();
    }

    // Обновление базы данных новыми данными
    private void updateDatabaseWithNewData(String curPath, String destPath) {
        // Если требуется сбросить состояние, сбросить текущий путь и обновить базу данных корзины
        if (isReset) {
            curPath = null;
            updateCartDatabase();
        }
        // Обновление базы данных с использованием ViewModel
        viewModel.updateDatabase(curPath, destPath, countDuplicateReplace);
    }

    // Обновление базы данных корзины
    private void updateCartDatabase() {
        ((CartViewModel) viewModel).updateDatabase(position);
    }

    // Обновление ViewModel
    private void updateViewModel() {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            // Если требуется переместить в корзину
            if (isMoveToCart) {
                // Обновление ViewModel в зависимости от типа ViewModel
                if (viewModel instanceof ImageViewModel) {
                    ((ImageViewModel) viewModel).moveToCart(position);
                } else if (viewModel instanceof FavoritesViewModel) {
                    ((FavoritesViewModel) viewModel).moveToCart(position);
                }
            } else {
                // Обновление ViewModel в зависимости от типа ViewModel и действия пользователя
                if (viewModel instanceof ImageViewModel) {
                    ((ImageViewModel) viewModel).updateFavorites(position, updatePaths);
                } else if (viewModel instanceof FavoritesViewModel) {
                    ((FavoritesViewModel) viewModel).updateFavorites(position, updatePaths);
                }
            }
            // Удаление элемента в ViewModel и сигнализация о завершении операции
            viewModel.removeItem(position);
            latch.countDown();
        });
        awaitLatch();
    }

    // Обновление ViewModel с использованием старого и нового файла
    private void updateViewModel(Model oldFile, Model newFile) {
        viewModel.updateData(oldFile.getId(), newFile);
    }

    // Ожидание завершения latch
    private void awaitLatch() {
        if (latch != null) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Сброс состояния
    private void reset() {
        // Сброс всех флагов состояния и позиции
        isMoveToCart = false;
        isCancel = false;
        isApplyAll = false;
        isReset = false;
        isSkip = false;
        isReplace = false;
        this.position = -1;
        // Вызов обработчика события, если он установлен
        if (listener != null) {
            handler.post(listener::onPermissionsGranted);
        }
    }

    // Метод для отображения предупреждения о дублировании файла
    private void showWarningDuplicate(Model file, File finalDestPath, Operation operation) {
        // Создаем счетчик для ожидания завершения действий в PopupWindow
        latch = new CountDownLatch(1);

        // Постинг в основной поток для отображения предупреждения о дублировании
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

        // Ожидаем завершения операции в PopupWindow
        awaitLatch();
    }

    // Метод для отображения предупреждения о дублировании группы файлов
    private void showWarningDuplicateGroup(Model file, File finalDestPath, Operation operation) {
        // Создаем счетчик для ожидания завершения действий в PopupWindow
        latch = new CountDownLatch(1);

        // Постинг в основной поток для отображения предупреждения о дублировании группы
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

        // Ожидаем завершения операции в PopupWindow
        awaitLatch();
    }

    // Метод для отображения процесса с заданным текстом
    private void showProcess(int idText) {
        // Создаем счетчик для ожидания завершения действий в PopupWindow
        latch = new CountDownLatch(1);

        // Постинг в основной поток для отображения процесса
        handler.post(() -> {
            // Отображаем окно с прогрессом
            mPopupWindow = PopupWindowProgress.show(context, null, idText, viewModel.totalCheckedCount());
            latch.countDown();
        });

        // Ожидаем завершения операции в PopupWindow
        awaitLatch();
    }

    // Метод для обновления прогресса в PopupWindow
    private void updateProgressBar() {
        // Создаем счетчик для ожидания завершения действий в PopupWindow
        latch = new CountDownLatch(1);

        // Постинг в основной поток для обновления прогресса
        handler.post(() -> {
            // Обновляем прогресс в PopupWindow
            mPopupWindow.updateProgress();
            latch.countDown();
        });

        // Ожидаем завершения операции в PopupWindow
        awaitLatch();
    }

    // Метод для выполнения операции (копирование или перемещение)
    private void runOperation(Model file, File finalDestPath, Operation operation) {
        switch (operation) {
            case COPY:
                // Копирование файла
                copyFileTo(file.getPath(), finalDestPath);
                break;
            case MOVE:
                // Перемещение файла
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