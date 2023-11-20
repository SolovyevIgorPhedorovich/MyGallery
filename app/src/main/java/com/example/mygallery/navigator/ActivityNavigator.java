package com.example.mygallery.navigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ActivityNavigator {

    private final Context context;

    private Intent intent;

    public ActivityNavigator(Context context) {
        this.context = context;
    }

    private void setIntent(Class<?> activityClass) {
        intent = new Intent(context, activityClass);
    }

    public void navigateToActivity(Class<?> activityClass) {
        setIntent(activityClass);
        context.startActivity(intent);
    }

    public void navigateToActivityWithExtras(Class<?> activityClass, Bundle extras) {
        setIntent(activityClass);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public void navigateToActivityForResult(Class<?> activityClass, ActivityResultLauncher<Intent> listener) {
        setIntent(activityClass);
        listener.launch(intent);
    }

}
