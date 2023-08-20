package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.mygallery.DataManager;
import com.example.mygallery.adapters.AlbumAdapter;
import com.example.mygallery.DatabaseManager;
import com.example.mygallery.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private DataManager dataManager;
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private ImageButton settingButton;
    private ProgressBar progressBar;
    private int imageWidth;
    private DatabaseManager DBManager;
    private SQLiteDatabase Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        progressBar = findViewById(R.id.recyclerProgress);
        settingButton = findViewById(R.id.buttonContextMenu);

        dataManager = DataManager.getInstance();
        DBManager = new DatabaseManager(this);

        try{
            Db = DBManager.getWritableDatabase();
            createTrashDir();
            setSizeViewImage();
            requestStoragePermission();
        }
        catch (SQLException e){
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

    private void loadFilesOutDataBase(){
        DBManager.getFolder();
        DBManager.close();
        if (dataManager.isDataNamesFolders() && dataManager.isDataCoversFolders() && dataManager.isDataCountFiles()) {
            setConfigRecycleView();
            progressBar.setVisibility(View.GONE);
            createAdapter();
        }
        else{
            scanImages();
        }
    }

    private void addFilesDataBase(){
        DBManager.insertMultipleDataFolders();
        DBManager.insertMultipleDataPath();
        DBManager.close();
    }

    private void scanImages(){
        File directory = new File ("storage/emulated/0/");
        if (directory != null && directory.exists()){
            scanDirectory(directory);
        }
        addFilesDataBase();
        createAdapter();
        setConfigRecycleView();
        progressBar.setVisibility(View.GONE);
    }

    //Создание адаптера
    private  void createAdapter(){
        albumAdapter = new AlbumAdapter(this, imageWidth, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openDirectoryImage(position);
            }
        });
        recyclerView.setAdapter(albumAdapter);
    }

    // Насторйка RecycleView с использование GridLayoutManager
    private void setConfigRecycleView(){
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager= new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(layoutManager);
    }

    // Рекурсивное сканирование и добавление пути к изображению в список
    private void scanDirectory(File directory){
        int colum = 0;
        boolean flag = false;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file: files){
                if(file.isHidden() || file.getName().equals("Android")) {continue;}
                if(file.isDirectory()){
                    scanDirectory(file);
                }else{
                    //Добавлене файлов изображения в список
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")){
                        ++colum;
                        if (!flag){
                            String nameDirectory = directory.getName();
                            dataManager.addDataInNamesFolders(nameDirectory);
                            dataManager.addDataPathsFolders(directory.getPath());
                            dataManager.addDataInCoversFolders(file.getAbsolutePath());
                            flag = true;
                        }
                        dataManager.addDataInPathsFiles(file.getAbsolutePath());
                    }
                }
            }
            if (flag) {
                dataManager.addDataInCountFiles(colum);
            }
        }
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
        imageWidth = (screenWidth - (2*imageMargin)) / 2;
    }

    private void openDirectoryImage(int position) {
        Intent intent = new Intent(this, MainActivity.class);
        DBManager.getFileInFolder(position);
        dataManager.position = position;
        startActivity(intent);
    }

    private void createTrashDir(){
        File binFolder = new File(getFilesDir(), "Корзина");
        if (!binFolder.exists()){
            if (binFolder.mkdir()){
                DBManager.insertFolders("Корзина", 0, binFolder.getPath());
                DBManager.close();
            }
            else {

            }
        }
    }

}