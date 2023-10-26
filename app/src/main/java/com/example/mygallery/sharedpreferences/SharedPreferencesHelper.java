package com.example.mygallery.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mygallery.sharedpreferences.values.AlbumPreferences;

public class SharedPreferencesHelper {

    public static final String ALBUM_PREFERENCES = "display_album_preferences";
    public static final String IMAGE_PREFERENCES = "display_image_preferences";
    public static final String SETTING_PREFERENCES = "setting_preferences";

    private final SharedPreferences displayPreferences;

    public SharedPreferencesHelper(Context context, String preferencesName) {
        displayPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    public static void initializeSharedPreferences(Context context) {
        SharedPreferences albumPreferences = context.getSharedPreferences(ALBUM_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = albumPreferences.edit();
        editor.putString(AlbumPreferences.DISPLAY_TYPE_KEY, AlbumPreferences.DISPLAY_TYPE_GRID_VALUES);
        editor.putString(AlbumPreferences.SORT_ALBUMS_KEY, AlbumPreferences.SORT_ALBUMS_SORT_VALUES);
        editor.apply();

    }

    // Метод для проверки, были ли настройки уже инициализированы
    public static boolean areSharedPreferencesInitialized(Context context) {
        SharedPreferences albumPreferences = context.getSharedPreferences(SharedPreferencesHelper.ALBUM_PREFERENCES, Context.MODE_PRIVATE);
        return albumPreferences.contains(AlbumPreferences.DISPLAY_TYPE_KEY);
    }

    public void replaceString(String key, String value) {
        SharedPreferences.Editor editor = displayPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return displayPreferences.getString(key, null);
    }

}