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
import com.example.mygallery.interfaces.model.Model;
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
    private OnFragmentInteraction listener;
    private Context context;

    public SelectBarFragment(BaseViewModel<Model> viewModel, Fragment parentFragment) {
        this.viewModel = viewModel;
        this.buttonToolbar = new HashMap<>();
        this.countSelected = 0;
        this.isAllSelected = false;
        this.listener = (OnFragmentInteraction) parentFragment;
    }

    private void initializeViews(View view) {
        buttonBack = view.findViewById(R.id.button_back);
        textView = view.findViewById(R.id.select_info);
        toolbar = view.findViewById(R.id.tool_bar);
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
            listener.onFragmentClosed();
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_bar, container, false);
        initializeViews(view);
        setButtonToolbar();
        setObserve();

        buttonBack.setOnClickListener(this::closeFragment);
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
            if (entry.getKey() != R.id.button_selected_all)
                entry.getValue().setClickable(!isEmpty);
        }
    }

    private void setButtonClickListener(int buttonId, ImageButton button) {
        button.setOnClickListener(view -> {
            if (buttonId == R.id.button_share) {
                //TODO: add method share
                return;
            } else if (buttonId == R.id.button_move) {
                com.example.mygallery.navigator.FragmentManager.openActionFragment(context, ActionFileFragment.Action.MOVE, viewModel, viewModel.getItem(0));
            } else if (buttonId == R.id.button_selected_all) {
                if (!isAllSelected)
                    viewModel.selectAll();
                else
                    viewModel.clearAll();
            } else if (buttonId == R.id.button_remove) {
                PopupWindowRemovedContextMenu.run(context, view, viewModel);
            } else if (buttonId == R.id.button_context_menu) {
                PopupWindowSelectBarContextMenu.show(context, view, viewModel, viewModel.getItem(0));
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

    private void closeFragment(View view) {
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

    public interface OnFragmentInteraction {
        void onFragmentClosed();
    }
}
