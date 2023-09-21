package com.example.mygallery.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.mygallery.R;
import com.example.mygallery.ScanMemoryRunnable;
import com.example.mygallery.fragment.RecyclerViewFragment;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.popupWindow.PopupWindowContextMenuAlbum;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    SharedPreferences displayPreferences;
    private DataManager dataManager;
    // Флаг для определения первого запуска
    private boolean isFirstStart = false;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DatabaseManager databaseManager;
    private RecyclerViewFragment albumsFragment, cartFragment, favoritesFragment;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Инициализация элементов интерфейса
        initializeViews();
        initializedPreferences();

        // Инициализация менеджеров
        dataManager = new DataManager(this);
        databaseManager = new DatabaseManager(this);

        createTrashDir();
        requestStoragePermission();
    }

    // Метод для инициализации элементов интерфейса
    private void initializeViews() {
        progressBar = findViewById(R.id.recyclerProgress);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textView = findViewById(R.id.fragment_text);
        bottomNavigationView.setSelectedItemId(R.id.action_albums);

        setSupportActionBar(toolbar);
    }

    // Метод для запроса разрешений на доступ к хранилищу
    private void requestStoragePermission() {
        if (hasStoragePermissions()) {
            setListenerBottomNavigationView();
            loadFilesFromDatabase();
            createListener();
        } else {
            requestStoragePermissions();
        }
    }

    // Метод для проверки наличия разрешений
    private boolean hasStoragePermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Метод для запроса разрешений
    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // Обработка результатов запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешения получены, запускаем задачи
                loadFilesFromDatabase();
                createListener();
            } else {
                // Разрешения не получены, предпринимаем необходимые действия
            }
        }
    }

    // Загрузка файлов из базы данных
    private void loadFilesFromDatabase() {
        databaseManager.getFolder();
        databaseManager.close();
        if (dataManager.isDataFolderNamesList() && dataManager.isDataFolderCoversList() && dataManager.isDataFileCountList()) {
            createStartFragment();
        } else {
            isFirstStart = true;
            scanImages();
        }
    }

    private void createStartFragment() {
        albumsFragment = new RecyclerViewFragment(this, RecyclerViewFragment.ALBUM);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, albumsFragment)
                .commit();
    }

    // Метод для сканирования изображений
    private void scanImages() {
        // Запустить сканирование изображений
    }

    // Отображение контекстного меню действий
    private void showContextMenuAction() {
        PopupWindowManager mPopupWindow = new PopupWindowContextMenuAlbum(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.popup_window_album_context_menu, null);

        mPopupWindow.setContent(menuView);
        mPopupWindow.setPosition(40, 80);
        mPopupWindow.showPopupWindow(findViewById(R.id.action_open_context_menu), menuView, Gravity.TOP | Gravity.RIGHT);

    }

    // Сканирование папки
    private void scanFolder(String nameFolder, int position) {
        File folder = new File(databaseManager.getFolderPath(nameFolder));
        dataManager.clearData(DataManager.PATH);
        long startTime = System.currentTimeMillis();
        dataManager.setImageFilesList(folder, position);
        long endTime = System.currentTimeMillis();
        Log.d("scanFolder", "Время выполнения функции "+ (endTime - startTime) + "мс");
    }

    // Открытие директории изображений
    public void openDirectoryImage(String nameFolder, int position) {
        Intent intent = new Intent(this, AlbumGridActivity.class);
        scanFolder(nameFolder, position);
        intent.putExtra("nameFolder", nameFolder);
        startActivity(intent);
    }

    // Создание корзины
    private void createTrashDir() {
        File binFolder = new File(getFilesDir(), "Корзина");
        if (!binFolder.exists()) {
            binFolder.mkdir();
        }
    }

    // Создание слушателя (listener)
    private void createListener (){
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Обновление адаптера
    public void updateAdapter() {
        runOnUiThread(() -> {
            if (!isFirstStart) {
                albumsFragment.updateAdapter();
            } else {
                createStartFragment();
                progressBar.setVisibility(View.GONE);
                isFirstStart = false;
            }
        });
    }

    // Действия после возобновления активности
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isFirstStart) progressBar.setVisibility(View.GONE);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ScanMemoryRunnable(this));
        executor.shutdown();
    }

    // Установка слушателя для нижней навигационной панели
    private void setListenerBottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_albums) {
                // Отобразить фрагмент с альбомами
                showAlbumsFragment();
                return true;
            } else if (id == R.id.action_favorites) {
                // Отобразить фрагмент с избранными
                showFavoritesFragment();
                return true;
            } else if (id == R.id.action_cart) {
                // Отобразить фрагмент с корзиной
                showCartFragment();
                return true;
            }
            return false;
        });
    }

    // Отображение фрагмента с альбомами
    private void showAlbumsFragment() {
        if (albumsFragment != null) {
            if (albumsFragment.isHidden())
                albumsFragment.updateFragment();
            if (favoritesFragment != null && cartFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(albumsFragment)
                        .hide(favoritesFragment)
                        .hide(cartFragment)
                        .commit();
            } else if (favoritesFragment == null && cartFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(albumsFragment)
                        .hide(cartFragment)
                        .commit();
            } else if (cartFragment == null && favoritesFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(albumsFragment)
                        .hide(favoritesFragment)
                        .commit();
            }
        }
    }

    // Отображение фрагмента с избранными
    private void showFavoritesFragment() {
        if (favoritesFragment == null) {
            favoritesFragment = new RecyclerViewFragment(AlbumActivity.this, RecyclerViewFragment.FAVORITES);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, favoritesFragment)
                    .commit();
        }
        if (favoritesFragment.isHidden())
            favoritesFragment.updateFragment();
        if (cartFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(albumsFragment)
                    .show(favoritesFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(albumsFragment)
                    .show(favoritesFragment)
                    .hide(cartFragment)
                    .commit();
        }
    }

    // Отображение фрагмента с корзиной
    private void showCartFragment() {
        if (cartFragment == null) {
            cartFragment = new RecyclerViewFragment(AlbumActivity.this, RecyclerViewFragment.CART);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, cartFragment)
                    .commit();
        }
        if (cartFragment.isHidden())
            cartFragment.updateFragment();
        if (favoritesFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(albumsFragment)
                    .show(cartFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(albumsFragment)
                    .hide(favoritesFragment)
                    .show(cartFragment)
                    .commit();
        }
    }

    public void hideTextInFragment() {
        textView.setVisibility(View.GONE);
    }

    public void showTextInFragment() {
        textView.setVisibility(View.VISIBLE);
    }

    public void setTextFragment(int text) {
        textView.setText(text);
    }

    // Создание пунктов меню в панели инструментов
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (displayPreferences.getString("display_type", null).contains("grid"))
            getMenuInflater().inflate(R.menu.menu_toolbar_grid, menu);
        else if (displayPreferences.getString("display_type", null).contains("list"))
            getMenuInflater().inflate(R.menu.menu_toolbar_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_create_album) {
        } else if (itemId == R.id.action_view_type) {
            replaceViewType();
        } else if (itemId == R.id.action_open_context_menu) {
            showContextMenuAction();
        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceViewType() {
        if (displayPreferences.getString("display_type", null).contains("grid"))
            PreferencesReplaceString("display_type", "list");
        else
            PreferencesReplaceString("display_type", "grid");
        invalidateOptionsMenu();

        albumsFragment.updateTypeDisplay();
    }

    private void PreferencesReplaceString(String key, String value) {
        SharedPreferences.Editor editor = displayPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    //Создаем preferences если их нет
    private void initializedPreferences() {
        displayPreferences = getSharedPreferences("display_album_preferences", Context.MODE_PRIVATE);
        //
        if (!displayPreferences.contains("display_type")) {
            SharedPreferences.Editor editor = displayPreferences.edit();
            editor.putString("display_type", "grid");
            editor.putString("sort_albums", "name");
            editor.apply();
        }


    }

}
