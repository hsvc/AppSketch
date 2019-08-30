package com.example.sp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/* Splash가 꺼진 후 첫 Activity, 사진을 선택하여 변경하는 버튼을 누르는 행동을 위한 Activity */
public class MainActivity extends Activity {

    Button imgsel, upload;
    ImageView img;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 삭제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 켜진 상태 유지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        img=findViewById(R.id.img);
        imgsel=findViewById(R.id.selimg);// Selected Image 관리

        upload=findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /* 어플리케이션 자체의 저장공간 권한을 줬을 때 이미지에 permission이 주어짐 */

                uploadToServer(path);// node js 서버에 사진을 업로드

                try {
                    Thread.sleep(500000); // 업로드 후 이미지 변경에 시간이 걸리기 때문에, 이를 위한 sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /* 서버에 사진을 업로드 한 후 모델에 따른 결과 사진이 나왔을 때 Intent를 통해 결과 activity로 간다. */
                Intent intent=new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        });

        /* Image를 선택해서 imageVIew에 불러옴 */
        imgsel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent fintent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                fintent.setType("image/jpeg");

                try{
                    startActivityForResult(fintent, 100);
                }catch(ActivityNotFoundException e){

                }
            }
        });
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data==null) return;
        switch (requestCode){
            case 100:
                if (resultCode == RESULT_OK){
                    path= getPathFromUri(getApplicationContext(), data.getData());
                    //System.out.println("this is my path ~ "+ path);
                    img.setImageURI(data.getData());
                    upload.setVisibility(View.VISIBLE);
                }
        }
    }

    public String getPathFromUri(Context context, Uri contentUri){

        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
    /* 서버에 사진을 업로드 할 수 있는 메소드 */
    private void uploadToServer(String filePath) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
        // 어플리케이션에 있는 file의 path를 가져와야 함, 해당 사진을 서버에 올려 변경시켜줘야 하기 때문에 !
        // 정확한 Path를 가져오는 방법을 알고 있는 것이 매우 중요함 !!!
        File file = new File(filePath);

        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        Call call = uploadAPI.uploadImage(part, description);// 이미지 upload Post 보냄

        /* 서버의 응답 */
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("failed "+t);
            }
        });
    }
}
