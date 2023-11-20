package com.example.mygallery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.databinding.ActivityAddFileInNewAlbumBinding;
import com.example.mygallery.fragments.ChoiceImageListFragment;
import com.example.mygallery.fragments.ImageRecyclerViewFragment;
import com.example.mygallery.fragments.RecyclerViewFragment;
import com.example.mygallery.navigator.ActivityNavigator;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

import java.io.File;

public class CreatedAlbumActivity extends AppCompatActivity {
    private ImageButton buttonViewType;
    private ImageViewModel images;
    private String pathAlbum;
    private ActivityAddFileInNewAlbumBinding binding;
    private ActivityResultLauncher<Intent> listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFileInNewAlbumBinding.inflate(getLayoutInflater());
        listener = getResult();
        setContentView(binding.getRoot());

        initializeViews();
        initializedViewModels();
        setupUI();
    }

    private void initializeViews() {
        setTextInTextView();
        setListenerButton();
    }

    private void setTextInTextView() {
        binding.nameFragment.setText(R.string.albums);
    }

    private void setListenerButton() {
        binding.buttonSwap.setOnClickListener(view -> {
            openSelectedAlbumFragment();
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

        RecyclerViewFragment fragmentImage = new ImageRecyclerViewFragment(Environment.getExternalStorageDirectory(), images);
        ChoiceImageListFragment fragmentChoice = new ChoiceImageListFragment(images, pathAlbum);
        getSupportFragmentManager().beginTransaction()
                .add(binding.fragmentContainer.getId(), fragmentImage)
                .add(binding.choiceInfoContainer.getId(), fragmentChoice)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        binding.buttonClose.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void openSelectedAlbumFragment() {
        ActivityNavigator activityNavigator = new ActivityNavigator(this);
        activityNavigator.navigateToActivityForResult(AlbumSelected.class, listener);
    }

    private ActivityResultLauncher<Intent> getResult() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        String value = result.getData().getStringExtra("result");
                        onUpdateRecyclerView(new File(value));
                    }
                }
        );
    }

    public void onUpdateRecyclerView(File pathAlum) {
        images.scanMediaAlbum(pathAlum);
    }

}
