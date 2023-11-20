package com.example.mygallery.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.databinding.FragmentRecyclerViewBinding;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.navigator.ActivityNavigator;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import com.example.mygallery.viewmodel.BaseViewModel;
import org.jetbrains.annotations.NotNull;

public abstract class RecyclerViewFragment extends Fragment {
    protected Context fragmentContext = getActivity();
    protected RecyclerView recyclerView;
    protected TextView textView;
    protected BaseViewModel<Model> viewModel;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    protected FragmentRecyclerViewBinding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

    protected void initializedViews() {
        textView = binding.fragmentText;
        recyclerView = binding.recyclerViewList;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentContext = null; // Очищаем ссылку на контекст при отсоединении фрагмента
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecyclerViewBinding.inflate(inflater, container, false);
        initializedViews();
        configureRecyclerView(); // Конфигурирование RecyclerView
        setObserve();
        return binding.getRoot();
    }

    protected void openActivity(int position, Class<?> openClass) {
        ActivityNavigator navigator = new ActivityNavigator(fragmentContext);
        Bundle extras = new Bundle();
        extras.putInt("position", position);
        navigator.navigateToActivityWithExtras(openClass, extras);
    }

    protected abstract void setObserve();

    // Конфигурирование RecyclerView в зависимости от типа фрагмента
    protected abstract void configureRecyclerView();

    protected void hideTextInFragment() {
        textView.setVisibility(View.GONE);
    }

    protected void showTextInFragment() {
        textView.setVisibility(View.VISIBLE);
    }

    protected void setTextFragment(int text) {
        textView.setText(text);
    }

    protected abstract void viewFragmentText(Boolean isEmpty);
}
