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
    protected Context context;
    protected RecyclerView recyclerView;
    protected TextView textView;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    protected BaseViewModel<Model> viewModel;
    protected FragmentRecyclerViewBinding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecyclerViewBinding.inflate(inflater, container, false);
        initializeViews();
        configureRecyclerView();
        setObserve();
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    protected void initializeViews() {
        textView = binding.fragmentText;
        recyclerView = binding.recyclerViewList;
    }

    protected void openActivity(int position, Class<?> openClass) {
        ActivityNavigator navigator = new ActivityNavigator(context);
        Bundle extras = new Bundle();
        extras.putInt("position", position);
        navigator.navigateToActivityWithExtras(openClass, extras);
    }

    protected abstract void setObserve();

    protected abstract void configureRecyclerView();

    protected void hideTextInFragment() {
        setTextViewVisibility(View.GONE);
    }

    protected void showTextInFragment() {
        setTextViewVisibility(View.VISIBLE);
    }

    protected void setTextFragment(int text) {
        textView.setText(text);
    }

    protected abstract void viewFragmentText(Boolean isEmpty);

    private void setTextViewVisibility(int visibility) {
        textView.setVisibility(visibility);
    }
}