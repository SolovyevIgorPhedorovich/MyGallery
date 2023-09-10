package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mygallery.fragment.ActionFileFragment;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.adapters.ImageViewPagerAdapter;
import com.example.mygallery.R;
import com.example.mygallery.ZoomOutPageTransformer;
import com.example.mygallery.managers.DatabaseManager;
import com.example.mygallery.managers.FileManager;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity implements ActionFileFragment.FolderSelectionListener {
    private static final int MOVE = 0;
    private static final int COPY = 1;
    private ViewPager2 viewPager;
    private DataManager dataManager;
    private FileManager fileManager;
    private ImageButton imageButtonBack,
                        buttonRemoveFile,
                        buttonContextMenu;
    private Button buttonMoveFile,
            buttonCopyFile,
            buttonStartSlideShow,
            buttonPrint,
            buttonRenameTo,
            buttonSetAs,
            buttonRotation,
            buttonChooseArtworkAlbum;
    private TextView textView;
    private int idFolder = -1;
    private  boolean isMenuVisible = true;
    private ImageViewPagerAdapter adapter;
    private FrameLayout menu, toolbar;
    private int initialPosition;
    private Intent intent;
    private Context context;
    private PopupWindow popupWindow;
    private int typeAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_image_view);

        viewPager = findViewById(R.id.viewPager);
        imageButtonBack = findViewById(R.id.buttonBack);
        buttonRemoveFile = findViewById(R.id.buttonRemove);
        buttonContextMenu = findViewById(R.id.buttonContextMenu);
        textView = findViewById(R.id.itemNameTextView);
        menu = findViewById(R.id.menuViewImage);
        toolbar = findViewById(R.id.toolBar);
        context = this;

        dataManager = DataManager.getInstance(this);
        fileManager = new FileManager(this);
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
        setNameItem(dataManager.getPathsFiles().get(initialPosition).getName());
    }

    public int getPositionAlbum(){
        DatabaseManager DBManger = DatabaseManager.getInstance(this);
        if (idFolder == -1){
            idFolder = (int) DBManger.getIdFolder(intent.getStringExtra("nameFolder")) - 2;
        }
        return idFolder;
    }

    //Обработка нажатий
    private void setOnClickListenerButtons() {
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        buttonRemoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyItemRemoved(initialPosition);
                fileManager.deleteFile(dataManager.getPathsFiles().get(initialPosition));
            }
        });

        buttonContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContextMenuAction(view);
            }
        });
    }

    private void setOnClickListenerContextMenu(){
        buttonCopyFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeAction = 1;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ActionFileFragment actionFileFragment = new ActionFileFragment(context, true);
                fragmentTransaction.replace(R.id.activity_image_view, actionFileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                popupWindow.dismiss();
            }
        });

        buttonMoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeAction = 0;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ActionFileFragment actionFileFragment = new ActionFileFragment(context, false);
                fragmentTransaction.replace(R.id.activity_image_view, actionFileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                popupWindow.dismiss();
            }
        });

        buttonStartSlideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonRenameTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonSetAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonChooseArtworkAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void showContextMenuAction(View view){

        popupWindow = new PopupWindow(this);

        //Загрузка XML-макета
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.context_menu_action_file, null);

        //Установка содержимого
        popupWindow.setContentView(menuView);

        //Установка размеров меню
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = menuView.findViewById(R.id.contextMenu).getHeight();
        popupWindow.setAnimationStyle(R.anim.popup_scale_in);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]-x);
        createBackgroundView();

        buttonMoveFile = menuView.findViewById(R.id.moveFile);
        buttonCopyFile = menuView.findViewById(R.id.copyFile);
        buttonStartSlideShow =  menuView.findViewById(R.id.slide_show);
        buttonPrint =  menuView.findViewById(R.id.print);
        buttonRenameTo =  menuView.findViewById(R.id.rename_file);
        buttonSetAs =  menuView.findViewById(R.id.set);
        buttonRotation =  menuView.findViewById(R.id.rotation);
        buttonChooseArtworkAlbum =  menuView.findViewById(R.id.choose);
        buttonStartSlideShow =  menuView.findViewById(R.id.slide_show);
        buttonPrint =  menuView.findViewById(R.id.print);
        buttonRenameTo =  menuView.findViewById(R.id.rename_file);
        buttonSetAs =  menuView.findViewById(R.id.set);
        buttonRotation =  menuView.findViewById(R.id.rotation);
        buttonChooseArtworkAlbum = menuView.findViewById(R.id.choose);

        setOnClickListenerContextMenu();
    }

    //Создание фонового view
    private void createBackgroundView(){
        final View backgroundView = new View(this);
        backgroundView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        backgroundView.setBackgroundColor(Color.parseColor("#00000000"));
        ((ViewGroup) getWindow().getDecorView()).addView(backgroundView);

        backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((ViewGroup) getWindow().getDecorView()).removeView(backgroundView);
            }
        });
    }

    //Обработка перелистывания pageViewer2
    private void registerOnPage(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position){
               setNameItem(dataManager.getPathsFiles().get(position).getName());
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
    public int getStatusBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return  result;
    }

    @Override
    public void onFolderSelected(File folderPath){
        if (typeAction == MOVE){
            adapter.notifyItemRemoved(initialPosition);
            fileManager.moveFile(dataManager.getPathsFiles().get(initialPosition), folderPath);
        } else if (typeAction == COPY) {
            fileManager.copyFile(dataManager.getPathsFiles().get(initialPosition), folderPath);
        }
    }
}

