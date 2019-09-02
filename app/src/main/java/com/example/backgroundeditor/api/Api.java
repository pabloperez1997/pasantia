package com.example.backgroundeditor.api;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface Api {

    @Multipart
    @POST("nuevafoto")
    Call<ResponseBody> nuevaFoto(@Part MultipartBody.Part file,
                                     @Part("email") RequestBody email);

}
