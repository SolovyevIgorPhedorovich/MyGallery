package com.example.mygallery.activities.imageViewActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapter;
import com.example.mygallery.database.DatabaseAlbum;
import com.example.mygallery.fragments.ActionFileFragment;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Image;
import com.example.mygallery.navigator.FragmentManager;
import com.example.mygallery.popupWindow.PopupWindowInputNameContextMenu;
import com.example.mygallery.viewPager.ConfigurationViewPager;
import com.example.mygallery.viewmodel.BaseViewModel;

public class ImageFileManager {

    private final Image image;
    private final Context context;
    private final BaseViewModel<Model> viewModel;
    private Runnable runnable;
    private Handler handler;

    public ImageFileManager(Context context, BaseViewModel<Model> viewModel, Image image) {
        this.image = image;
        this.context = context;
        this.viewModel = viewModel;
    }

    public void moveFile() {
        FragmentManager.openActionFragment(context, ActionFileFragment.Action.MOVE, viewModel, image);
    }

    public void copyFile() {
        FragmentManager.openActionFragment(context, ActionFileFragment.Action.COPY, viewModel, image);
    }

    public void createView() {
        View viewBackground = new View(context);
        viewBackground.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) ((Activity) context).getWindow().getDecorView()).addView(viewBackground);
        viewBackground.setOnClickListener(view -> {
            handler.removeCallbacks(runnable);
            ((ViewGroup) ((Activity) context).getWindow().getDecorView()).removeView(view);
            ((ImageViewActivity) context).toggle();
        });
    }

    public void startSlidShow(ConfigurationViewPager viewPager) {
        createView();
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                viewPager.slideShow();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
        ((ImageViewActivity) context).toggle();
    }

    public void print() {

    }

    public void rename(View view) {
        PopupWindowInputNameContextMenu.show(context, view, image.name);
    }

    public void rename(String newName) {
        if (!newName.isEmpty()) {

            com.example.mygallery.managers.FileManager fileManager = new com.example.mygallery.managers.FileManager(context, viewModel);

            fileManager.renameFile(image, newName);
        }
    }

    public void setAs() {

    }

    public void rotation(ImagePagerAdapter adapter) {
        adapter.rotateImage(image.id);
    }

    public void choose() {
        DatabaseAlbum DBManager = new DatabaseAlbum(context);
        DBManager.updateArtwork(image.path.getParent(), image.path.getAbsolutePath());
    }
}
