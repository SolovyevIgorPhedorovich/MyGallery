package com.example.mygallery.viewPager;

import android.content.Context;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapter;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapterHelper;
import com.example.mygallery.adapters.imagepager.SelectImagePagerAdapter;
import com.example.mygallery.interfaces.TextViewUpdateListener;
import com.example.mygallery.interfaces.ToggleMenuListener;

import java.io.File;
import java.util.List;

public class ConfigurationViewPager {
    private final ViewPager2 viewPager2;
    private final TextViewUpdateListener updateListener;
    private ImagePagerAdapterHelper adapter;
    private int initialPosition;

    public ConfigurationViewPager(ViewPager2 viewPager2, int initialPosition, TextViewUpdateListener listener) {
        this.viewPager2 = viewPager2;
        this.updateListener = listener;
        this.initialPosition = initialPosition;

        onStart();
        registerOnPage();
    }

    private void onStart() {
        viewPager2.requestLayout();
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());

        if (updateListener != null)
            updateListener.updateTextView(initialPosition);
    }

    //Установка адаптера для ViewPager
    public void setAdapter(Context context, List<File> pathImages, ToggleMenuListener listener) {
        adapter = new ImagePagerAdapter(context, pathImages, listener);
        viewPager2.setAdapter(adapter);

        viewPager2.setCurrentItem(initialPosition, false);
    }

    public void setAdapter(Context context, List<File> pathImages, int statusBarHeight) {
        adapter = new SelectImagePagerAdapter(context, pathImages, statusBarHeight);
        viewPager2.setAdapter(adapter);

        viewPager2.setCurrentItem(initialPosition, false);
    }

    public ImagePagerAdapterHelper getAdapter() {
        return adapter;
    }

    public void updateAdapter(List<File> paths) {
        adapter.setList(paths);
        adapter.notifyDataSetChanged();
    }


    //Обработка перелистывания pageViewer2
    private void registerOnPage() {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (updateListener != null)
                    updateListener.updateTextView(position);

                if (initialPosition != position) {
                    adapter.notifyItemChanged(initialPosition); // вынести в отдельный поток с задржкой
                    initialPosition = position;
                }
            }
        });
    }

    public void slideShow() {
        int position = (initialPosition < adapter.getItemCount() - 1) ? initialPosition + 1 : 0;
        viewPager2.setCurrentItem(position);
        initialPosition = viewPager2.getCurrentItem();
    }

}
