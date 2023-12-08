package com.example.mygallery.navigator;

import android.content.Context;
import android.util.SparseBooleanArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.mygallery.fragments.AlbumSelectionFragment;
import com.example.mygallery.fragments.SelectBarFragment.SelectBarCartViewFragment;
import com.example.mygallery.fragments.SelectBarFragment.SelectBarFragment;
import com.example.mygallery.fragments.ViewChoiceImage;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewmodel.BaseViewModel;
import com.example.mygallery.viewmodel.CartViewModel;

public class FragmentNavigator {

    // Получение транзакции фрагмента
    private static FragmentTransaction getFragmentTransaction(Context context) {
        if (context instanceof FragmentActivity) {
            return ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        }
        throw new IllegalArgumentException("Context must be an instance of FragmentActivity");
    }

    // Коммит транзакции фрагмента с добавлением в стек возврата
    private static void commitFragmentTransaction(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.addToBackStack(null).commit();
    }

    // Открытие фрагмента в транзакции
    private static void openFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.add(android.R.id.content, fragment);
    }

    // Открытие фрагмента для действия с файлом
    public static void openAlbumSelectionFragment(Context context, AlbumSelectionFragment.Action typeAction, BaseViewModel<Model> viewModel, Model file) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);
        openFragment(fragmentTransaction, new AlbumSelectionFragment(typeAction, viewModel, file));
        commitFragmentTransaction(fragmentTransaction);
    }

    // Открытие фрагмента для действия с файлом с передачей слушателя взаимодействия
    public static void openAlbumSelectionFragment(Context context, AlbumSelectionFragment.Action typeAction, BaseViewModel<Model> viewModel, Model file, OnFragmentInteractionListener listener) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);
        openFragment(fragmentTransaction, new AlbumSelectionFragment(typeAction, viewModel, file, listener));
        commitFragmentTransaction(fragmentTransaction);
    }

    // Открытие фрагмента для выбора элемента изображения
    public static void openSelectedViewFragment(Context context, int position, BaseViewModel<Model> viewModel, SparseBooleanArray selectedItems, OnItemClickListener listener) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);
        openFragment(fragmentTransaction, new ViewChoiceImage(position, viewModel, selectedItems, listener));
        commitFragmentTransaction(fragmentTransaction);
    }

    // Открытие фрагмента для выбора элемента с панелью
    public static void openSelectBarFragment(Fragment fragment, Context context, BaseViewModel<Model> viewModel) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);
        SelectBarFragment openFragment = (viewModel instanceof CartViewModel)
                ? new SelectBarCartViewFragment(viewModel, fragment)
                : new SelectBarFragment(viewModel, fragment);
        openFragment(fragmentTransaction, openFragment);
        commitFragmentTransaction(fragmentTransaction);
    }
}