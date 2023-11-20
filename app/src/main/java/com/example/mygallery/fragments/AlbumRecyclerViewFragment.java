package com.example.mygallery.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.mygallery.DiffUtilCallback;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumGridActivity;
import com.example.mygallery.activities.AlbumSelected;
import com.example.mygallery.adapters.album.AlbumAdapter;
import com.example.mygallery.adapters.album.AlbumAdapterHelper;
import com.example.mygallery.adapters.album.FolderListAdapter;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import com.example.mygallery.sharedpreferences.values.AlbumPreferences;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

public class AlbumRecyclerViewFragment extends RecyclerViewFragment {
    private int spanCount;
    private OnItemClickListener listener;
    private AlbumAdapterHelper<Model> adapter;

    public AlbumRecyclerViewFragment() {
    }

    public AlbumRecyclerViewFragment(Activity activity) {
        this.listener = (OnItemClickListener) activity;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(AlbumViewModel.class);
        sharedPreferencesHelper = new SharedPreferencesHelper(context, SharedPreferencesHelper.ALBUM_PREFERENCES);

        ((AlbumViewModel) viewModel).findAlbums();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getData();
    }

    @Override
    protected void setObserve() {
        viewModel.data.observe(getViewLifecycleOwner(), o -> {
            if (adapter != null) {
                Log.d("Update", "Данные обновлены");
                DiffUtilCallback<Model> callback = new DiffUtilCallback<>(adapter.onGetDataList(), o);
                adapter.onSetDataList(o);
                callback.start(adapter);
                viewFragmentText(o.isEmpty());
                if (fragmentContext instanceof OnFragmentInteractionListener) {
                    ((OnFragmentInteractionListener) fragmentContext).onPermissionsGranted();
                }
            }
        });
    }

    @Override
    protected void configureRecyclerView() {
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
    private void createAlbumAdapter(AlbumAdapterHelper<Model> adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    private AlbumAdapterHelper<Model> setListAdapter() {
        if (fragmentContext instanceof AlbumSelected) {
            return new FolderListAdapter(viewModel.getList(), listener);
        } else {
            return new FolderListAdapter(viewModel.getList(), p -> openActivity(p, AlbumGridActivity.class));
        }
    }

    private AlbumAdapterHelper<Model> setGridAdapter() {
        if (fragmentContext instanceof AlbumSelected) {
            return new AlbumAdapter(fragmentContext, viewModel.getList(), spanCount, listener);
        } else {
            return new AlbumAdapter(fragmentContext, viewModel.getList(), spanCount, p -> openActivity(p, AlbumGridActivity.class));
        }
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
}
