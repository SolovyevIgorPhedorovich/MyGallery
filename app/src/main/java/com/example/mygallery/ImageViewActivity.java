package com.example.mygallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<String> imagePaths;
    private ImageButton imageButton;
    private TextView textView;
    private FrameLayout menu;
    private  boolean isMenuVisible = true;
    private ImageViewPagerAdapter adapter;
    private int initialPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        viewPager = findViewById(R.id.viewPager);
        imageButton = findViewById(R.id.buttonBack);
        textView = findViewById(R.id.itemNameTextView);
        menu = findViewById(R.id.menuViewImage);


        //Получение пути изображения
        Intent intent = getIntent();
        imagePaths = intent.getStringArrayListExtra("imagePaths");

        //Установка адаптера для ViewPager
        adapter = new ImageViewPagerAdapter(this, imagePaths);
        viewPager.setAdapter(adapter);

        //Установка позиции
        initialPosition = intent.getIntExtra("position", 0);
        viewPager.setCurrentItem(initialPosition, false);

        setNameItem(imagePaths.get(initialPosition));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position){
                setNameItem(imagePaths.get(position));
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void toggleMenu(){
        Animation animation;
        if (isMenuVisible){
            //Скрыть меню
            animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            menu.setVisibility(View.GONE);
        }
        else {
            animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            menu.setVisibility(View.VISIBLE);
        }
        menu.startAnimation(animation);

        isMenuVisible = !isMenuVisible;
    }

    private void setNameItem (String path){
        File file = new File(path);
        textView.setText(file.getName());
    }



}