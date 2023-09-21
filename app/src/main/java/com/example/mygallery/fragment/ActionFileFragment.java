package com.example.mygallery.fragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.FolderListAdapter;
import com.example.mygallery.managers.DatabaseManager;

import java.io.File;

public class ActionFileFragment extends Fragment {

    private final boolean isCopyAction;
    private final Context context;
    private ImageButton closeFragment;
    private TextView textView;
    private RecyclerView recyclerView;
    private FolderSelectionListener folderSelectionListener;

    // Конструктор класса
    public ActionFileFragment(Context context, boolean isCopyAction) {
        this.isCopyAction = isCopyAction;
        this.context = context;
    }

    // Метод жизненного цикла onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Метод жизненного цикла onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_file, container, false);

        // Установка отступа в зависимости от контекста
        if (context instanceof ImageViewActivity) {
            view.setPadding(0, ((ImageViewActivity) context).getStatusBarHeight(), 0, 0);
        }

        // Инициализация элементов интерфейса
        textView = view.findViewById(R.id.type_action);
        recyclerView = view.findViewById(R.id.list);
        closeFragment = view.findViewById(R.id.close_fragment);

        // Установка текста
        setTextView();

        // Создание и настройка адаптера для RecyclerView
        createRecyclerViewAdapter(view);

        // Установка слушателя для кнопки закрытия
        setCloseFragmentListener();

        return view;
    }

    // Установка слушателя для кнопки закрытия фрагмента
    private void setCloseFragmentListener() {
        closeFragment.setOnClickListener(view -> closeFragment());
    }

    // Установка текста в зависимости от типа действия
    private void setTextView() {
        int actionTextResourceId = isCopyAction ? R.string.copy_file : R.string.move_file_in;
        textView.setText(actionTextResourceId);
    }

    // Создание адаптера и настройка RecyclerView
    private void createRecyclerViewAdapter(View view) {
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FolderListAdapter adapter = new FolderListAdapter(context);
        adapter.setOnFolderItemClickListener(nameFolder -> {
            // Получение пути к выбранной папке и обработка выбора
            DatabaseManager dbManager = new DatabaseManager(context);
            File selectedFolderPath = new File(dbManager.getFolderPath(nameFolder));
            dbManager.close();

            if (folderSelectionListener != null) {
                folderSelectionListener.onFolderSelected(selectedFolderPath);
            }
            closeFragment();
        });
        recyclerView.setAdapter(adapter);
    }

    // Закрытие фрагмента
    private void closeFragment() {
        if (context instanceof ImageViewActivity) {
            ImageViewActivity activity = (ImageViewActivity) context;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
    }

    // Метод жизненного цикла onAttach
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            folderSelectionListener = (FolderSelectionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement FolderSelectionListener");
        }
    }

    // Интерфейс для обработки выбора папки
    public interface FolderSelectionListener {
        void onFolderSelected(File folderPath);
    }
}