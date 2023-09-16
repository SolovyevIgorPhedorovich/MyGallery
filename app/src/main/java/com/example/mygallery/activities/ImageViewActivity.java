package com.example.mygallery.activities;

import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.mygallery.fragment.ActionFileFragment;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.adapters.ImageViewPagerAdapter;
import com.example.mygallery.R;
import com.example.mygallery.ZoomOutPageTransformer;
import com.example.mygallery.managers.DatabaseManager;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.managers.PopupWindowManager;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity implements ActionFileFragment.FolderSelectionListener {
    private static final int MOVE = 0;
    private static final int COPY = 1;
    private ViewPager2 viewPager;
    private DataManager dataManager;
    private FileManager fileManager;
    private ImageButton imageButtonBack,
                        buttonRemoveFile,
                        buttonContextMenu,
                        buttonAddFavorites;
    private TextView textView;
    private int idFolder = -1;
    private  boolean isMenuVisible = true;
    private ImageViewPagerAdapter adapter;
    private FrameLayout menu, toolbar;
    private int initialPosition;
    private Intent intent;
    private Context context;
    private int typeAction;
    private PopupWindowManager mPopupWindow;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_image_view);

        viewPager = findViewById(R.id.viewPager);
        imageButtonBack = findViewById(R.id.buttonBack);
        buttonRemoveFile = findViewById(R.id.buttonRemove);
        buttonContextMenu = findViewById(R.id.buttonContextMenu);
        buttonAddFavorites = findViewById(R.id.buttonAddFavorites);
        textView = findViewById(R.id.itemNameTextView);
        menu = findViewById(R.id.menuViewImage);
        toolbar = findViewById(R.id.toolBar);
        context = this;

        viewPager.requestLayout();
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

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

    public int getPosition(){
        DatabaseManager DBManger = DatabaseManager.getInstance(this);
        if (idFolder == -1){
            idFolder = (int) DBManger.getIdFolder(dataManager.getPathsFiles().get(initialPosition).getParent()) - 1;
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

        buttonAddFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = dataManager.getPathsFiles().get(initialPosition).getAbsolutePath();
                DatabaseManager db = new DatabaseManager(context);
                db.addFavorites(path);
            }
        });

        buttonContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContextMenuAction(view);
            }
        });
    }

    public void moveFile(){
        typeAction = 0;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ActionFileFragment actionFileFragment = new ActionFileFragment(context, true);
        fragmentTransaction.replace(R.id.activity_image_view, actionFileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void copyFile(){
        typeAction = 1;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ActionFileFragment actionFileFragment = new ActionFileFragment(context, false);
        fragmentTransaction.replace(R.id.activity_image_view, actionFileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void creatView(){
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) getWindow().getDecorView()).addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable);
                ((ViewGroup) getWindow().getDecorView()).removeView(view);
                toggleMenu();
            }
        });
    }

    public void startSlaidShow(){
        creatView();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int position = (initialPosition < adapter.getItemCount() - 1)? initialPosition + 1 : 0;
                viewPager.setCurrentItem(position);
                initialPosition = viewPager.getCurrentItem();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
        toggleMenu();
    }

    public void print(){

    }

    public void rename(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.input_line_new_name, null);
        EditText inputText = menuView.findViewById(R.id.newName);
        inputText.setText(dataManager.getPathsFiles().get(initialPosition).getName());

        //Установка содержимого
        mPopupWindow.setContentViewPopupWindow(menuView);
        mPopupWindow.setPosition(0,50);
        mPopupWindow.showPopupWindow(view, menuView);

    }

    public void rename(String newName){
        if (!newName.isEmpty()){

            File pathFile = dataManager.getPathsFiles().get(initialPosition);
            File newNamePathFile = new File(pathFile.getParent() + "/" + newName);

            if (newNamePathFile.compareTo(pathFile) != 0){
                fileManager.renameFile(pathFile, newNamePathFile);
            }
        }
    }

    public void setAs(){

    }

    public void rotation(){
        adapter.rotation(initialPosition);
    }

    public void choose(){
        String pathArtwork = dataManager.getPathsFiles().get(initialPosition).getAbsolutePath();
        DatabaseManager DBManager = new DatabaseManager(context);
        int id = DBManager.updatePath(String.valueOf(textView.getText()), pathArtwork);
        DBManager.close();
        dataManager.getCoversFolders().set(id-1, pathArtwork);
    }

    private void showContextMenuAction(View view){

        mPopupWindow = new PopupWindowManager(this);

        //Загрузка XML-макета
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.context_menu_action_file, null);

        mPopupWindow.setContentViewPopupWindow(menuView);
        mPopupWindow.setPosition(toolbar.getWidth()/8, toolbar.getHeight()+30);
        mPopupWindow.showPopupWindow(view, menuView);
    }

    //Обработка перелистывания pageViewer2
    private void registerOnPage(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position){
               setNameItem(dataManager.getPathsFiles().get(position).getName());
                if (initialPosition != position) {
                    adapter.notifyItemChanged(initialPosition); // вынести в отдельный поток с задржкой
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

