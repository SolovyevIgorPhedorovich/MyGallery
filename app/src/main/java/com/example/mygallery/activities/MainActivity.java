package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.example.mygallery.DataManager;
import com.example.mygallery.adapters.ImageAdapter;
import com.example.mygallery.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView recyclerView;
    private TextView directoryName;
    protected ImageAdapter imageAdapter;
    private int imageSize;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        directoryName = findViewById(R.id.directoryNameTextView);
        dataManager = DataManager.getInstance();

        setSizeImage();
        viewDirectoryImage();
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void viewDirectoryImage(){
        directoryName.setText(dataManager.getNamesFolders().get(dataManager.position));

        // Насторйка RecycleView с использование GridLayoutManager
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        imageAdapter = new ImageAdapter(this, imageSize, new ImageAdapter.OnItemClickListener() {
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
        intent.putExtra("position", position);
        startActivity(intent);
    }
}