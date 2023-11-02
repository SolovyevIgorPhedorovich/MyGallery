package com.example.mygallery.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumGridActivity;
import com.example.mygallery.adapters.album.AlbumAdapterHelper;
import com.example.mygallery.adapters.album.AlbumAdapter;
import com.example.mygallery.adapters.album.FolderListAdapter;
import com.example.mygallery.models.Album;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import com.example.mygallery.sharedpreferences.values.AlbumPreferences;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

public class AlbumRecyclerViewFragment extends RecyclerViewFragment {
    private AlbumViewModel albums;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        albums = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(AlbumViewModel.class);
        sharedPreferencesHelper = new SharedPreferencesHelper(context, SharedPreferencesHelper.ALBUM_PREFERENCES);
    }

    @Override
    public void onStart() {
        super.onStart();
        albums.getData();
    }

    @Override
    protected void setObserve() {
        albums.data.observe(getViewLifecycleOwner(), o -> {
            AlbumAdapterHelper<Album> adapter = (AlbumAdapterHelper<Album>) recyclerView.getAdapter();
            if (adapter != null) {
                Log.d("Update", "Данные обновлены");
                adapter.setDataList(o);
                adapter.notifyDataSetChanged();
                viewFragmentText(o.isEmpty());
            }
        });
    }

    @Override
    protected void configureRecyclerView() {
        int spanCount;
        if (sharedPreferencesHelper.getString(AlbumPreferences.DISPLAY_TYPE_KEY).equals(AlbumPreferences.DISPLAY_TYPE_GRID_VALUES)) {
            spanCount = 2;
            createAlbumAdapter(setGridAdapter()); // Создание адаптера для альбомов
        } else {
            spanCount = 1;
            createAlbumAdapter(setListAdapter());
        }
        recyclerView.setLayoutManager(new GridLayoutManager(fragmentContext, spanCount));
    }

    // Создание адаптера для альбомов
    private void createAlbumAdapter(RecyclerView.Adapter<?> adapter) {
        //viewFragmentText();
        recyclerView.setAdapter(adapter);
    }

    private RecyclerView.Adapter<?> setListAdapter() {
        return new FolderListAdapter(albums.getList(), p -> openActivity(p, AlbumGridActivity.class));
    }

    private RecyclerView.Adapter<?> setGridAdapter() {
        int imageWidth = calculateImageWidth();
        return new AlbumAdapter(albums.getList(), imageWidth, p -> openActivity(p, AlbumGridActivity.class));
    }

    // Вычисление ширины изображения
    private int calculateImageWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) fragmentContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int imageMargin = fragmentContext.getResources().getDimensionPixelSize(R.dimen.layout_margin_10dp);
        return (screenWidth - (2 * imageMargin)) / 2;
    }

    public void updateTypeDisplay() {
        configureRecyclerView();
    }

    //Если adapter recyclerView пуст, то выводит текст
    @Override
    protected void viewFragmentText(Boolean isEmpty) {
        if (!isEmpty) {
            hideTextInFragment();
        } else {
            setTextFragment(R.string.empty_album);
            showTextInFragment();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        albums.updateDatabase();
    }
}
