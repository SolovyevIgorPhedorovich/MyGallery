package com.example.mygallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.mygallery.managers.DataManager;
import com.example.mygallery.adapters.AlbumAdapter;
import com.example.mygallery.managers.DatabaseManager;
import com.example.mygallery.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private FileScannerAsyncTask task;
    public boolean isScanningPath = false;
    public boolean isRun = true;
    private boolean isFirstStart = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private DataManager dataManager;
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private ImageButton settingButton;
    private ProgressBar progressBar;
    private int imageWidth;
    private DatabaseManager DBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        progressBar = findViewById(R.id.recyclerProgress);
        settingButton = findViewById(R.id.buttonContextMenu);

        dataManager = new DataManager();
        DBManager = new DatabaseManager(this);
        mainHandler = new Handler(Looper.getMainLooper());

        try {
            createTrashDir();
            setSizeViewImage();
            requestStoragePermission();
        } catch (SQLException e) {
            throw e;
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        } else {
            loadFilesOutDataBase();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешения получены, запускаем задачи
                loadFilesOutDataBase();
            } else {
                // Разрешения не получены, предпринимаем необходимые действия
            }
        }
    }

    private void loadFilesOutDataBase() {
        DBManager.getFolder();
        DBManager.close();
        if (dataManager.isDataNamesFolders() && dataManager.isDataCoversFolders() && dataManager.isDataCountFiles()) {
            setConfigRecycleView();
            createAdapter();

        } else {
            isFirstStart = true;
            scanImages();
        }
    }

    private void scanImages() {
        task = new FileScannerAsyncTask(this);
        task.execute();
    }

    //Создание адаптера
    private void createAdapter() {
        albumAdapter = new AlbumAdapter(this, imageWidth, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String nameFolder) {
                openDirectoryImage(nameFolder);
            }
        });
        recyclerView.setAdapter(albumAdapter);
    }

    // Насторйка RecycleView с использование GridLayoutManager
    private void setConfigRecycleView() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setSizeViewImage() {
        //Получение ширины экрна
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        //Получение ширины отступа
        int imageMargin = getResources().getDimensionPixelSize(R.dimen.image_preview_margin_horizontal);

        //Вычисление ширины для отображения изображения в альбома
        imageWidth = (screenWidth - (2 * imageMargin)) / 2;
    }

    private void scanFolder(String nameFolder) {
        File folder = new File(DBManager.getFolderPath(nameFolder));
        dataManager.setPathsFiles(folder);
    }

    private void openDirectoryImage(String nameFolder) {
        Intent intent = new Intent(this, MainActivity.class);
        scanFolder(nameFolder);
        intent.putExtra("nameFolder", nameFolder);
        startActivity(intent);
    }

    private void createTrashDir() {
        File binFolder = new File(getFilesDir(), "Корзина");
        if (!binFolder.exists()) {
            if (binFolder.mkdir()) {
                DBManager.insertFolders("Корзина", 0, binFolder.getPath());
                DBManager.close();
            } else {

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (isRun && !isFirstStart) {
                    progressBar.setVisibility(View.GONE);
                    task = new FileScannerAsyncTask(AlbumActivity.this);
                    task.execute();
                }
            }
        });
    }

    protected void updateAdapter() {

        if (!isFirstStart) {
            albumAdapter.updateDataAdapter();
            albumAdapter.notifyDataSetChanged();
        } else {
            createAdapter();
            setConfigRecycleView();
            progressBar.setVisibility(View.GONE);
            isFirstStart = false;
        }
    }

}

class FileScannerAsyncTask extends AsyncTask<Void, Void, Void> {
    private DataManager dataManager;
    private DatabaseManager databaseManager;
    private Context context;

    public FileScannerAsyncTask(Context context) {
        dataManager = DataManager.getInstance();
        databaseManager = DatabaseManager.getInstance(context);
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        File directory = new File("storage/emulated/0/");
        dataManager.clearData();
        if (directory != null && directory.exists()) {
            scanDirectoriesForFoldersWithImages(directory);
            processFoldersWithImages();
            addFilesDataBase();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        AlbumActivity albumActivity = (AlbumActivity) context;
        if (!albumActivity.isScanningPath) {
            databaseManager.close();
        }
        albumActivity.updateAdapter();
        albumActivity.isRun = false;
    }

    //Cканирование имен и пути папок
    private void scanDirectoriesForFoldersWithImages(File directory) {
        IOFileFilter fileFilter = FileFilterUtils.or(
                FileFilterUtils.suffixFileFilter(".jpg"),
                FileFilterUtils.suffixFileFilter(".png"),
                FileFilterUtils.suffixFileFilter(".jpeg")
        );

        // Сканирование всех папок в каталоге, содержащие подходящие изображения
        File[] subdirectories = directory.listFiles((FilenameFilter) FileFilterUtils.directoryFileFilter());
        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                if (subdirectory.isHidden() || subdirectory.getName().equals("Android")) {
                    continue; // Пропустить скрытые и системные папки
                }
                // Если папка содержит подходящие изображения, сохраните её имя и путь
                if (FileUtils.listFiles(subdirectory, fileFilter, null).size() > 0) {
                    String nameDirectory = subdirectory.getName();
                    dataManager.addDataInNamesFolders(nameDirectory);
                    dataManager.addDataPathsFolders(subdirectory.getPath());
                }
                scanDirectoriesForFoldersWithImages(subdirectory);
            }
        }
    }

    //Подсчет количества файлов и выбор обложки для каждой папки
    private void processFoldersWithImages() {
        for (String path : dataManager.getPathsFolders()) {
            File directory = new File(path);
            IOFileFilter fileFilter = FileFilterUtils.or(
                    FileFilterUtils.suffixFileFilter(".jpg"),
                    FileFilterUtils.suffixFileFilter(".png"),
                    FileFilterUtils.suffixFileFilter(".jpeg")
            );

            // Сканирование все подходящие файлы в папке
            List<File> imageFiles = (List<File>) FileUtils.listFiles(directory, fileFilter, null);
            int count = imageFiles.size();

            if (count > 0) {
                dataManager.addDataInCountFiles(count);

                if (imageFiles.get(0) != null) {
                    dataManager.addDataInCoversFolders(imageFiles.get(0).getAbsolutePath());
                }
            }
        }
    }

    private void addFilesDataBase() {
        Log.d("addFileDataBase", "Start");
        databaseManager.checkedData();
        databaseManager.insertMultipleDataFolders();
    }
}

