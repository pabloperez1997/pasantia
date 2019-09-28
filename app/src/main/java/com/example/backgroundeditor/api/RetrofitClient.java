package com.example.backgroundeditor.api;


import androidx.appcompat.app.AppCompatActivity;

import com.example.backgroundeditor.SharedPrefManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient extends AppCompatActivity {

    //private static final String BASE_URL = "https://backgroundeditor.herokuapp.com/public/";
    //private static final String BASE_URL = "http://192.168.1.5/Pasantia/public/";

    private String BASE_URL= this.getRuta();


    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance==null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);

    }

    private String getRuta(){
        return SharedPrefManager.getInstance(this).getRuta();
    }

}
