package com.example.mygallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.constructors.CartConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCart extends DatabaseManager {
    private static final String TABLE_NAME_CART = "cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CURRENT_PATH = "current_path";
    private static final String COLUMN_INITIAL_PATH = "initial_path";
    private static final String COLUMN_DELETION_DATE = "deletion_date";
    private final String cartDirectory;

    public DatabaseCart(Context context) {
        super(context);
        // Инициализация директории для корзины
        this.cartDirectory = context.getFilesDir() + "/Корзина/";
    }

    // Получение элементов из корзины
    public List<Model> getCartItems() {
        List<Model> cartItems = new ArrayList<>();
        try {
            if (openOrInitializeDatabase()) {
                // Выполнение запроса к базе данных для получения данных
                Cursor cursor = mDataBase.rawQuery("SELECT * FROM " + TABLE_NAME_CART, null);
                if (cursor != null) {
                    // Обработка результатов запроса
                    while (cursor.moveToNext()) {
                        cartItems.add(createModelFromCursor(cursor));
                    }
                    cursor.close();
                }
            }
        } finally {
            // Закрытие базы данных в блоке finally для гарантированного выполнения
            close();
        }
        return cartItems;
    }

    // Удаление файлов из корзины
    public void removeFiles(List<Model> models) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (Model model : models) {
                    removeFile(model.getPath());
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    // Удаление файла из корзины
    public void removeFile(File initialPath) {
        try {
            if (openOrInitializeDatabase()) {
                remove(initialPath);
            }
        } finally {
            close();
        }
    }

    // Добавление файла в корзину
    public void addToCart(File initialPath) {
        try {
            if (openOrInitializeDatabase()) {
                add(initialPath);
            }
        } finally {
            close();
        }
    }

    // Добавление файлов в корзину
    public void addToCart(List<Model> models) {
        try {
            if (openOrInitializeDatabase()) {
                mDataBase.beginTransaction();
                for (Model model : models) {
                    add(model.getPath());
                }
                mDataBase.setTransactionSuccessful();
            }
        } finally {
            mDataBase.endTransaction();
            close();
        }
    }

    // Создание объекта Model на основе данных из курсора
    private Model createModelFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        File currentPath = new File(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PATH)));
        File initialPath = new File(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INITIAL_PATH)));
        int deletionDate = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DELETION_DATE));
        String name = currentPath.getName();
        return CartConstructor.create(id, name, currentPath, initialPath, deletionDate);
    }

    // Удаление файла из базы данных
    private void remove(File initialPath) {
        mDataBase.delete(TABLE_NAME_CART, COLUMN_CURRENT_PATH + "=?", new String[]{String.valueOf(initialPath)});
    }

    // Добавление файла в базу данных
    private void add(File initialPath) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_PATH, cartDirectory + initialPath.hashCode() + getFormat(initialPath.getName()));
        values.put(COLUMN_INITIAL_PATH, initialPath.getAbsolutePath());
        values.put(COLUMN_DELETION_DATE, System.currentTimeMillis());
        mDataBase.insert(TABLE_NAME_CART, null, values);
    }

    // Получение формата файла
    private String getFormat(String oldName) {
        String[] parts = oldName.split("\\.");
        return "." + parts[parts.length - 1];
    }
}