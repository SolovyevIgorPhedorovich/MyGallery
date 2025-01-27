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
import com.example.mygallery.multichoice.DragSelect;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import com.example.mygallery.sharedpreferences.values.AlbumPreferences;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlbumRecyclerViewFragment extends RecyclerViewFragment {
    private int spanCount;
    private DragSelect dragSelect;
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
        initializeViewModelAndPreferences(context);
        ((AlbumViewModel) viewModel).findAlbums();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getData();
    }

    @Override
    protected void setObserve() {
        viewModel.data.observe(getViewLifecycleOwner(), this::handleDataUpdate);
    }

    private void handleDataUpdate(List<Model> newData) {
        if (adapter != null) {
            Log.d("Update", "Данные обновлены");
            updateAdapterData(newData);
            viewFragmentText(newData.isEmpty());
            notifyPermissionsGranted();
        }
    }

    private void updateAdapterData(List<Model> newData) {
        DiffUtilCallback<Model> callback = new DiffUtilCallback<>(adapter.onGetDataList(), newData);
        adapter.onSetDataList(newData);
        callback.start(adapter);
    }

    private void notifyPermissionsGranted() {
        if (context instanceof OnFragmentInteractionListener) {
            ((OnFragmentInteractionListener) context).onPermissionsGranted();
        }
    }

    @Override
    protected void configureRecyclerView() {
        String displayType = sharedPreferencesHelper.getString(AlbumPreferences.DISPLAY_TYPE_KEY);
        spanCount = (displayType.equals(AlbumPreferences.DISPLAY_TYPE_GRID_VALUES)) ? 2 : 1;
        createAlbumAdapter(getAlbumAdapter(displayType));
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
        setupDragSelect();
    }

    private AlbumAdapterHelper<Model> getAlbumAdapter(String displayType) {
        if (context instanceof AlbumSelected) {
            return (displayType.equals(AlbumPreferences.DISPLAY_TYPE_GRID_VALUES))
                    ? new AlbumAdapter(context, viewModel.getList(), spanCount, listener, this::isChecked)
                    : new FolderListAdapter(context, viewModel.getList(), listener);
        } else {
            return (displayType.equals(AlbumPreferences.DISPLAY_TYPE_GRID_VALUES))
                    ? new AlbumAdapter(context, viewModel.getList(), spanCount, p -> openActivity(p, AlbumGridActivity.class), this::isChecked)
                    : new FolderListAdapter(context, viewModel.getList(), p -> openActivity(p, AlbumGridActivity.class));
        }
    }

    private void createAlbumAdapter(AlbumAdapterHelper<Model> adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    public void updateTypeDisplay() {
        configureRecyclerView();
    }

    @Override
    protected void viewFragmentText(Boolean isEmpty) {
        if (!isEmpty) {
            hideTextInFragment();
        } else {
            setTextFragment(R.string.empty_album);
            showTextInFragment();
        }
    }

    private void initializeViewModelAndPreferences(Context context) {
        viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(AlbumViewModel.class);
        sharedPreferencesHelper = new SharedPreferencesHelper(context, SharedPreferencesHelper.ALBUM_PREFERENCES);
    }

    private void setupDragSelect() {
//        dragSelect.setViewModel(viewModel);
//        dragSelect.setConfigDragSelectTouchListener();
//        recyclerView.addOnItemTouchListener(dragSelect.getDragSelectTouchListener());
    }

    private boolean isChecked(int position) {
        return viewModel.isChecked(position);
    }
}