package com.example.sp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.Future;

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
        setContentView(R.layout.activity_main);
        img=findViewById(R.id.img);
        imgsel=findViewById(R.id.selimg);// Selected Image 관리

        //FileUpload upload=ServiceGenerator.createService(FileUpload.class);



        upload=findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                uploadToServer(path);
            }
        });
//        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
//        upload=findViewById(R.id.uploadimg);
//        upload.setVisibility(View.INVISIBLE);
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v){
//                File f = new File(path);
//                //System.out.println("this is 실행?");
//                Future uploading = Ion.with(MainActivity.this)
//                        .load("POST", "http://101.101.160.73:3000/upload")
////                        .setLogging("UPLOAD LOGS:", Log.DEBUG)
//                        .uploadProgressHandler(new ProgressCallback() {
//                            @Override
//                            public void onProgress(long uploaded, long total) {
//                                System.out.print("progress "+uploaded);
//                            }
//                        })
//                        .setMultipartFile("image","image/jpeg", f)//이미지 전송에서 에러가 발생함
//                        .asString()
//                        .setCallback(new FutureCallback<String>() {
//                            @Override
//                            public void onCompleted(Exception e, String result) {
//                                //dismissDialog();
//                                if(result!=null){
//                                    System.out.println("Success");
//                                }
//
//                            }
//                        });
////                        .asString()
////                        .withResponse()
////                        .setCallback(new FutureCallback<Response<String>>() {
////                            /* 완성되었을 때 Json 파일로 결과를 보여준다 */
////                            @Override
////                            public void onCompleted(Exception e, Response<String> result) {
////                                try{
////                                    System.out.println("this is result "+result);/* result가 null값 찍힘 */
////                                    System.out.println("this is result.getResult() "+result.getResult());
////                                    JSONObject jobj =new JSONObject(result.getResult());
////                                    Toast.makeText(getApplicationContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();
////                                }catch (JSONException e1){
////                                    e1.printStackTrace();
////                                }
////                            }
////                        });
//            }
//        });



        /* Image를 선택해서 imageVIew에 불러옴 */
        imgsel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Intent fintent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                //fintent.setType("image/jpeg");
//                fintent.addCategory(Intent.CATEGORY_OPENABLE);
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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
//        if(file.exists())
//            System.out.println("파일이 존재합니다.");
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
                System.out.println("hello");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("failed");
                System.out.println(t);
            }
        });
    }
}
