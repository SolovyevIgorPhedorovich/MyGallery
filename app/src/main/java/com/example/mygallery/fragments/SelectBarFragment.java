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
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.activities.AlbumGridActivity;
import com.example.mygallery.interfaces.OnFragmentInteractionListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.navigator.FragmentNavigator;
import com.example.mygallery.popupWindow.PopupWindowRemovedContextMenu;
import com.example.mygallery.popupWindow.PopupWindowSelectBarContextMenu;
import com.example.mygallery.viewmodel.BaseViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SelectBarFragment extends Fragment {
    private final BaseViewModel<Model> viewModel;
    private final Map<Integer, ImageButton> buttonToolbar;
    private ImageButton buttonBack;
    private TextView textView;
    private View toolbar;
    private int countSelected;
    private boolean isAllSelected;
    private OnFragmentInteractionListener listener;
    private Context context;

    public SelectBarFragment(BaseViewModel<Model> viewModel, Fragment parentFragment) {
        this.viewModel = viewModel;
        this.buttonToolbar = new HashMap<>();
        this.countSelected = 0;
        this.isAllSelected = false;
        this.listener = (OnFragmentInteractionListener) parentFragment;
    }

    private void initializeViews(View view) {
        buttonBack = view.findViewById(R.id.button_back);
        textView = view.findViewById(R.id.select_info);
        toolbar = view.findViewById(R.id.toolbar);
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (listener != null) {
            listener.onPermissionsGranted();
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_bar, container, false);
        initializeViews(view);
        setButtonToolbar();
        setObserve();

        buttonBack.setOnClickListener(v -> closeFragment());
        return view;
    }

    private void setText() {
        String selectedElements = getResources().getQuantityString(R.plurals.selected_elements, countSelected, countSelected);
        textView.setText(selectedElements);
    }

    private void setButtonToolbar() {
        if (toolbar instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) toolbar;

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);

                if (childView instanceof ImageButton) {
                    ImageButton button = (ImageButton) childView;
                    int buttonId = button.getId();

                    buttonToolbar.put(buttonId, button);
                    setButtonClickListener(buttonId, button);
                }
            }
        }
    }

    private void setClickableButton(boolean isEmpty) {
        for (Map.Entry<Integer, ImageButton> entry : buttonToolbar.entrySet()) {
            if (entry.getKey() != R.id.button_selected_all) {
                ImageButton button = entry.getValue();
                button.setClickable(!isEmpty);
                if (!isEmpty) {
                    button.setAlpha(1.0f);
                } else {
                    button.setAlpha(0.5f);
                }
            }
        }
    }

    private void setButtonClickListener(int buttonId, ImageButton button) {
        button.setOnClickListener(view -> {
            if (buttonId == R.id.button_share) {
                //TODO: add method share
            } else if (buttonId == R.id.button_move) {
                FragmentNavigator.openActionFragment(context, ActionFileFragment.Action.MOVE, viewModel, viewModel.getItem(0), this::closeFragment);
            } else if (buttonId == R.id.button_selected_all) {
                if (!isAllSelected)
                    viewModel.selectAll();
                else
                    viewModel.clearAll();
            } else if (buttonId == R.id.button_remove) {
                PopupWindowRemovedContextMenu.run(context, view, viewModel, this::closeFragment);
            } else if (buttonId == R.id.button_context_menu) {
                PopupWindowSelectBarContextMenu.show(context, view, viewModel, viewModel.getItem(0), this::closeFragment);
            }
        });
    }

    private void setObserve() {
        viewModel.listener().observe(getViewLifecycleOwner(), o -> {
            setClickableButton(o.totalCheckedCount() == 0);
            isAllSelected = o.isAllSelected();
            countSelected = o.totalCheckedCount();
            setText();
        });

    }

    private void closeFragment() {
        FragmentManager fragmentManager;
        if(context instanceof AlbumActivity){
            fragmentManager = ((AlbumActivity) context).getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        else if (context instanceof AlbumGridActivity){
            fragmentManager = ((AlbumGridActivity) context).getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
    }
}
