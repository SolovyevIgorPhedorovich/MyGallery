package com.example.mygallery.viewmodel;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.mygallery.App;
import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final App app;

    public ViewModelFactory(App app) {
        this.app = app;
    }

    public static ViewModelFactory factory(Activity activity) {
        App app = (App) activity.getApplication();
        return new ViewModelFactory(app);
    }

    public static ViewModelFactory factory(Fragment fragment) {
        App app = (App) fragment.requireActivity().getApplication();
        return new ViewModelFactory(app);
    }

    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(AlbumViewModel.class))
            return (T) new AlbumViewModel(app.albums);

        else if (modelClass.isAssignableFrom(CartViewModel.class))
            return (T) new CartViewModel(app.cart);

        else if (modelClass.isAssignableFrom(FavoritesViewModel.class))
            return (T) new FavoritesViewModel(app.favorites);

        else if (modelClass.isAssignableFrom(ImageViewModel.class))
            return (T) new ImageViewModel(app.images);

        else
            throw new IllegalArgumentException("Неизвестный класс модели: " + modelClass.getName());
    }
}


