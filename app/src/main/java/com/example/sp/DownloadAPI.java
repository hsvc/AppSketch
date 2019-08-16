package com.example.sp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadAPI {
//    @Streaming
//    @POST("/download")
//    Call<ResponseBody> downloadFileWithDynamicUrlSync();
//@Streaming
@GET
Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
