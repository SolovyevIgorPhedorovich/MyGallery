package com.example.mygallery.cloud;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NextCloud {

    private static final String BASE_URL = "https://nc.local/remote.php/dav/files/";
    private final String username;
    private final String password;
    private final ApiServices apiService;

    public NextCloud(String username, String password){
        this.username = username;
        this.password = password;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiServices.class);
    }

    private String getAuthHeader() {
        String credentials = username + ":" + password;
        return "Basic " + android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);
    }

    public void uploadFile(File file, UploadCallback callback) {
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("application/octet-stream"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        apiService.uploadFile(getAuthHeader(), body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(new Exception("Upload failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public Call<ResponseBody> downloadFile(String fileName) {
        return apiService.downloadFile(getAuthHeader(), fileName);
    }

    public Call<List<String>> getFile() {
        return apiService.getFiles(getAuthHeader());
    }

    public interface UploadCallback {
        void onSuccess();
        void onError(Throwable error);
    }
}
