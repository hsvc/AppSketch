package com.example.sp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends Activity {
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 상태바 삭제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 켜진 상태 유지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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