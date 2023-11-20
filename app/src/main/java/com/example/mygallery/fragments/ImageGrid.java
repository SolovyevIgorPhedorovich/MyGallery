package com.example.mygallery.fragments;

import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.mygallery.DiffUtilCallback;
import com.example.mygallery.activities.CreatedAlbumActivity;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.FileChoiceAdapter;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.multichoice.DragSelect;
import com.example.mygallery.multichoice.MultiChoiceState;
import com.example.mygallery.navigator.FragmentManager;

import java.util.List;

public class ImageGrid extends RecyclerViewFragment implements SelectBarFragment.OnFragmentInteraction {
    private static final int spanCount = 4;
    private DragSelect dragSelect;
    private ImageAdapter<Model> adapter;
    private View toolbar;

    @Override
    protected void initializedViews() {
        super.initializedViews();
        toolbar = binding.toolbar;
    }

    @Override
    protected void setObserve() {
        viewModel.data.observe(getViewLifecycleOwner(), this::updateAdapter);
        viewModel.listener().observe(getViewLifecycleOwner(), this::updateAdapter);
    }

    @Override
    protected void configureRecyclerView() {
        this.dragSelect = new DragSelect(fragmentContext, toolbar, recyclerView);
        dragSelect.setViewModel(viewModel);

        createAdapter();
        dragSelect.setConfigDragSelectTouchListener();

        recyclerView.addOnItemTouchListener(dragSelect.getDragSelectTouchListener());
        recyclerView.setLayoutManager(new GridLayoutManager(fragmentContext, spanCount));
    }

    @Override
    protected void viewFragmentText(Boolean isEmpty) {
    }

    @Override
    public void onFragmentClosed() {
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

        DiffUtilCallback<Model> callback = new DiffUtilCallback<>(adapter.getDataList(), data);
        adapter.setDataList(data);
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

    // Helper method to create the adapter
    private void createAdapter() {
        if (fragmentContext instanceof CreatedAlbumActivity) {
            adapter = new FileChoiceAdapter(fragmentContext, viewModel.getList(), spanCount, this::openViewing, dragSelect::choiceItem, dragSelect::startDragSelection, this::isChecked);
        } else {
            adapter = new ImageAdapter<>(fragmentContext, viewModel.getList(), spanCount, this::openViewing, dragSelect::choiceItem, p -> dragSelect.startSelected(p, this), this::isChecked);
        }
        recyclerView.setAdapter(adapter);
        dragSelect.setAdapter(adapter);
    }

    // Helper method to open the viewing activity
    private void openViewing(int position) {
        switch (adapter.getMode()) {
            case VIEWING:
                openActivity(position, ImageViewActivity.class);
                break;
            case SELECTED:
                FragmentManager.openSelectedViewFragment(fragmentContext, position, viewModel, adapter.getSelectedItems(), dragSelect::updateCheckBoxAdapter);
                break;
        }
    }

    private boolean isChecked(int position) {
        return viewModel.isChecked(position);
    }
}
