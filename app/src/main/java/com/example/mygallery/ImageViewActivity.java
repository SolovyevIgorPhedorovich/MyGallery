package com.example.mygallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<String> imagePaths;
    private int initialPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        viewPager = findViewById(R.id.viewPager);

        //Получение пути изображения
        Intent intent = getIntent();
        imagePaths = intent.getStringArrayListExtra("imagePaths");

        //Установка адаптера для ViewPager
        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, imagePaths);
        viewPager.setAdapter(adapter);

        //Установка позиции
        initialPosition = intent.getIntExtra("position", 0);
        viewPager.setCurrentItem(initialPosition, false);
    }
}