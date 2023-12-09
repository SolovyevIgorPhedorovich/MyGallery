package com.example.mygallery.fragments;

import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.mygallery.DiffUtilCallback;
import com.example.mygallery.activities.CreatedAlbumActivity;
import com.example.mygallery.adapters.FileChoiceAdapter;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.multichoice.DragSelect;
import com.example.mygallery.multichoice.MultiChoiceState;
import com.example.mygallery.navigator.FragmentNavigator;

import java.util.List;

public abstract class ImageGrid extends RecyclerViewFragment implements OnFragmentInteractionListener {
    private static final int SPAN_COUNT = 4;
    private DragSelect dragSelect;
    private ImageAdapter<Model> adapter;
    private View toolbar;

    @Override
    protected void initializeViews() {
        super.initializeViews();
        toolbar = binding.toolbar;
    }

    @Override
    protected void setObserve() {
        viewModel.data.observe(getViewLifecycleOwner(), this::updateAdapter);
        viewModel.listener().observe(getViewLifecycleOwner(), this::updateAdapter);
    }

    @Override
    protected void configureRecyclerView() {
        createAdapter();
        setupDragSelect();
        setupRecyclerView();
    }

    @Override
    protected void viewFragmentText(Boolean isEmpty) {
        // Implementation if needed
    }

    @Override
    public void onPermissionsGranted() {
        handlePermissionsGranted();
    }

    private void setupDragSelect() {
        dragSelect.setViewModel(viewModel);
        dragSelect.setConfigDragSelectTouchListener();
        recyclerView.addOnItemTouchListener(dragSelect.getDragSelectTouchListener());
    }

    private void createAdapter() {
        dragSelect = new DragSelect(context, toolbar, recyclerView);

        if (context instanceof CreatedAlbumActivity) {
            adapter = new FileChoiceAdapter(context, viewModel.getList(), SPAN_COUNT, this::openViewing, dragSelect::choiceItem, dragSelect::startDragSelection, this::isChecked);
        } else {
            adapter = new ImageAdapter<>(context, viewModel.getList(), SPAN_COUNT, this::openViewing, dragSelect::choiceItem, p -> dragSelect.startSelected(p, this), this::isChecked);
        }
        recyclerView.setAdapter(adapter);
        dragSelect.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
    }

    private void handlePermissionsGranted() {
        dragSelect.toggle();
        toolbar.setOnClickListener(null);
        viewModel.clearAll();
        adapter.reset();
    }

    // Helper method to update the adapter with new data
    private void updateAdapter(List<Model> data) {
        if (adapter == null) {
            return;
        }

        DiffUtilCallback<Model> callback = new DiffUtilCallback<>(adapter.onGetDataList(), data);
        adapter.onSetDataList(data);
        callback.start(adapter);
        viewFragmentText(data.isEmpty());
    }

    private void updateAdapter(MultiChoiceState<Model> data) {
        if (adapter == null) {
            return;
        }

        if (data.isAllSelected())
            adapter.selectedAll();
        else if (data.totalCheckedCount() == 0 && !data.isAllSelected())
            adapter.clearAll();
        else
            adapter.updateSelectedItems(data.getSelectedItems());
    }

    private void openViewing(int position) {
        switch (adapter.getMode()) {
            case VIEWING:
                handleViewingMode(position);
                break;
            case SELECTED:
                FragmentNavigator.openSelectedViewFragment(context, this, position, viewModel, adapter.getSelectedItems(), dragSelect::updateCheckBoxAdapter);
                break;
        }
    }

    protected abstract void handleViewingMode(int position);

    private boolean isChecked(int position) {
        return viewModel.isChecked(position);
    }
}
