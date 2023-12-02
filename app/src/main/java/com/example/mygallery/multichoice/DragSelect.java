package com.example.mygallery.multichoice;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.image.ImageAdapter;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.dragselection.DragSelectionProcessor;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.navigator.FragmentNavigator;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

public class DragSelect {
    private final DragSelectTouchListener dragSelectTouchListener; // объект для отслеживания событий перетаскивания
    private BaseViewModel<Model> viewModel; // объект ViewModel, который управляет данными
    private boolean isStartedFragment = false; // флаг, указывающий, запущен ли фрагмент
    private final Context context;
    private final View toolbar;
    private final RecyclerView recyclerView;
    private ImageAdapter<Model> adapter;

    public DragSelect(Context context, View toolbar, RecyclerView recyclerView) {
        this.dragSelectTouchListener = new DragSelectTouchListener();
        this.context = context;
        this.toolbar = toolbar;
        this.recyclerView = recyclerView;
    }

    public void setAdapter(ImageAdapter<Model> adapter) {
        this.adapter = adapter;
    }

    public void setConfigDragSelectTouchListener() {
        dragSelectTouchListener
                .withSelectListener(setOnDragSelectListener()) // устанавливаем слушатель событий перетаскивания
                .withMaxScrollDistance(20) // устанавливаем максимальное расстояние прокрутки
                .withTopOffset(0) // устанавливаем отступ сверху
                .withBottomOffset(0) // устанавливаем отступ снизу
                .withScrollAboveTopRegion(true) // разрешаем прокрутку выше верхней области
                .withScrollBelowTopRegion(true); // разрешаем прокрутку ниже верхней области
    }

    public void setViewModel(BaseViewModel<Model> viewModel) {
        this.viewModel = viewModel;
    }

    public void startSelected(int position, Fragment fragment) {
        if (!isStartedFragment) {
            FragmentNavigator.openSelectBarFragment(fragment, context, viewModel); // открываем фрагмент с выбором элемента
            toolbar.setOnClickListener(v -> {
            });
            isStartedFragment = true; // устанавливаем флаг, что фрагмент запущен
        }
        startDragSelection(position); // запускаем процесс перетаскивания
    }

    public void startDragSelection(int position) {
        dragSelectTouchListener.startDragSelection(position);
    }

    public DragSelectTouchListener getDragSelectTouchListener() {
        return dragSelectTouchListener;
    }

    public void choiceItem(int position) {
        viewModel.toggleSelection(viewModel.getItem(position)); // изменяем выбор элемента
    }

    public void updateCheckBoxAdapter(int position) {
        ImageViewHolder viewHolder = (ImageViewHolder) recyclerView.findViewHolderForAdapterPosition(position); // находим ViewHolder для указанной позиции
        if (viewHolder != null) {
            adapter.setCheckedState(viewHolder, position); // устанавливаем состояние выбора для ViewHolder
        }
    }

    public void toggle() {
        isStartedFragment = !isStartedFragment; // переключаем флаг запущенности фрагмента
    }

    private DragSelectTouchListener.OnDragSelectListener setOnDragSelectListener() {
        return new DragSelectionProcessor(new DragSelectionProcessor.SelectedHandler() {
            @Override
            public void getSelection(SparseBooleanArray selectedItems) {
                adapter.setSelectedItems(selectedItems);
            }

            @Override
            public void updateSelection(int position) {
                updateCheckBoxAdapter(position);
                choiceItem(position);
            }
        });
    }
}
