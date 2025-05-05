package com.example.mygallery.cloud;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiServices {

    @Multipart
    @POST("upload.php") // Уточни, если используется другой путь
    Call<ResponseBody> uploadFile(
            @Header("Authorization") String authHeader,
            @Part MultipartBody.Part file
    );

    @GET("{fileName}")
    Call<ResponseBody> downloadFile(
            @Header("Authorization") String authHeader,
            @Path("fileName") String fileName
    );

    @GET("list.php")
    Call<List<String>> getFiles(@Header("Authorization") String authHeader);
}
