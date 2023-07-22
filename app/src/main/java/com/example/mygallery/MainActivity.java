package com.example.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int REQUEST_DIRECTORY = 456;
    protected RecyclerView recyclerView;
    protected LinearLayout imageContainer;
    protected ProgressBar progressBar;
    private Button selectDirectoryButton;
    protected ImageAdapter imageAdapter;
    private List<String> imagePaths;
    private int imageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.recyclerProgress);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            openDirectoryPicker();
        }
    }

    private void openDirectoryPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(intent, "Select Directory"), REQUEST_DIRECTORY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDirectoryPicker();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String directoryPath = getDirectoryPathFromUri(uri);

            // Вывод пути выбранной директории
            if (directoryPath != null) {
                // Вывод пути директории в логи
                Log.d("MainActivity", "Selected directory: " + directoryPath);
                scanImages(directoryPath);
            }
        }
    }

    private String getDirectoryPathFromUri(Uri uri) {
        if (DocumentsContract.isTreeUri(uri)) {
            String documentId = DocumentsContract.getTreeDocumentId(uri);
            String[] split = documentId.split(":");
            String type = split[0];
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {
                return "/storage/" + split[0] + "/" + split[1];
            }
        } else {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                String path = cursor.getString(columnIndex);
                cursor.close();
                return path;
            }
        }
        return null;
    }

    private void scanImages(String directoryPath){
        progressBar.setVisibility(View.VISIBLE);
        // Определние пути к директории с изображениями
        File directory = new File (directoryPath);

        imagePaths = new ArrayList<>();
        if (directory != null && directory.exists()){
            // Рекурсивное сканирование и добавление пути к изображению в список
            scanDirectory(directory);
        }

        progressBar.setVisibility(View.GONE);

        // Насторйка RecycleView с использование GridLayoutManager
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        imageAdapter = new ImageAdapter(this, imagePaths, imageSize, new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openImageViewer(position);
            }
        });
        recyclerView.setAdapter(imageAdapter);
    }

    private void scanDirectory(File directory){
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file: files){
                if(file.isDirectory()){
                    scanDirectory(file);
                }else{
                    //Добавлене файлов изображения в список
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")){
                        imagePaths.add(file.getAbsolutePath());
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
        int imageMargin = getResources().getDimensionPixelSize(R.dimen.image_layout_margin);

        //Вычисление ширины для отображения изображения в сетке
        imageSize = (screenWidth - (2*imageMargin)) / 4;
    }
    private void openImageViewer(int position) {
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putStringArrayListExtra("imagePaths", (ArrayList<String>) imagePaths);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}