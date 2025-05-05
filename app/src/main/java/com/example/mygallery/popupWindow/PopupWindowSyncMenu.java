package com.example.mygallery.popupWindow;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumSelected;
import com.example.mygallery.cloud.NextCloud;
import com.example.mygallery.managers.PopupWindowManager;
import com.example.mygallery.navigator.ActivityNavigator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopupWindowSyncMenu extends PopupWindowManager {

    private static final int DEFAULT_X_POSITION = 40;
    private static final int DEFAULT_Y_POSITION = 80;
    private static final String TAG = "PopupWindowSyncMenu";

    private BaseViewModel<ImageModel> viewModel;

    public PopupWindowSyncMenu(Context context) {
        super(context);
    }

    @Override
    protected boolean setConfiguration() {
        return false;
    }

    @Override
    protected void setAnimation() {}

    public static void run(Context context, View anchorView, BaseViewModel<ImageModel> viewModel) {
        PopupWindowSyncMenu popupWindow = new PopupWindowSyncMenu(context);
        popupWindow.setViewModel(viewModel);
        initializePopupWindow(context, popupWindow, anchorView);
    }

    public void setViewModel(BaseViewModel<ImageModel> viewModel) {
        this.viewModel = viewModel;
    }

    private static void initializePopupWindow(Context context, PopupWindowManager popupWindow, View anchorView) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View menuView = inflater.inflate(R.layout.popup_window_share, null);

        popupWindow.setContent(menuView);
        popupWindow.setPosition(DEFAULT_X_POSITION, DEFAULT_Y_POSITION);
        popupWindow.showPopupWindow(anchorView, menuView, Gravity.TOP | Gravity.END);
    }

    public void uploadSelectedFiles(Context context, BaseViewModel<? extends Model> viewModel) {
        SharedPreferences prefs = getEncryptedPrefs(context);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Выполните вход", Toast.LENGTH_SHORT).show();
            return;
        }

        List<? extends Model> selectedItems = viewModel.getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(context, "Нет выбранных файлов", Toast.LENGTH_SHORT).show();
            return;
        }

        NextCloud cloud = new NextCloud(username, password);

        for (Model item : selectedItems) {
            File file = item.getPath();

            cloud.uploadFile(file, new NextCloud.UploadCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Загружено: " + file.getName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable error) {
                    Toast.makeText(context, "Ошибка загрузки: " + file.getName(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Upload error", error);
                }
            });
        }
    }

    public void downloadFilesFromCloud(Context context, List<String> fileNames) {
        SharedPreferences prefs = getEncryptedPrefs(context);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Сначала выполните вход", Toast.LENGTH_SHORT).show();
            return;
        }

        NextCloud cloud = new NextCloud(username, password);

        for (String fileName : fileNames) {
            Call<ResponseBody> call = cloud.downloadFile(fileName);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean written = writeToDisk(response.body(), fileName, context);
                        Toast.makeText(context, (written ? "Скачано: " : "Ошибка записи: ") + fileName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Ошибка скачивания: " + fileName, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Ошибка сети: " + fileName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean writeToDisk(ResponseBody body, String fileName, Context context) {
        try {
            File outputDir = new File(context.getExternalFilesDir(null), "Downloads");
            if (!outputDir.exists()) outputDir.mkdirs();

            File file = new File(outputDir, fileName);
            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Write error", e);
            return false;
        }
    }

    private SharedPreferences getEncryptedPrefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации защищённых настроек", e);
        }
    }

    @Override
    protected void handleButtonClick(int buttonId) {
        popupWindow.dismiss();

        if (buttonId == R.id.button_upload) {
            ActivityNavigator activityNavigator = new ActivityNavigator(context);
            activityNavigator.navigateToActivity(AlbumSelected.class);
            uploadSelectedFiles(context);
        } else if (buttonId == R.id.button_load) {
            List<String> files = Arrays.asList("example1.txt", "photo.jpg");
            downloadFilesFromCloud(context, files);
        }
    }
}
