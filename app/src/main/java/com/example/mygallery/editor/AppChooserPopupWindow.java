package com.example.mygallery.editor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.mygallery.R;
import com.example.mygallery.databinding.LayoutEditorChoiceBinding;
import com.example.mygallery.managers.PopupWindowManager;

import java.util.List;

public class AppChooserPopupWindow extends PopupWindowManager {

    private final List<ResolveInfo> resolveInfoList;
    private final PackageManager packageManager;
    private final LayoutEditorChoiceBinding binding;
    private final SelectedApp listener;
    private final Context context;
    private int selectedPosition;

    public AppChooserPopupWindow(Context context, List<ResolveInfo> resolveInfoList, PackageManager packageManager, SelectedApp listener) {
        super(context);
        this.context = context;
        this.resolveInfoList = resolveInfoList;
        this.packageManager = packageManager;
        this.listener = listener;
        this.binding = LayoutEditorChoiceBinding.inflate(LayoutInflater.from(context));
        setRecyclerView();
    }

    private void setRecyclerView() {
        ResolveInfoAdapter adapter = new ResolveInfoAdapter(resolveInfoList, packageManager, this::setSelectedPosition);
        binding.editors.setAdapter(adapter);
        binding.editors.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    private boolean isCheckBoxChecked() {
        return binding.checkBox.isChecked();
    }

    private void setSelectedPosition(int position) {
        selectedPosition = position;
        binding.OK.setClickable(true);
    }

    @Override
    protected boolean setConfiguration() {
        popupWindow.setWidth(context.getResources().getDisplayMetrics().widthPixels);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popupbg));
        return true;
    }

    @Override
    protected void setAnimation() {
        popupWindow.setAnimationStyle(R.style.popupGlideAnimation);
    }

    @Override
    protected void handleButtonClick(int buttonId) {
        popupWindow.dismiss();
        if (buttonId == R.id.OK) {
            listener.selectedApp(selectedPosition, isCheckBoxChecked());
        }
    }

    public static AppChooserPopupWindow show(Context context, View view, List<ResolveInfo> resolveInfoList, PackageManager packageManager, SelectedApp listener) {
        AppChooserPopupWindow popupWindow = new AppChooserPopupWindow(context, resolveInfoList, packageManager, listener);
        initialize(popupWindow, view);
        return popupWindow;
    }

    private static void initialize(AppChooserPopupWindow popupWindow, View view) {
        View viewContent = popupWindow.binding.getRoot();
        popupWindow.setContent(viewContent);
        popupWindow.setPosition(0, 0);
        popupWindow.showPopupWindow(view, viewContent, Gravity.BOTTOM);
    }
}