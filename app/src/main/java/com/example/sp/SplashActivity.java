package com.example.sp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd=new Handler();
        hd.postDelayed(new splashhandler(), 3000); // delay 시간 3초
    }

    private class splashhandler implements Runnable{
        public void run(){
            // 로딩이 끝난 후 이동
            startActivity(new Intent(getApplication(), MainActivity.class));
            SplashActivity.this.finish(); // stack에서 제거
        }
    }
}