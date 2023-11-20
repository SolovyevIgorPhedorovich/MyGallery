package com.example.mygallery.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.databinding.ActivityAlbumGridBinding;
import com.example.mygallery.fragments.ImageRecyclerViewFragment;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class AlbumGridActivity extends AppCompatActivity {
    private BaseViewModel<Model> viewModel;
    private int position;
    private ActivityAlbumGridBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumGridBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewModel();
        setupUI();
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
                binding.albumNameTextView.setText(viewModel.getName(position));
            }
        }

        // Настройка RecyclerView
        ImageRecyclerViewFragment fragment = new ImageRecyclerViewFragment(viewModel.getPath(position));
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        binding.buttonBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }
}