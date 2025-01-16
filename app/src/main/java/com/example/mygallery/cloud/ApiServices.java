package com.example.mygallery.cloud;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiServices {

    @GET("{username}")
    Call<List<String>> getFiles(@Header("Authorization") String authHeader);

    @Multipart
    @POST("{username}/")
    Call<ResponseBody> uploadFile(@Header("Authorization") String authHeader, @Part MultipartBody.Part file);
}
