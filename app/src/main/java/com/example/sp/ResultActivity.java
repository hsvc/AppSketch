package com.example.sp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 삭제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 켜진 상태 유지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_result);

        ImageButton Plain, Cezanne, VanGogh, Monet;
        final ImageView imageview =findViewById(R.id.main);
        Plain=findViewById(R.id.imagePlain);
        Cezanne=findViewById(R.id.imageCezanne);
        VanGogh=findViewById(R.id.imageVanGogh);
        Monet=findViewById(R.id.imageMonet);

        /* 이미지 변경되는 메서드 */
        Plain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview.setImageResource(R.drawable.p);
            }
        });
        Monet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview.setImageResource(R.drawable.m);
            }
        });
        VanGogh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview.setImageResource(R.drawable.v);
            }
        });
        Cezanne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview.setImageResource(R.drawable.c);
            }
        });
        /* 이미지 변경 끝 */

        DownloadAPI downloadService = ServiceGenerator.createService(DownloadAPI.class);
        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync("/download");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("server contacted and has file: "+response.body());

                    String writtenToDisk = writeResponseBodyToDisk(response.body());
                    System.out.println("response: "+response);

                    System.out.println("file download was a success? " + writtenToDisk);
                } else {
                    System.out.println("server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("error");
            }
        });
//        final DownloadAPI downloadService =
//                ServiceGenerator.createService(DownloadAPI.class);
//
//        Call<ResponseBody> call = downloadService. downloadFileWithDynamicUrlAsync("/download");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    System.out.println("server contacted and has file");
//
//                    new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//                            String writtenToDisk = writeResponseBodyToDisk(response.body());
//
//                            System.out.println("file download was a success? " + writtenToDisk);
//                            return null;
//                        }
//                    }.execute();
//                }
//                else {
//                    System.out.println("server contact failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("error");
//            }
//        });
    }
    private String writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "WhatsWrong.jpg");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                byte[] fileReader = new byte[1024];

                long fileSize = body.contentLength();
                //System.out.println("this is body"+body.string());
                //System.out.println(body +"type : "+ body.contentType());
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();

                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    System.out.println(fileSize);
                    //System.out.println("this is inputStream " +inputStream);
                    //System.out.println(inputStream.read());
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    System.out.println("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return "true";
            } catch (IOException e) {
                return "false IOException";
            } finally {
                if(futureStudioIconFile.exists()){
                    if(futureStudioIconFile.canRead())
                        System.out.println("this is my absolute path: "+futureStudioIconFile.getAbsolutePath());
                }
                else System.out.println("this is false");

//                Intent i =new Intent();
//                i.setType("image/*");
//                i.setData(Uri.fromFile(futureStudioIconFile));

                ImageView myImage = (ImageView) findViewById(R.id.main);

                myImage.setImageURI(Uri.fromFile(futureStudioIconFile));


//                Bitmap bm = BitmapFactory.decodeStream(futureStudioIconFile) ;
//
//                ImageView imageView = (ImageView) findViewById(R.id.main) ;
//                imageView.setImageBitmap(bm) ;
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

            }

        } catch (IOException e) {
            return "false IOE2";
        }
    }
}
