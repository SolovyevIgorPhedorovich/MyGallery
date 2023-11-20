package com.example.mygallery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.activities.CreatedAlbumActivity;
import com.example.mygallery.databinding.ActivityAlbumGridBinding;
import com.example.mygallery.fragments.AlbumRecyclerViewFragment;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

import java.io.File;

public class AlbumSelected extends AppCompatActivity implements OnItemClickListener {
    //private final CreatedAlbumActivity.SelectedAlbum listener;
    private ActivityAlbumGridBinding binding;
    private AlbumViewModel viewModel;
    private File selectedAlbumPath;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupUI() {

        // Настройка RecyclerView
        AlbumRecyclerViewFragment fragment = new AlbumRecyclerViewFragment(this);
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        binding.buttonBack.setOnClickListener(this::closeActivity);
    }

    private void closeActivity(View view) {
        Intent result = new Intent();

        if (selectedAlbumPath != null) {
            result.putExtra("result", selectedAlbumPath.getAbsolutePath());
            setResult(Activity.RESULT_OK, result);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }

    private File getPathAlbum(int position) {
        return viewModel.getPath(position);
    }

    @Override
    public void onItemClick(int position) {
        selectedAlbumPath = getPathAlbum(position);
        closeActivity(null);
    }
}
