package com.example.mygallery.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.DiffUtilCallback;
import com.example.mygallery.R;
import com.example.mygallery.adapters.ListSelectedAdapter;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.viewmodel.ImageViewModel;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ChoiceImageListFragment extends Fragment {
    private final ImageViewModel images;
    private final File pathAlbum;
    private Context context;
    private TextView countChoiceFile, fragmentTextView;
    private RecyclerView recyclerView;
    private Button buttonDone;
    private ListSelectedAdapter adapter;
    private FileManager fileManager;

    public ChoiceImageListFragment(ImageViewModel images, String pathAlbum) {
        this.images = images;
        this.pathAlbum = new File(pathAlbum);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.context = context;
        OnFragmentInteractionListener listener = (OnFragmentInteractionListener) context;
        this.fileManager = new FileManager(context, images, listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choice_list, container, false);
        initializeViews(view);
        configureRecyclerView();
        setObserve();
        setOnClickListener();
        return view;
    }

    private void initializeViews(View view) {
        countChoiceFile = view.findViewById(R.id.count_selected);
        fragmentTextView = view.findViewById(R.id.selected_info_text);
        recyclerView = view.findViewById(R.id.selected_element);
        buttonDone = view.findViewById(R.id.button_done_selected);
    }

    private void setOnClickListener() {
        buttonDone.setOnClickListener(v -> fileManager.copyFile(pathAlbum));
    }

    private void setObserve() {
        images.listener().observe(getViewLifecycleOwner(), imageMultiChoiceState -> {
            updateAdapterList();
            updateCountAndVisibility(imageMultiChoiceState.totalCheckedCount());
        });
    }

    private void updateAdapterList() {
        DiffUtilCallback<Model> callback = new DiffUtilCallback<>(adapter.onGetDataList(), images.getSelectedItems());
        adapter.onSetDataList(images.getSelectedItems());
        callback.start(adapter);
    }

    private void updateCountAndVisibility(int count) {
        countChoiceFile.setText(String.valueOf(count));
        viewFragmentText(count == 0);
    }

    private void configureRecyclerView() {
        setAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    private void viewFragmentText(Boolean isEmpty) {
        fragmentTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        buttonDone.setClickable(!isEmpty);
        buttonDone.setAlpha(isEmpty ? 0.5f : 1.0f);
    }

    private void setAdapter() {
        adapter = new ListSelectedAdapter(context, images.getSelectedItems(), images::toggleSelection);
        recyclerView.setAdapter(adapter);
    }
}