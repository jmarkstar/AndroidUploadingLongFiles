package com.jmarkstar.uploading.api;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by jmarkstar on 30/09/2017.
 */

public interface ApiService {
    @Multipart
    @POST("upload/")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part file);
}
