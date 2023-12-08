package com.example.mygallery.viewPager;

import androidx.viewpager2.widget.ViewPager2;
import com.example.mygallery.DiffUtilCallback;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapter;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapterHelper;
import com.example.mygallery.adapters.imagepager.SelectImagePagerAdapter;
import com.example.mygallery.interfaces.TextViewUpdateListener;
import com.example.mygallery.interfaces.ToggleMenuListener;
import com.example.mygallery.interfaces.model.Model;

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

        // Инициализируем ViewPager и настраиваем обработчики
        initializeViewPager();
        registerOnPageChangeCallback();
    }

    // Инициализируем настройки ViewPager
    private void initializeViewPager() {
        viewPager2.requestLayout();
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());

        // Обновляем TextView, если обработчик предоставлен
        if (updateListener != null) {
            updateListener.updateTextView(initialPosition);
        }
    }

    // Устанавливаем адаптер для ViewPager с ImagePagerAdapter и ToggleMenuListener
    public void setAdapter(List<Model> pathImages, ToggleMenuListener listener) {
        adapter = new ImagePagerAdapter(pathImages, listener);
        setAdapterAndViewPager(adapter);
    }

    // Устанавливаем адаптер и инициализируем ViewPager
    private void setAdapterAndViewPager(ImagePagerAdapterHelper pagerAdapter) {
        adapter = pagerAdapter;
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(initialPosition, false);
    }

    // Получаем текущий адаптер
    public ImagePagerAdapterHelper getAdapter() {
        return adapter;
    }

    // Устанавливаем адаптер для ViewPager с SelectImagePagerAdapter и statusBarHeight
    public void setAdapter(List<Model> pathImages) {
        adapter = new SelectImagePagerAdapter(pathImages);
        setAdapterAndViewPager(adapter);
    }

    // Обновляем адаптер с новым списком изображений
    public void updateAdapter(List<Model> imageList) {
        DiffUtilCallback<Model> callback = new DiffUtilCallback<>(adapter.onGetDataList(), imageList);
        adapter.onSetDataList(imageList);
        callback.start(adapter);
    }

    // Регистрируем обратный вызов для событий изменения страницы
    private void registerOnPageChangeCallback() {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                handlePageSelected(position);
            }
        });
    }

    // Обрабатываем действия при выборе новой страницы
    private void handlePageSelected(int position) {
        // Обновляем TextView, если обработчик предоставлен
        if (updateListener != null) {
            updateListener.updateTextView(position);
        }

        // Обновляем начальную позицию на текущую
        if (initialPosition != position) {
            initialPosition = position;
        }
    }

    // Запускаем слайд-шоу, переходя к следующей странице
    public void slideShow() {
        int position = (initialPosition < adapter.getItemCount() - 1) ? initialPosition + 1 : 0;
        viewPager2.setCurrentItem(position);
        initialPosition = viewPager2.getCurrentItem();
    }
}