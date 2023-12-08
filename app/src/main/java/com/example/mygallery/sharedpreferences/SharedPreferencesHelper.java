package com.example.mygallery.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mygallery.sharedpreferences.values.AlbumPreferences;
import com.example.mygallery.sharedpreferences.values.SettingPreferences;

public class SharedPreferencesHelper {

    // Ключи для именования различных SharedPreferences
    public static final String ALBUM_PREFERENCES = "display_album_preferences";
    public static final String IMAGE_PREFERENCES = "display_image_preferences";
    public static final String SETTING_PREFERENCES = "setting_preferences";

    // Объект для работы с SharedPreferences
    private final SharedPreferences displayPreferences;

    // Конструктор класса
    public SharedPreferencesHelper(Context context, String preferencesName) {
        displayPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    // Инициализация SharedPreferences для альбомов и настроек
    public static void initializeSharedPreferences(Context context) {
        initializeAlbumPreferences(context);
        initializeSettingPreferences(context);
    }

    // Инициализация SharedPreferences для альбомов
    private static void initializeAlbumPreferences(Context context) {
        SharedPreferences albumPreferences = context.getSharedPreferences(ALBUM_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = albumPreferences.edit();
        // Установка значений по умолчанию для альбомов
        editor.putString(AlbumPreferences.DISPLAY_TYPE_KEY, AlbumPreferences.DISPLAY_TYPE_GRID_VALUES);
        editor.putString(AlbumPreferences.SORT_ALBUMS_KEY, AlbumPreferences.SORT_ALBUMS_SORT_VALUES);
        editor.apply();
    }

    // Инициализация SharedPreferences для настроек
    private static void initializeSettingPreferences(Context context) {
        SharedPreferences settingPreferences = context.getSharedPreferences(SETTING_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settingPreferences.edit();
        // Установка значений по умолчанию для настроек
        editor.putString(SettingPreferences.DEFAULT_APP_EDIT_IMAGE_KEY, null);
        editor.putString(SettingPreferences.DEFAULT_APP_EDIT_VIDEO_KEY, null);
        editor.apply();
    }

    // Проверка, были ли настройки уже инициализированы
    public static boolean areSharedPreferencesInitialized(Context context) {
        SharedPreferences albumPreferences = context.getSharedPreferences(ALBUM_PREFERENCES, Context.MODE_PRIVATE);
        return albumPreferences.contains(AlbumPreferences.DISPLAY_TYPE_KEY);
    }

    // Замена строки по ключу
    public void replaceString(String key, String value) {
        SharedPreferences.Editor editor = displayPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Получение строки по ключу
    public String getString(String key) {
        return displayPreferences.getString(key, null);
    }
}