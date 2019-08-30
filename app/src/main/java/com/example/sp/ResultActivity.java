package com.example.sp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 결과 사진이 왔을 때 이를 띄워주는 Activity */
public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 삭제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 켜진 상태 유지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_result);

        /* 각 화가에 해당하는 사진을 서버를 통해 생성된 폴더에서 가져옴 */
        /********** Monet **********/
        DownloadAPI downloadService = ServiceGenerator.createService(DownloadAPI.class);
        Call<ResponseBody> callMonet = downloadService.downloadFileWithDynamicUrlSync("/download/monet");// 생성된 파일이 있는 폴더

        callMonet.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String writtenToDisk = writeResponseBodyToDisk(response.body(), "monet");
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

        /********** Van gogh **********/
        Call<ResponseBody> callVangogh = downloadService.downloadFileWithDynamicUrlSync("/download/vangogh");

        callVangogh.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String writtenToDisk = writeResponseBodyToDisk(response.body(), "vangogh");
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

        /********** Cezanne **********/
        Call<ResponseBody> callCezanne = downloadService.downloadFileWithDynamicUrlSync("/download/cezanne");

        callCezanne.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String writtenToDisk = writeResponseBodyToDisk(response.body(), "cezanne");
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

        /********** Plain(원본사진) **********/
        Call<ResponseBody> callPlain = downloadService.downloadFileWithDynamicUrlSync("/download/plain");

        callPlain.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String writtenToDisk = writeResponseBodyToDisk(response.body(), "plain");
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
    }
    /* jpg 이미지 파일을 안드로이드에 가져온다, binary 형태로 들어옴!, 성공했을 때 return true */
    private String writeResponseBodyToDisk(ResponseBody body, String filename) {
        try {
            // 파일을 가져옴, 해당 경로를 바꿀 수도 있음 !
            final File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + filename +".jpg");
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                // binary 형태로 download
                byte[] fileReader = new byte[1024];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();

                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
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
                /* // 파일 확인
                if(futureStudioIconFile.exists()){
                    if(futureStudioIconFile.canRead())
                        System.out.println("this is my absolute path: "+futureStudioIconFile.getAbsolutePath());
                }
                else System.out.println("this is false");
                */
                final ImageButton Plain, Cezanne, VanGogh, Monet;
                final ImageView imageview = findViewById(R.id.main);
                Plain = findViewById(R.id.imagePlain);
                Cezanne = findViewById(R.id.imageCezanne);
                VanGogh = findViewById(R.id.imageVanGogh);
                Monet = findViewById(R.id.imageMonet);
                Uri monetU = null;
                Uri vangoghU = null;
                Uri cezanneU = null;
                Uri plainU = null;
                //Monet.setImageURI(Uri.fromFile(futureStudioIconFile));

                /* 각 이미지를 클릭했을 때 main에 해당 이미지가 보여지게 하기 위한 메서드 */
                if (filename =="monet"){
                    monetU=Uri.fromFile(futureStudioIconFile);
                    Monet.setImageURI(monetU);
                }else if(filename =="vangogh"){
                    vangoghU=Uri.fromFile(futureStudioIconFile);
                    VanGogh.setImageURI(vangoghU);
                }else if(filename =="cezanne"){
                    cezanneU=Uri.fromFile(futureStudioIconFile);
                    Cezanne.setImageURI(cezanneU);
                } else if(filename=="plain"){
                    plainU=Uri.fromFile(futureStudioIconFile);
                    imageview.setImageURI(plainU);
                }

                final Uri finalMonetU = monetU;
                final Uri finalVangoghU = vangoghU;
                final Uri finalCezanneU1 = cezanneU;
                final Uri finalPlainU = plainU;

                Monet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageview.setImageURI(finalMonetU);
                    }
                });
                VanGogh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageview.setImageURI(finalVangoghU);
                    }
                });
                Cezanne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageview.setImageURI(finalCezanneU1);
                    }
                });
                Plain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageview.setImageURI(finalPlainU);
                    }
                });
                /* 이미지 변경 끝 */

                //종료
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }//finally

        } catch (IOException e) {
            return "false IOE2";
        }
    }
}
