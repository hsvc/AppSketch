package com.example.sp;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static final String BASE_URL = "http://101.101.160.73:3000/";
    //private static final String BASE_URL = "http://10.15.73.232:3000/";

    private static Retrofit retrofit;
    public static Retrofit getRetrofitClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(1, TimeUnit.MINUTES)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}