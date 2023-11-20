package com.example.mygallery.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.mygallery.R;
import com.example.mygallery.databinding.ActivityAlbumBinding;
import com.example.mygallery.fragments.AlbumRecyclerViewFragment;
import com.example.mygallery.fragments.CartRecyclerViewFragment;
import com.example.mygallery.fragments.FavoritesRecyclerViewFragment;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.managers.CreateAlbumManager;
import com.example.mygallery.navigator.FragmentManagerHelper;
import com.example.mygallery.popupWindow.PopupWindowAlbumContextMenu;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import com.example.mygallery.sharedpreferences.values.AlbumPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AlbumActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final int PERMISSION_REQUEST_CODE = 123;

    // Флаг для определения первого запуска
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    FragmentManagerHelper fragmentManagerHelper;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ActivityAlbumBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManagerHelper = new FragmentManagerHelper(getSupportFragmentManager(), binding.fragmentContainer.getId());
        sharedPreferencesHelper = new SharedPreferencesHelper(this, SharedPreferencesHelper.ALBUM_PREFERENCES);

        // Инициализация элементов интерфейса
        initializeViews();
        setListenerBottomNavigationView();

        requestStoragePermission();
    }

    // Метод для инициализации элементов интерфейса
    private void initializeViews() {
        progressBar = binding.recyclerProgress;
        Toolbar toolbar = binding.toolbar;
        bottomNavigationView = binding.bottomNavigationView;

        bottomNavigationView.setSelectedItemId(R.id.action_albums);

        setSupportActionBar(toolbar);
    }

    // Метод для запроса разрешений на доступ к хранилищу
    private void requestStoragePermission() {
        if (hasStoragePermissions()) {
            fragmentManagerHelper.switchToFragment(AlbumRecyclerViewFragment.class);
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
                requestStoragePermission();
            }
        }
    }

    // Действия после возобновления активности
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Установка слушателя для нижней навигационной панели
    private void setListenerBottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_albums) {
                // Отобразить фрагмент с альбомами
                fragmentManagerHelper.switchToFragment(AlbumRecyclerViewFragment.class);
                return true;
            } else if (id == R.id.action_favorites) {
                // Отобразить фрагмент с избранными
                fragmentManagerHelper.switchToFragment(FavoritesRecyclerViewFragment.class);
                return true;
            } else if (id == R.id.action_cart) {
                // Отобразить фрагмент с корзиной
                fragmentManagerHelper.switchToFragment(CartRecyclerViewFragment.class);
                return true;
            }
            return false;
        });
    }


    // Создание пунктов меню в панели инструментов
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (sharedPreferencesHelper.getString(AlbumPreferences.DISPLAY_TYPE_KEY).equals("grid"))
            getMenuInflater().inflate(R.menu.menu_toolbar_grid, menu);
        else if (sharedPreferencesHelper.getString(AlbumPreferences.DISPLAY_TYPE_KEY).equals("list"))
            getMenuInflater().inflate(R.menu.menu_toolbar_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_create_album)
            CreateAlbumManager.createAlbum(this, findViewById(itemId));
        else if (itemId == R.id.action_view_type)
            replaceViewType();
        else if (itemId == R.id.action_open_context_menu)
            PopupWindowAlbumContextMenu.run(this, findViewById(R.id.action_open_context_menu));

        return super.onOptionsItemSelected(item);
    }

    private void replaceViewType() {
        if (sharedPreferencesHelper.getString(AlbumPreferences.DISPLAY_TYPE_KEY).contains("grid"))
            sharedPreferencesHelper.replaceString(AlbumPreferences.DISPLAY_TYPE_KEY, AlbumPreferences.DISPLAY_TYPE_LIST_VALUES);
        else
            sharedPreferencesHelper.replaceString(AlbumPreferences.DISPLAY_TYPE_KEY, AlbumPreferences.DISPLAY_TYPE_GRID_VALUES);
        invalidateOptionsMenu();
        updateFragment();
    }

    private void updateFragment() {
        if (fragmentManagerHelper.isCurrentFragmentInstanceOf(AlbumRecyclerViewFragment.class)) {
            AlbumRecyclerViewFragment fragment = (AlbumRecyclerViewFragment) fragmentManagerHelper.getCurrentFragment();
            fragment.updateTypeDisplay();
        }
    }

    @Override
    public void onPermissionsGranted() {
        progressBar.setVisibility(View.GONE);
    }
}
