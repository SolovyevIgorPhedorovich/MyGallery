package com.example.mygallery.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewPager.ConfigurationViewPager;
import com.example.mygallery.viewmodel.BaseViewModel;
import dev.chrisbanes.insetter.Insetter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public abstract class ViewActivity extends AppCompatActivity {
    protected BaseViewModel<Model> viewModel;
    protected int initialPosition;
    protected ConfigurationViewPager configurationViewPager;
    private ViewPager2 viewPager;
    private ImageButton imageButtonBack;
    private TextView textView;
    private View menu, toolbar;
    private boolean mVisible = true;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onCreate() {
        intent = getIntent();
        initializeViews();
        initializeViewModel();

        Insetter.builder().padding(WindowInsetsCompat.Type.statusBars()).applyToView(menu);
        Insetter.builder().padding(WindowInsetsCompat.Type.navigationBars()).applyToView(toolbar);
        configurationViewPager = new ConfigurationViewPager(viewPager, getPosition(), this::setNameItem);

        setAdapter();
        setObserve();

        setOnClickListenerButtons();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 30)
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        hideUiInterface();

        if (Build.VERSION.SDK_INT >= 30) {
            viewPager.getWindowInsetsController().hide(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            viewPager.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        showUiInterface();
    }

    protected void initializeViews() {
        // Инициализация всех View-компонентов
        viewPager = findViewById(R.id.view_pager);
        imageButtonBack = findViewById(R.id.button_back);
        textView = findViewById(R.id.item_name_text_view);
        menu = findViewById(R.id.menu_view_image);
        toolbar = findViewById(R.id.toolbar);
    }

    protected abstract void initializeViewModel();

    //Установка позиции
    private int getPosition() {
        return intent.getIntExtra("position", 0);
    }

    //Обработка нажатий
    protected void setOnClickListenerButtons() {
        imageButtonBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    //Показать интерфейс
    private void showUiInterface() {
        Animation animationTotMenu = AnimationUtils.loadAnimation(this, R.anim.slide_down_top);
        Animation animationBottomMenu = AnimationUtils.loadAnimation(this, R.anim.slide_up_bottom);

        menu.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        beginAnimationUiInterface(animationTotMenu, animationBottomMenu);
    }

    //Скрыть интерфейс
    private void hideUiInterface() {
        Animation animationTotMenu = AnimationUtils.loadAnimation(this, R.anim.slide_up_top);
        Animation animationBottomMenu = AnimationUtils.loadAnimation(this, R.anim.slide_down_bottom);

        menu.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        beginAnimationUiInterface(animationTotMenu, animationBottomMenu);
    }

    //Запуск анимации изменения интерфейса
    private void beginAnimationUiInterface(Animation animationTotMenu, Animation animationBottomMenu) {
        menu.startAnimation(animationTotMenu);
        toolbar.startAnimation(animationBottomMenu);
    }

    //Функция возвращает имена файла
    private void setNameItem(int position) {
        textView.setText(viewModel.getName(position));
        initialPosition = position;
    }

    private void setAdapter() {
        configurationViewPager.setAdapter(this, viewModel.getPathList(), this::toggle);
    }

    private void setObserve() {
        viewModel.data.observe(this, o -> configurationViewPager.updateAdapter(viewModel.getPathList()));
    }
}