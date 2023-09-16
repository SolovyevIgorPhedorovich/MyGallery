package com.example.mygallery.activities;

import android.view.*;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.mygallery.ScanMemoryRunnable;
import com.example.mygallery.fragment.RecyclerViewFragment;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;
import com.example.mygallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    public boolean isRun = true;
    private boolean isFirstStart = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private DataManager dataManager;
    private FrameLayout fragmentContainer;
    private ProgressBar progressBar;

    private DatabaseManager DBManager;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private RecyclerViewFragment albumsFragment, cartFragment, favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        progressBar = findViewById(R.id.recyclerProgress);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        setSupportActionBar(toolbar);

        dataManager = new DataManager(this);
        DBManager = new DatabaseManager(this);
        mainHandler = new Handler(Looper.getMainLooper());


        try {
            createTrashDir();
            requestStoragePermission();
        } catch (SQLException e) {
            throw e;
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            setListenerBottomNavigationView();
            loadFilesOutDataBase();
            createListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешения получены, запускаем задачи
                loadFilesOutDataBase();
                createListener();
            } else {
                // Разрешения не получены, предпринимаем необходимые действия
            }
        }
    }

    private void loadFilesOutDataBase() {
        DBManager.getFolder();
        DBManager.close();
        if (dataManager.isDataNamesFolders() && dataManager.isDataCoversFolders() && dataManager.isDataCountFiles()) {
            albumsFragment = new RecyclerViewFragment(this, RecyclerViewFragment.ALBUM);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, albumsFragment)
                    .commit();

        } else {
            isFirstStart = true;
            scanImages();
        }
    }

    private void scanImages() {
        //task = new FileScannerAsyncTask(this);
        //task.execute();
    }

    private void showContextMenuAction(View view){

        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.TOP);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu_action_image, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.move_file) {
                    //fileManager.moveFile();

                    return true;
                } else if (menuItem.getItemId() == R.id.copy_file) {
                    //fileManager.copyFile();

                    return true;
                }
                else{
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private void scanFolder(String nameFolder, int position) {
        File folder = new File(DBManager.getFolderPath(nameFolder));
        dataManager.clearData(DataManager.PATH);
        long startTime = System.currentTimeMillis();
        dataManager.setPathsFiles(folder, position);
        long endTime = System.currentTimeMillis();
        Log.d("scanFolder", "Время выполнения функции "+ (endTime - startTime) + "мс");
    }

    public void openDirectoryImage(String nameFolder, int position) {
        Intent intent = new Intent(this, MainActivity.class);
        scanFolder(nameFolder, position);
        intent.putExtra("nameFolder", nameFolder);
        startActivity(intent);
    }

    private void createTrashDir() {
        File binFolder = new File(getFilesDir(), "Корзина");
        if (!binFolder.exists()) {
            if (binFolder.mkdir()) {

            } else {

            }
        }
    }

    private void createListener (){
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateAdapter() {
        isRun = false;
        runOnUiThread(() -> {
            if (!isFirstStart) {
                albumsFragment.updateAdapter();
            } else {
                albumsFragment.updateAdapter();
                progressBar.setVisibility(View.GONE);
                isFirstStart = false;
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isFirstStart) progressBar.setVisibility(View.GONE);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new ScanMemoryRunnable(this));
        executor.shutdown();
    }

    private void setListenerBottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_albums) {
                    if (favoritesFragment != null && cartFragment != null){
                        getSupportFragmentManager().beginTransaction()
                                .show(albumsFragment)
                                .hide(favoritesFragment)
                                .hide(cartFragment)
                                .commit();
                    }
                    else if (favoritesFragment == null) {
                        getSupportFragmentManager().beginTransaction()
                                .show(albumsFragment)
                                .hide(cartFragment)
                                .commit();
                    }
                    else if (cartFragment == null){
                        getSupportFragmentManager().beginTransaction()
                                .show(albumsFragment)
                                .hide(favoritesFragment)
                                .commit();
                    }
                    return true;
                } else if (id == R.id.action_favorites) {
                    if (favoritesFragment == null){
                        favoritesFragment = new RecyclerViewFragment(AlbumActivity.this, RecyclerViewFragment.FAVORITES);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragmentContainer, favoritesFragment)
                                .commit();
                    }
                    if (cartFragment == null){
                        getSupportFragmentManager().beginTransaction()
                                .hide(albumsFragment)
                                .show(favoritesFragment)
                                .commit();
                    }
                    else {
                        getSupportFragmentManager().beginTransaction()
                                .hide(albumsFragment)
                                .show(favoritesFragment)
                                .hide(cartFragment)
                                .commit();
                    }
                    return true;
                } else if (id == R.id.action_cart) {
                    if (cartFragment == null){
                        cartFragment = new RecyclerViewFragment(AlbumActivity.this, RecyclerViewFragment.CART);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragmentContainer, cartFragment)
                                .commit();
                    }
                    if (favoritesFragment == null) {
                        getSupportFragmentManager().beginTransaction()
                                .hide(albumsFragment)
                                .show(cartFragment)
                                .commit();
                    }
                    else{
                        getSupportFragmentManager().beginTransaction()
                                .hide(albumsFragment)
                                .hide(favoritesFragment)
                                .show(cartFragment)
                                .commit();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}


