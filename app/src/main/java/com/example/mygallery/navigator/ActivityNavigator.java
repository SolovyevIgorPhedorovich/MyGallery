package com.example.mygallery.navigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ActivityNavigator {

    private final Context context;

    public ActivityNavigator(Context context) {
        this.context = context;
    }

    public void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    public void navigateToActivityWithExtras(Class<?> activityClass, Bundle extras) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

}
