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
    protected Type type;
    private final Context context;
    private final PackageManager packageManager;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    public FileEditor(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context, SharedPreferencesHelper.SETTING_PREFERENCES);
        this.editIntent = new Intent(Intent.ACTION_EDIT);
    }

    protected void getEditors() {
        editors = packageManager.queryIntentActivities(editIntent, 0);
    }

    protected void startEditor(View view) {
        String key = getSettingKey();
        if (sharedPreferencesHelper.getString(key) == null) {
            getEditors();
            AppChooserPopupWindow.show(context, view, editors, packageManager, (position, isDefault) -> startApp(getPackageName(position, isDefault)));
        } else {
            startApp(getPackageName(key));
        }
    }

    private String getSettingKey() {
        switch (type) {
            case IMAGE:
                return SettingPreferences.DEFAULT_APP_EDIT_IMAGE_KEY;
            case VIDEO:
                return SettingPreferences.DEFAULT_APP_EDIT_VIDEO_KEY;
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private String getPackageName(String key) {
        return sharedPreferencesHelper.getString(key);
    }

    private String getPackageName(int position, boolean isDefault) {
        String packageName = editors.get(position).activityInfo.packageName;
        if (isDefault) setDefaultApp(packageName);
        return packageName;
    }

    private void setDefaultApp(String values) {
        String key = getSettingKey();
        if (key != null) {
            sharedPreferencesHelper.replaceString(key, values);
        }
    }

    private void startApp(String packageName) {
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        editIntent.setPackage(packageName);
        context.startActivity(editIntent);
    }
}