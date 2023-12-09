package com.example.mygallery.fragments;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.mygallery.R;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewPager.ConfigurationViewPager;
import com.example.mygallery.viewmodel.BaseViewModel;

public class ViewChoiceImage extends Fragment {
    private final BaseViewModel<Model> viewModel;
    private final SparseBooleanArray selectedItems;
    private final OnItemClickListener listener;
    private final Fragment fragment;
    private int currentPosition;
    private CheckBox checkBox;
    private ImageButton backButton;

    public ViewChoiceImage(Fragment fragment, int position, BaseViewModel<Model> viewModel, SparseBooleanArray selectedItems, OnItemClickListener listener) {
        this.currentPosition = position;
        this.viewModel = viewModel;
        this.selectedItems = selectedItems;
        this.listener = listener;
        this.fragment = fragment;
    }

    private void initializeViews(View view) {
        backButton = view.findViewById(R.id.button_back);
        checkBox = view.findViewById(R.id.check_box);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_view_image, container, false);
        initializeViews(view);
        setClickListeners();

        ConfigurationViewPager viewPager = new ConfigurationViewPager(view.findViewById(R.id.view_pager), currentPosition, this::setCurrentPosition);
        viewPager.setAdapter(viewModel.getList());
        return view;
    }

    private void setClickListeners() {
        checkBox.setOnClickListener(v -> handleCheckBoxClick());
        backButton.setOnClickListener(v -> closeFragment());
    }

    private void closeFragment() {
        FragmentManager fragmentManager = fragment.getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private void setCurrentPosition(int position) {
        currentPosition = position;
        updateCheckBoxState(selectedItems.get(position, false));
    }

    private void updateCheckBoxState(boolean isChecked) {
        checkBox.setChecked(isChecked);
    }

    private void updateSelectedItems() {
        selectedItems.put(currentPosition, checkBox.isChecked());
    }

    private void handleCheckBoxClick() {
        updateSelectedItems();
        viewModel.toggleSelection(currentPosition);
        listener.onItemClick(currentPosition);
    }
}