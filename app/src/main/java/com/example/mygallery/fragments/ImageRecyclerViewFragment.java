package com.example.mygallery.fragments;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.mygallery.DiffUtilCallback;
import com.example.mygallery.R;
import com.example.mygallery.activities.CreatedAlbumActivity;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.FileChoiceAdapter;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.database.DatabaseManager;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.navigator.FragmentManager;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.ImageViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.example.mygallery.dragselection.DragSelectionProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ImageRecyclerViewFragment extends RecyclerViewFragment implements SelectBarFragment.OnFragmentInteraction {
    private final File albumPath;
    private final DragSelectTouchListener dragSelectTouchListener;
    protected DatabaseManager databaseManager;
    private boolean isStartedFragment = false;
    private BaseViewModel<Model> viewModel;
    private ImageAdapter<Model> adapter;
    private View toolbar;
    private DiffUtilCallback callback;

    public ImageRecyclerViewFragment(File albumPath) {
        this.albumPath = albumPath;
        this.dragSelectTouchListener = new DragSelectTouchListener();
    }

    public ImageRecyclerViewFragment(File albumPath, BaseViewModel<Model> viewModel) {
        this(albumPath);
        this.viewModel = viewModel;
    }

    @Override
    protected void initializedViews() {
        super.initializedViews();
        toolbar = binding.toolbar;
    }

    private void setConfigDragSelectTouchListener() {
        dragSelectTouchListener
                .withSelectListener(setOnDragSelectListener())
                .withMaxScrollDistance(20)
                .withTopOffset(0)
                .withBottomOffset(0)
                .withScrollAboveTopRegion(true)
                .withScrollBelowTopRegion(true);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.databaseManager = new DatabaseManager(context);

        if (viewModel == null)
            viewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(ImageViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewModel instanceof ImageViewModel)
            ((ImageViewModel) viewModel).scanMediaAlbum(albumPath);
    }

    @Override
    protected void setObserve() {
        viewModel.data.observe(getViewLifecycleOwner(), o -> {
            if (adapter != null) {
                callback = new DiffUtilCallback(adapter.getDataList(), o);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
                adapter.setDataList(o);
                diffResult.dispatchUpdatesTo(adapter);
                viewFragmentText(o.isEmpty());
            }
        });
        viewModel.listener().observe(getViewLifecycleOwner(), o -> {
            if (adapter != null) {
                if (o.isAllSelected())
                    adapter.selectedAll();
                else if (o.totalCheckedCount() == 0 && !o.isAllSelected())
                    adapter.clearAll();
            }
        });
    }

    @Override
    protected void configureRecyclerView() {
        createAdapter(); // Создание адаптера
        setConfigDragSelectTouchListener();

        recyclerView.addOnItemTouchListener(dragSelectTouchListener);
        recyclerView.setLayoutManager(new GridLayoutManager(fragmentContext, 4));
    }

    @Override
    protected void viewFragmentText(Boolean isEmpty) {
    }

    // Создание адаптера
    private void createAdapter() {
        if (fragmentContext instanceof CreatedAlbumActivity) {
            adapter = new FileChoiceAdapter(fragmentContext, viewModel.getList(), this::openViewing, this::choiceItem, dragSelectTouchListener::startDragSelection);
        } else {
            adapter = new ImageAdapter<>(fragmentContext, viewModel.getList(), this::openViewing, this::choiceItem, this::startSelected);
        }
        recyclerView.setAdapter(adapter);
    }

    private void openViewing(int position) {
        switch (adapter.getMode()) {
            case VIEWING:
                openActivity(position, ImageViewActivity.class);
                break;
            case SELECTED:
                FragmentManager.openSelectedViewFragment(fragmentContext, position, viewModel, adapter.getSelectedItems(), this::updateCheckBoxAdapter);
                break;
        }
    }

    private void choiceItem(int position) {
        viewModel.toggleSelection(viewModel.getItem(position));
    }

    private void startSelected(int position) {
        if (!isStartedFragment) {
            FragmentManager.openSelectBarFragment(this, fragmentContext, viewModel);
            toolbar.setOnClickListener(v -> {
            });
            isStartedFragment = !isStartedFragment;
        }
        dragSelectTouchListener.startDragSelection(position);
    }

    private DragSelectTouchListener.OnDragSelectListener setOnDragSelectListener() {
        return new DragSelectionProcessor(new DragSelectionProcessor.SelectedHandler() {
            @Override
            public void getSelection(SparseBooleanArray selectedItems) {
                assert adapter != null;
                adapter.setSelectedItems(selectedItems);
            }

            @Override
            public void updateSelection(int position) {
                updateCheckBoxAdapter(position);
                choiceItem(position);
            }
        });
    }

    private void updateCheckBoxAdapter(int position) {
        assert adapter != null;
        adapter.setCheckedState((ImageViewHolder) recyclerView.findViewHolderForAdapterPosition(position), position);
    }


    @Override
    public void onFragmentClosed() {
        isStartedFragment = !isStartedFragment;
        toolbar.setOnClickListener(null);
        viewModel.clearAll();
        adapter.reset();
    }
}
