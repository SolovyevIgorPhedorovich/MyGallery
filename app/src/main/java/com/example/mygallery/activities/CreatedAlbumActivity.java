package com.example.mygallery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.databinding.ActivityAddFileInNewAlbumBinding;
import com.example.mygallery.fragments.AlbumRecyclerViewFragment;
import com.example.mygallery.fragments.ChoiceImageListFragment;
import com.example.mygallery.fragments.ImageRecyclerViewFragment;
import com.example.mygallery.fragments.RecyclerViewFragment;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class CreatedAlbumActivity extends AppCompatActivity {
    private RecyclerViewFragment fragmentImage, fragmentAlbum;
    private TextView fragmentTextView;
    private ImageButton buttonViewType;
    private ImageViewModel images;
    private String pathAlbum;
    private FragmentType currentFragment = FragmentType.IMAGE;
    private ActivityAddFileInNewAlbumBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFileInNewAlbumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViews();
        initializedViewModels();
        setupUI();
    }

    private void initializeViews() {
        fragmentTextView = binding.nameFragment;
        buttonViewType = binding.buttonSwap;

        setListenerButton();
    }

    private void setListenerButton() {
        buttonViewType.setOnClickListener(view -> {
            switch (currentFragment) {
                case IMAGE:
                    fragmentTextView.setText(R.string.albums);
                    buttonViewType.setImageResource(R.drawable.baseline_all_photo_32);
                    currentFragment = FragmentType.ALBUM;
                    break;
                case ALBUM:
                    fragmentTextView.setText(R.string.all_photo);
                    buttonViewType.setImageResource(R.drawable.baseline_photo_library_32);
                    currentFragment = FragmentType.IMAGE;
                    break;
            }
            toggleFragment();
        });

    }

    private void initializedViewModels() {
        images = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(ImageViewModel.class);
    }

    private void setupUI() {

        // Получение intent из предыдущей активности
        Intent intent = getIntent();
        if (intent != null) {
            pathAlbum = intent.getStringExtra("pathAlbum");
        }

        // Настройка RecyclerView

        fragmentImage = new ImageRecyclerViewFragment(Environment.getExternalStorageDirectory(), images);
        fragmentAlbum = new AlbumRecyclerViewFragment();
        ChoiceImageListFragment fragmentChoice = new ChoiceImageListFragment(images, pathAlbum);
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), fragmentImage)
                .add(binding.choiceInfoContainer.getId(), fragmentChoice)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        binding.buttonClose.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void toggleFragment() {
        switch (currentFragment) {
            case ALBUM:
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), fragmentAlbum)
                        .commit();
                break;
            case IMAGE:
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), fragmentImage)
                        .commit();
                break;
        }
    }

    private enum FragmentType {ALBUM, IMAGE}

}
