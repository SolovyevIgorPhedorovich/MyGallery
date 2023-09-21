package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mygallery.fragment.RecyclerViewFragment;
import com.example.mygallery.R;

public class AlbumGridActivity extends AppCompatActivity {
    private TextView albumNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_grid);

        initializeViews();
        setupUI();
    }

    private void initializeViews() {
        albumNameTextView = findViewById(R.id.album_name_text_view);
    }

    private void setupUI() {

        // Получение intent из предыдущей активности
        Intent intent = getIntent();
        if (intent != null) {
            String albumName = intent.getStringExtra("albumName");
            if (albumName != null) {
                albumNameTextView.setText(albumName);
            }
        }

        // Настройка RecyclerView
        RecyclerViewFragment fragment = new RecyclerViewFragment(this, RecyclerViewFragment.IMAGES);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        findViewById(R.id.buttonBack).setOnClickListener(view -> onBackPressed());
    }
}