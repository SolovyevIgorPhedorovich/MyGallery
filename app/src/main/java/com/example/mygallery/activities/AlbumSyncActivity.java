package com.example.mygallery.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mygallery.databinding.ActivityAlbumSelectBinding;
import com.example.mygallery.fragments.AlbumRecyclerViewFragment;
import com.example.mygallery.navigator.FragmentNavigatorHelper;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class AlbumSyncActivity extends AppCompatActivity {
    FragmentNavigatorHelper fragmentNavigatorHelper;
    private ActivityAlbumSelectBinding binding;
    private AlbumViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewModel();
        setupUI();

    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(AlbumViewModel.class);
    }

    private void setupUI() {
        AlbumRecyclerViewFragment fragment = new AlbumRecyclerViewFragment(this);
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .commit();

    }


}
