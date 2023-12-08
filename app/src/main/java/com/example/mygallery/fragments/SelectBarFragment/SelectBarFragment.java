package com.example.mygallery.fragments.SelectBarFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mygallery.R;
import com.example.mygallery.fragments.AlbumSelectionFragment;
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

    protected final BaseViewModel<Model> viewModel;
    protected final Map<Integer, ImageButton> buttonToolbar;
    private ImageButton buttonBack;
    private TextView textView;
    private View toolbar;
    private int countSelected;
    protected boolean isAllSelected;
    private OnFragmentInteractionListener listener;
    protected Context context;

    public SelectBarFragment(BaseViewModel<Model> viewModel, Fragment parentFragment) {
        this.viewModel = viewModel;
        this.buttonToolbar = new HashMap<>();
        this.countSelected = 0;
        this.isAllSelected = false;
        this.listener = (OnFragmentInteractionListener) parentFragment;
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

    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_select_bar, container, false);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getView(inflater, container);
        initializeViews(view);
        setButtonToolbar();
        setObserve();
        buttonBack.setOnClickListener(v -> closeFragment());
        return view;
    }

    private void initializeViews(View view) {
        buttonBack = view.findViewById(R.id.button_back);
        textView = view.findViewById(R.id.select_info);
        toolbar = view.findViewById(R.id.toolbar);
    }

    private void setText() {
        String selectedElements = getResources().getQuantityString(R.plurals.selected_elements, countSelected, countSelected);
        textView.setText(selectedElements);
    }

    private void setButtonToolbar() {
        if (toolbar instanceof ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof ImageButton button) {
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
                button.setAlpha(isEmpty ? 0.5f : 1.0f);
            }
        }
    }

    protected void setButtonClickListener(int buttonId, ImageButton button) {
        button.setOnClickListener(view -> handleButtonClick(view, buttonId));
    }

    private void handleButtonClick(View view, int buttonId) {
        if (buttonId == R.id.button_share) {
            //TODO: add method share
        } else if (buttonId == R.id.button_move) {
            FragmentNavigator.openAlbumSelectionFragment(context, AlbumSelectionFragment.Action.MOVE, viewModel, viewModel.getItem(0), this::closeFragment);
        } else if (buttonId == R.id.button_selected_all) {
            handleSelectedAllButtonClick();
        } else if (buttonId == R.id.button_remove) {
            PopupWindowRemovedContextMenu.show(context, view, viewModel, this::closeFragment);
        } else if (buttonId == R.id.button_context_menu) {
            PopupWindowSelectBarContextMenu.show(context, view, viewModel, viewModel.getItem(0), this::closeFragment);
        }
    }

    protected void handleSelectedAllButtonClick() {
        if (!isAllSelected)
            viewModel.selectAll();
        else
            viewModel.clearAll();
    }

    private void setObserve() {
        viewModel.listener().observe(getViewLifecycleOwner(), o -> {
            setClickableButton(o.totalCheckedCount() == 0);
            isAllSelected = o.isAllSelected();
            countSelected = o.totalCheckedCount();
            setText();
        });
    }

    protected void closeFragment() {
        getParentFragmentManager().popBackStack();
    }
}