package com.example.mygallery.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mygallery.databinding.ActivityAlbumSelectBinding;
import com.example.mygallery.navigator.FragmentNavigatorHelper;

public class AlbumSyncActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    FragmentNavigatorHelper fragmentNavigatorHelper;
    private ActivityAlbumSelectBinding binding;

    // Метод для инициализации элементов интерфейса
    private void initializeViews() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentNavigatorHelper = new FragmentNavigatorHelper(getSupportFragmentManager(), binding.fragmentContainer.getId());

        // Инициализация элементов интерфейса
        initializeViews();
    }

    // Действия после возобновления активности
    @Override
    protected void onResume() {
        super.onResume();
    }


}
