package com.example.backgroundeditor;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "my_shared_preff";
    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context mCtx){
        this.mCtx= mCtx;

    }

    public static  synchronized SharedPrefManager getInstance(Context mCtx)
    {
        if(mInstance==null){
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }

    public void guardarConfig(String ruta){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("ruta",ruta);

        editor.apply();

    }

    public boolean estaConfigurado(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getString("ruta", "rutaDefecto") != "rutaDefecto")
            return true;

        return false;
    }

    public String getRuta(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String ruta = sharedPreferences.getString("ruta", "rutaDefecto");

        return ruta;

    }



    public void clear(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
