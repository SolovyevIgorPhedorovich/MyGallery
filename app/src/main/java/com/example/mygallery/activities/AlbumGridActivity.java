package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.fragments.ImageRecyclerViewFragment;
import com.example.mygallery.R;
import com.example.mygallery.models.Album;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class AlbumGridActivity extends AppCompatActivity {
    private TextView albumNameTextView;
    private BaseViewModel<Album> viewModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_grid);

        initializeViews();
        initializeViewModel();
        setupUI();
    }

    private void initializeViews() {
        albumNameTextView = findViewById(R.id.album_name_text_view);
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(AlbumViewModel.class);
    }

    private void setupUI() {

        // Получение intent из предыдущей активности
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra("position", -1);
            if (position != -1) {
                albumNameTextView.setText(viewModel.getName(position));
            }
        }

        // Настройка RecyclerView
        ImageRecyclerViewFragment fragment = new ImageRecyclerViewFragment(viewModel.getPath(position));
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        findViewById(R.id.button_back).setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }
}