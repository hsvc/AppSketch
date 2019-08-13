package com.example.sp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Future;

public class MainActivity extends Activity {

    Button imgsel, upload;
    ImageView img;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img=findViewById(R.id.img);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        imgsel=findViewById(R.id.selimg);// Selected Image 관리
        upload=findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                File f = new File(path);

//                if(f.exists())
//                    System.out.println("this is a file");
//                else if(f.isDirectory())
//                    System.out.println("this is directory");
//                else
//                    System.out.println("this is not a file");

                Future uploading = Ion.with(MainActivity.this)
                        .load("http://127.0.0.1:3000/upload")
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            /* 완성되었을 때 Json 파일로 결과를 보여준다 */
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try{
                                    System.out.println("this is result "+result);/* result가 null값 찍힘 */
                                    JSONObject jobj =new JSONObject(result.getResult());
                                    Toast.makeText(getApplicationContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();
                                }catch (JSONException e1){
                                    e1.printStackTrace();
                                }
                            }
                        });
            }
        });

        /* Image를 선택해서 imageVIew에 불러옴 */
        imgsel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Intent fintent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                //fintent.setType("image/jpeg");
//                fintent.addCategory(Intent.CATEGORY_OPENABLE);
//                fintent.setType("image/jpeg");
                Intent fintent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
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
//                    System.out.println("this is uri"+data.getData());
//                    System.out.println("this is data"+data);
//                    System.out.println("this is my path ~ "+ path);
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
}
