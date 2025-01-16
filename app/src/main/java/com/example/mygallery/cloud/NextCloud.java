package com.example.mygallery.cloud;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NextCloud {

    private static final String BASE_URL = "https://nc.local/remove.php/dav/files/";
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

    public Call<List<String>> getFile(){
        return apiService.getFiles(getAuthHeader());
    }

    public Call<ResponseBody> uploadImage(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        return apiService.uploadFile(getAuthHeader(), body);
    }

}
