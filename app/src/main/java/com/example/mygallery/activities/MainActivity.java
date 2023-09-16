package com.example.mygallery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.example.mygallery.InvalidsPathRemoved;
import com.example.mygallery.fragment.RecyclerViewFragment;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.adapters.ImageAdapter;
import com.example.mygallery.R;

public class MainActivity extends AppCompatActivity {
    private TextView directoryName;
    private int imageSize;
    private Intent mIntent;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        directoryName = findViewById(R.id.directoryNameTextView);
        dataManager = DataManager.getInstance(this);
        mIntent = getIntent();

        viewDirectoryImage();
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void viewDirectoryImage(){
        directoryName.setText(mIntent.getStringExtra("nameFolder"));
        RecyclerViewFragment fragment = new RecyclerViewFragment(this, RecyclerViewFragment.IMAGES);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
    }

}