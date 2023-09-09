package com.example.mygallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.mygallery.managers.DataManager;
import com.example.mygallery.adapters.AlbumAdapter;
import com.example.mygallery.managers.DatabaseManager;
import com.example.mygallery.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

        dataManager = new DataManager(this);
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadFilesOutDataBase();
            createListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешения получены, запускаем задачи
                loadFilesOutDataBase();
                createListener();
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
            public void onItemClick(String nameFolder, int position) {
                openDirectoryImage(nameFolder, position);
            }
        });
        recyclerView.setAdapter(albumAdapter);
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

    // Насторйка RecycleView с использование GridLayoutManager
    private void setConfigRecycleView() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void showContextMenuAction(View view){

        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.TOP);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu_action_image, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.move_file) {
                    //fileManager.moveFile();

                    return true;
                } else if (menuItem.getItemId() == R.id.copy_file) {
                    //fileManager.copyFile();

                    return true;
                }
                else{
                    return false;
                }
            }
        });

        popupMenu.show();
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

    private void scanFolder(String nameFolder, int position) {
        File folder = new File(DBManager.getFolderPath(nameFolder));
        dataManager.clearData(DataManager.PATH);
        long startTime = System.currentTimeMillis();
        dataManager.setPathsFiles(folder, position);
        long endTime = System.currentTimeMillis();
        Log.d("scanFolder", "Время выполнения функции "+ (endTime - startTime) + "мс");
    }

    private void openDirectoryImage(String nameFolder, int position) {
        Intent intent = new Intent(this, MainActivity.class);
        scanFolder(nameFolder, position);
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

    private void createListener (){
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContextMenuAction(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

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

class FileScannerAsyncTask extends AsyncTask<Void, Void, Void>  {
    private DataManager dataManager;
    private DatabaseManager databaseManager;
    private Context context;

    public FileScannerAsyncTask(Context context) {
        dataManager = DataManager.getInstance(context);
        databaseManager = DatabaseManager.getInstance(context);
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        File directory = Environment.getExternalStorageDirectory();
        dataManager.clearData();
        if (directory != null && directory.exists()) {
            scanDirectoriesForFoldersWithImages(directory);
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
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToFirst()){
            Map<String, Integer> folderFileCountMap = new HashMap<>();
            while (cursor.moveToNext()) {
                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int folderIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                String nameFolder = cursor.getString(folderIndex);
                String imagePath = cursor.getString(dataIndex);
                String pathFolder = new File(imagePath).getParent();
                if (!isImageInUnwantedFolder(imagePath)) {
                    if (folderFileCountMap.containsKey(pathFolder)){
                        int currentCount = folderFileCountMap.get(pathFolder);
                        folderFileCountMap.put(pathFolder, currentCount + 1);
                    }
                    else {
                        dataManager.getPathsFolders().add(pathFolder);
                        dataManager.getNamesFolders().add(nameFolder);
                        dataManager.getCoversFolders().add(imagePath);
                        folderFileCountMap.put(pathFolder, 1);
                    }
                }
            }
            for (int count: folderFileCountMap.values()){
                dataManager.getCountFiles().add(count);
            }
        }
        cursor.close();
    }

    private boolean isImageInUnwantedFolder(String imagePath){
        return imagePath.contains("/Android/") || imagePath.contains("/.");
    }

    private void addFilesDataBase() {
        Log.d("addFileDataBase", "Start");
        databaseManager.checkedData();
        databaseManager.insertMultipleDataFolders();
    }
}

