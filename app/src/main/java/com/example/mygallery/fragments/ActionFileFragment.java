package com.example.mygallery.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.album.FolderListAdapter;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.managers.FileManager;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewmodel.AlbumViewModel;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.ViewModelFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ActionFileFragment extends Fragment {

    private final Action typeAction;
    private final Model file;
    private final BaseViewModel<Model> viewModel;
    private View view;
    private Context context;
    private ImageButton closeFragment;
    private TextView textView;
    private RecyclerView recyclerView;
    private AlbumViewModel albumViewModel;
    private OnFragmentInteractionListener listener;
    // Конструктор класса
    public ActionFileFragment(Action typeAction, BaseViewModel<Model> viewModel, Model file) {
        this.typeAction = typeAction;
        this.file = file;
        this.viewModel = viewModel;
    }

    public ActionFileFragment(Action typeAction, BaseViewModel<Model> viewModel, Model file, OnFragmentInteractionListener listener) {
        this(typeAction, viewModel, file);
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.context = context;

        albumViewModel = new ViewModelProvider(this, ViewModelFactory.factory(this)).get(AlbumViewModel.class);
    }

    // Метод жизненного цикла onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_action_file, container, false);

        if (context instanceof ImageViewActivity) {
            view.setPadding(0, 0, 0, 0);
        }

        initializeViews();

        // Установка текста
        setTextView();

        // Создание и настройка адаптера для RecyclerView
        createRecyclerViewAdapter();

        // Установка слушателя для кнопки закрытия
        setCloseFragmentListener();

        return view;
    }

    // Инициализация элементов интерфейса
    private void initializeViews() {
        textView = view.findViewById(R.id.type_action);
        recyclerView = view.findViewById(R.id.list);
        closeFragment = view.findViewById(R.id.close_fragment);
    }

    // Установка текста в зависимости от типа действия
    private void setTextView() {
        int actionTextResourceId = typeAction == Action.COPY ? R.string.copy_file : R.string.move_file_in;
        textView.setText(actionTextResourceId);
    }

    // Установка слушателя для кнопки закрытия фрагмента
    private void setCloseFragmentListener() {
        closeFragment.setOnClickListener(view -> closeFragment());
    }

    // Создание адаптера и настройка RecyclerView
    private void createRecyclerViewAdapter() {
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FolderListAdapter adapter = new FolderListAdapter(context, albumViewModel.getList(), albumViewModel.getPathListByPathImage(file.getPath()), this::actionFile);
        recyclerView.setAdapter(adapter);
    }

    // Закрытие фрагмента
    private void closeFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private void actionFile(int position) {
        FileManager fileManager = new FileManager(context, viewModel, listener);
        File destPath = albumViewModel.getPath(position);
        fileManager.setPosition(position);

        switch (typeAction) {
            case MOVE:
                fileManager.moveFile(destPath);
                break;
            case COPY:
                fileManager.copyFile(destPath);
                break;
        }
        closeFragment();
    }

    public enum Action {MOVE, COPY}
}