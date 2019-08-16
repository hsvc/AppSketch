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
                /* nodejs서버에 사진을 업로드 */
                uploadToServer(path);
                /* */
                Intent intent=new Intent(MainActivity.this, ResultActivity.class);
                //intent.putExtra("fileName", fileName);// 파일 명 가져감
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
                    System.out.println("this is my path ~ "+ path);
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
    private void uploadToServer(String filePath) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        Call call = uploadAPI.uploadImage(part, description);
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
