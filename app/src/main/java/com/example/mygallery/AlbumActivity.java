package com.example.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private List<List<String>> imageDirectoryPaths = new ArrayList<List<String>>();
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private Button selectDirectoryButton;
    private int imageWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        recyclerView = findViewById(R.id.recyclerViewAlbum);
        selectDirectoryButton = findViewById(R.id.selectDirectoryButton);

        setSizeImage();
        selectDirectoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            scanImages();
        }
    }

    private void scanImages(){
        // Определние пути к директории с изображениями
        File directory = new File ("storage/emulated/0/");

        if (directory != null && directory.exists()){
            // Рекурсивное сканирование и добавление пути к изображению в список
            scanDirectory(directory);
        }

        List<String> nameDirect = new ArrayList<String>();
        List<String> imagePreviewPaths = new ArrayList<String>();
        List<Integer> countItem = new ArrayList<Integer>();
        for (List<String> row : imageDirectoryPaths){
            nameDirect.add(row.get(0));
            imagePreviewPaths.add(row.get(1));
            countItem.add(row.size() - 1);
        }

        // Насторйка RecycleView с использование GridLayoutManager
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        albumAdapter = new AlbumAdapter(this, nameDirect, imagePreviewPaths, countItem, imageWidth, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openDirectoryImage(position);
            }
        });
        recyclerView.setAdapter(albumAdapter);
    }

    private void scanDirectory(File directory){
        boolean flag = false;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file: files){
                if(!file.isHidden()) {continue;}
                if(file.isDirectory()){
                    scanDirectory(file);
                }else{
                    //Добавлене файлов изображения в список
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")){
                        if (!flag){
                            String nameDirectory = directory.getName().split("/")[0];
                            imageDirectoryPaths.add(new ArrayList<>());
                            imageDirectoryPaths.get(imageDirectoryPaths.size()-1).add(nameDirectory);
                            flag = true;
                        }
                        imageDirectoryPaths.get(imageDirectoryPaths.size()-1).add(file.getAbsolutePath());
                    }
                }
            }
        }
    }
    private void setSizeImage() {
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
        intent.putStringArrayListExtra("imagePaths", (ArrayList<String>) imageDirectoryPaths.get(position));
        startActivity(intent);
    }


}