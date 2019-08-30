package com.example.sp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/* 사진 download */
public interface DownloadAPI {
@GET
Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
