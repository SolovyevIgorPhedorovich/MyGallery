package com.example.mygallery.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.AlbumAdapter;
import com.example.mygallery.adapters.FolderListAdapter;
import com.example.mygallery.adapters.ImageAdapter;
import com.example.mygallery.adapters.OnItemClickListener;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RecyclerViewFragment extends Fragment {
    public static final int FAVORITES = 0;
    public static final int ALBUM = 1;
    public static final int CART = 2;
    public static final int IMAGES = 4;

    private final int fragmentId;
    private final Context fragmentContext;
    private RecyclerView recyclerView;
    private final DataManager dataManager;

    public RecyclerViewFragment(Context context, int idFragment) {
        this.fragmentContext = context;
        this.fragmentId = idFragment;
        this.dataManager = DataManager.getInstance(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_list);
        configureRecyclerView(); // Конфигурирование RecyclerView
        return view;
    }

    // Конфигурирование RecyclerView в зависимости от типа фрагмента
    private void configureRecyclerView() {
        int spanCount = 1;
        switch (fragmentId) {
            case FAVORITES:
                spanCount = 4;
                dataManager.setImageFilesList(new DatabaseManager(fragmentContext).getFavorites());
                createImagePreviewAdapter(); // Создание адаптера для избранного
                break;
            case CART:
                spanCount = 4;
                dataManager.setImageFilesList((List<File>) FileUtils.listFiles(new File(fragmentContext.getFilesDir(), "Корзина"), null, false));
                createImagePreviewAdapter(); // Создание адаптера для корзины
                break;
            case ALBUM:
                String displayType = getTypeDisplay();
                if (displayType.contains("grid")) {
                    spanCount = 2;
                }
                createAlbumAdapter(displayType); // Создание адаптера для альбомов
                break;
            case IMAGES:
                spanCount = 4;
                createImagePreviewAdapter(); // Создание адаптера для превью изображений
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + fragmentId);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(fragmentContext, spanCount));
    }

    //Открываем json файл конфигурации отображения
    private String getTypeDisplay() {
        SharedPreferences displayPreferences = fragmentContext.getSharedPreferences("display_album_preferences", Context.MODE_PRIVATE);
        return displayPreferences.getString("display_type", null);
    }

    public void updateTypeDisplay() {
        configureRecyclerView();
    }

    // Создание адаптера для альбомов
    private void createAlbumAdapter(String displayType) {
        viewFragmentText();
        if (displayType.contains("grid")) {
            int imageWidth = calculateImageWidth();
            AlbumAdapter adapter = new AlbumAdapter(fragmentContext, imageWidth, new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                }

                @Override
                public void onItemClick(String folderName, int position) {
                    openImageInAlbum(folderName, position);
                }
            });
            recyclerView.setAdapter(adapter);
        } else if (displayType.contains("list")) {
            FolderListAdapter adapter = new FolderListAdapter(fragmentContext, new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                }

                @Override
                public void onItemClick(String folderName, int position) {
                    openImageInAlbum(folderName, position);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    // Создание адаптера для превью изображений
    private void createImagePreviewAdapter() {
        viewFragmentText();
        ImageAdapter adapter = new ImageAdapter(fragmentContext, dataManager.getImageFilesList(), new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openImageViewer(position);
            }

            @Override
            public void onItemClick(String folderName, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    // Вычисление ширины изображения
    private int calculateImageWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AlbumActivity) fragmentContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int imageMargin = fragmentContext.getResources().getDimensionPixelSize(R.dimen.image_preview_margin_horizontal);
        return (screenWidth - (2 * imageMargin)) / 2;
    }

    // Открытие изображения в альбоме
    private void openImageInAlbum(String nameFolder, int position) {
        ((AlbumActivity) fragmentContext).openDirectoryImage(nameFolder, position);
    }

    // Открытие активити для просмотра изображения
    private void openImageViewer(int position) {
        Intent intent = new Intent(fragmentContext, ImageViewActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    // Обновление адаптера RecyclerView
    public void updateAdapter() {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        Log.d("updateAdapter", "Update completed");
    }

    public void updateFragment() {
        switch (fragmentId) {
            case FAVORITES:
                dataManager.setImageFilesList(new DatabaseManager(fragmentContext).getFavorites());
                viewFragmentText();
                updateAdapter(); // Обновляем адаптера для избранного
                break;
            case CART:
                dataManager.setImageFilesList((List<File>) FileUtils.listFiles(new File(fragmentContext.getFilesDir(), "Корзина"), null, false));
                viewFragmentText();
                updateAdapter(); // Обновляем адаптера для корзины
                break;
            case ALBUM:
                viewFragmentText();

        }
    }

    //Если adapter recyclerView пуст то выводит текст
    private void viewFragmentText() {
        if (fragmentContext instanceof AlbumActivity) {
            if (dataManager.isDataImageFilesList() | (fragmentId == ALBUM && dataManager.isDataFolderNamesList())) {
                ((AlbumActivity) fragmentContext).hideTextInFragment();
            } else {
                switch (fragmentId) {
                    case FAVORITES:
                        ((AlbumActivity) fragmentContext).setTextFragment(R.string.empty_favorites);
                        break;
                    case CART:
                        ((AlbumActivity) fragmentContext).setTextFragment(R.string.empty_cart);
                        break;
                    case ALBUM:
                        ((AlbumActivity) fragmentContext).setTextFragment(R.string.empty_album);
                }
                ((AlbumActivity) fragmentContext).showTextInFragment();
            }
        }
    }
}
