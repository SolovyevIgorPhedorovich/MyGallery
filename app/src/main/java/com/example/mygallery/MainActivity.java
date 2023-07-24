package com.example.mygallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView recyclerView;
    private TextView directoryName;
    protected ImageAdapter imageAdapter;
    private List<String> imagePaths;
    private int imageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        directoryName = findViewById(R.id.directoryNameTextView);
        setSizeImage();
        viewDirectoryImage();

    }

    private void viewDirectoryImage(){

        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        directoryName.setText(imagePaths.get(0));
        imagePaths.remove(0);

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