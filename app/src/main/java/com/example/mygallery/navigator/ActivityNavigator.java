package com.example.mygallery.navigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;

public class ActivityNavigator {

    private final Context context;

    private Intent intent;

    public ActivityNavigator(Context context) {
        this.context = context;
    }

    // Метод для установки класса активности
    private void setIntent(Class<?> activityClass) {
        intent = new Intent(context, activityClass);
    }

    // Метод для навигации к другой активности
    public void navigateToActivity(Class<?> activityClass) {
        setIntent(activityClass);
        context.startActivity(intent);
    }

    // Метод для навигации к другой активности с передачей дополнительных данных
    public void navigateToActivityWithExtras(Class<?> activityClass, Bundle extras) {
        setIntent(activityClass);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    // Метод для навигации к другой активности с передачей результата
    public void navigateToActivityForResult(Class<?> activityClass, ActivityResultLauncher<Intent> listener) {
        setIntent(activityClass);
        listener.launch(intent);
    }
}