package com.example.sp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
        imgsel=findViewById(R.id.selimg);
        upload=findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                File f = new File(path);

                Future uploading = Ion.with(MainActivity.this)
                        .load("localhost:8080/upload")
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try{
                                    JSONObject jobj =new JSONObject(result.getResult());
                                    Toast.makeText(getApplicationContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();
                                }catch (JSONException e1){
                                    e1.printStackTrace();
                                }
                            }
                        });
            }
        });

        imgsel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent fintent=new Intent(Intent.ACTION_GET_CONTENT);
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
                    path=getPathFromURI(data.getData());
                    System.out.println("THIS IS PATH!!! : "+path);
                    img.setImageURI(data.getData());
                    upload.setVisibility(View.VISIBLE);
                }
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj={MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
