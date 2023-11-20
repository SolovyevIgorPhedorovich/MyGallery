package com.example.mygallery.editor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import com.example.mygallery.sharedpreferences.SharedPreferencesHelper;
import com.example.mygallery.sharedpreferences.values.SettingPreferences;

import java.util.List;

public class FileEditor {
    protected enum Type {IMAGE, VIDEO}
    protected List<ResolveInfo> editors;
    protected final Intent editIntent;
    private final Context context;
    private final Type type;
    private final PackageManager packageManager;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    protected FileEditor(Context context, Type type){
        this.context = context;
        this.type = type;
        this.packageManager = context.getPackageManager();
        this.editIntent = new Intent(Intent.ACTION_EDIT);
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context, SharedPreferencesHelper.SETTING_PREFERENCES);
    }

    protected void getEditors(){
        editors = packageManager.queryIntentActivities(editIntent,0);
    }

    protected void startEditor(View view){
        String key = null;
        switch (type){
            case IMAGE:
                key = SettingPreferences.DEFAULT_APP_EDIT_IMAGE_KEY;
                break;
            case VIDEO:
                key = SettingPreferences.DEFAULT_APP_EDIT_VIDEO_KEY;
        }
        if (sharedPreferencesHelper.getString(key) == null){
            getEditors();
            AppChooserPopupWindow.show(context, view, editors, packageManager, (p, b) -> startApp(getPackageName(p, b)));
        }
        else{
            startApp(getPackageName(key));
        }
    }

    private String getPackageName(String key){
        return sharedPreferencesHelper.getString(key);
    }

    private String getPackageName(int position, boolean isDefault){
        String packageName = editors.get(position).activityInfo.packageName;
        if (isDefault) setDefaultApp(packageName);
        return packageName;
    }

    private void setDefaultApp(String values){
        String key = null;
        switch (type){
            case IMAGE:
                key = SettingPreferences.DEFAULT_APP_EDIT_IMAGE_KEY;
                break;
            case VIDEO:
                key = SettingPreferences.DEFAULT_APP_EDIT_VIDEO_KEY;
                break;
        }
        if (key != null) {
            sharedPreferencesHelper.replaceString(key, values);
        }
    }

    private void startApp(String packageName){
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        editIntent.setPackage(packageName); //TODO: добавить обработку, ситуации если приложение по умолчанию было удалено с устройства
        context.startActivity(editIntent);
    }
}
