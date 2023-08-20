package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mygallery.DataManager;
import com.example.mygallery.adapters.ImageViewPagerAdapter;
import com.example.mygallery.R;
import com.example.mygallery.ZoomOutPageTransformer;

import java.io.File;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DataManager dataManager;
    private ImageButton imageButtonBack;
    private TextView textView;
    private  boolean isMenuVisible = true;
    private ImageViewPagerAdapter adapter;
    private FrameLayout menu, toolbar;
    private int initialPosition;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_image_view);

        viewPager = findViewById(R.id.viewPager);
        imageButtonBack = findViewById(R.id.buttonBack);
        textView = findViewById(R.id.itemNameTextView);
        menu = findViewById(R.id.menuViewImage);
        toolbar = findViewById(R.id.toolBar);

        dataManager = DataManager.getInstance();
        int statusBarHeight = getStatusBarHeight();
        menu.setPadding(0, statusBarHeight, 0, 0);

        setAdapter(statusBarHeight);
        setPosition();
        registerOnPage();
        setOnClickListenerButtons();
    }

    //Установка адаптера для ViewPager
    private void setAdapter(int statusBarHeight){
        adapter = new ImageViewPagerAdapter(this, statusBarHeight);
        viewPager.setAdapter(adapter);
    }

    //Установка позиции
    private void setPosition(){
        intent = getIntent();
        initialPosition = intent.getIntExtra("position", 0);
        viewPager.setCurrentItem(initialPosition, false);
        setNameItem(dataManager.getPathsFiles().get(initialPosition));
    }

    //Обработка нажатий
    private void setOnClickListenerButtons(){
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //Обработка перелистывания pageViewer2
    private void registerOnPage(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position){
                setNameItem(dataManager.getPathsFiles().get(position));
                if (initialPosition != position) {
                    adapter.notifyItemChanged(initialPosition);
                    initialPosition = position;
                }
            }
        });
    }
    //Функция сворачивает/разворачивает интерфейс программы и ситемы
    public void toggleMenu(){
        Animation animationTotMenu, animationBottomMenu;
        View decorView = getWindow().getDecorView();
        if (isMenuVisible){
            animationTotMenu = AnimationUtils.loadAnimation(this, R.anim.slide_up_top);
            animationBottomMenu = AnimationUtils.loadAnimation(this, R.anim.slide_down_bottom);
            menu.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
        else {
            animationTotMenu = AnimationUtils.loadAnimation(this, R.anim.slide_down_top);
            animationBottomMenu = AnimationUtils.loadAnimation(this, R.anim.slide_up_bottom);
            menu.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        menu.startAnimation(animationTotMenu);
        toolbar.startAnimation(animationBottomMenu);
        viewPager.requestLayout();
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        isMenuVisible = !isMenuVisible;
    }

    //Функция возвращаеть именя файла
    private void setNameItem (String path){
        File file = new File(path);
        textView.setText(file.getName());
    }
    //Функция возвращает высоту статус бара системы
    private int getStatusBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return  result;
    }
}

