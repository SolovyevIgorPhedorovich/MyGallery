package com.example.mygallery.navigator;

import android.content.Context;
import android.util.SparseBooleanArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.mygallery.fragments.ActionFileFragment;
import com.example.mygallery.fragments.SelectBarFragment;
import com.example.mygallery.fragments.ViewChoiceImage;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewmodel.BaseViewModel;

public class FragmentNavigator {

    private static FragmentTransaction getFragmentTransaction(Context context) {
        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;
            return activity.getSupportFragmentManager().beginTransaction();
        }
        throw new IllegalArgumentException("Context must be an instance of FragmentActivity");
    }

    public static void openActionFragment(Context context, ActionFileFragment.Action typeAction, BaseViewModel<Model> viewModel, Model file) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);

        fragmentTransaction
                .add(android.R.id.content, new ActionFileFragment(typeAction, viewModel, file))
                .addToBackStack(null)
                .commit();
    }

    public static void openActionFragment(Context context, ActionFileFragment.Action typeAction, BaseViewModel<Model> viewModel, Model file, OnFragmentInteractionListener listener) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);

        fragmentTransaction
                .add(android.R.id.content, new ActionFileFragment(typeAction, viewModel, file, listener))
                .addToBackStack(null)
                .commit();
    }

    public static void openSelectedViewFragment(Context context, int position, BaseViewModel<Model> viewModel, SparseBooleanArray selectedItems, OnItemClickListener listener) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);

        fragmentTransaction
                .add(android.R.id.content, new ViewChoiceImage(position, viewModel, selectedItems, listener))
                .addToBackStack(null)
                .commit();
    }

    public static void openSelectBarFragment(Fragment fragment, Context context, BaseViewModel<Model> viewModel) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction(context);

        fragmentTransaction
                .add(android.R.id.content, new SelectBarFragment(viewModel, fragment))
                .addToBackStack(null)
                .commit();
    }


}
