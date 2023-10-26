package com.example.mygallery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.R;
import com.example.mygallery.fragments.ChoiceImageListFragment;
import com.example.mygallery.fragments.ImageRecyclerViewFragment;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;

public class CreatedAlbumActivity extends AppCompatActivity {
    private boolean isFragmentType = false;
    private TextView fragmentTextView;
    private ImageButton buttonViewType;
    private ImageViewModel images;
    private String pathAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file_in_new_album);

        initializeViews();
        initializedViewModels();
        setupUI();
    }

    private void initializeViews() {
        fragmentTextView = findViewById(R.id.name_fragment);
        buttonViewType = findViewById(R.id.button_swap);

        setListenerButton();
    }

    private void initializedViewModels() {
        images = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(ImageViewModel.class);
    }

    private void setListenerButton() {
        buttonViewType.setOnClickListener(view -> {
            if (!isFragmentType) {
                fragmentTextView.setText(R.string.albums);
                buttonViewType.setImageResource(R.drawable.baseline_all_photo_32);
            } else {
                fragmentTextView.setText(R.string.all_photo);
                buttonViewType.setImageResource(R.drawable.baseline_photo_library_32);
            }
            isFragmentType = !isFragmentType;
        });

    }

    private void setupUI() {

        // Получение intent из предыдущей активности
        Intent intent = getIntent();
        if (intent != null) {
            pathAlbum = intent.getStringExtra("pathAlbum");
        }

        // Настройка RecyclerView

        ImageRecyclerViewFragment fragmentImage = new ImageRecyclerViewFragment(Environment.getExternalStorageDirectory(), images);
        ChoiceImageListFragment fragmentChoice = new ChoiceImageListFragment(images, pathAlbum);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragmentImage)
                .add(R.id.choice_info_container, fragmentChoice)
                .commit();

        // Обработчик нажатия кнопки "Назад"
        findViewById(R.id.button_close).setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

}
