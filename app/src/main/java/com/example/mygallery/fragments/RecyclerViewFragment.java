package com.example.mygallery.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.navigator.ActivityNavigator;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import org.jetbrains.annotations.NotNull;

public abstract class RecyclerViewFragment extends Fragment {
    protected Context fragmentContext = getActivity();
    protected RecyclerView recyclerView;
    protected TextView textView;
    protected SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

    protected void initializedViews(View view) {
        textView = view.findViewById(R.id.fragment_text);
        recyclerView = view.findViewById(R.id.recycler_view_list);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentContext = null; // Очищаем ссылку на контекст при отсоединении фрагмента
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        initializedViews(view);
        configureRecyclerView(); // Конфигурирование RecyclerView
        setObserve();
        return view;
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
