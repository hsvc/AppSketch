package com.example.sp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface DownloadAPI {
    @POST("/download")
    Call<ResponseBody> downloadFileWithDynamicUrlSync();
}
