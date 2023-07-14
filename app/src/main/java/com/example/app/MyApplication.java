package com.example.app;

import android.app.Application;

import kr.co.bdgen.indywrapper.IndyWrapper;
// 새로 만든 MyApplication을 Application으로 확장함,

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        // 앱이 실행됐을때 처음으로 호출되는 함수
        super.onCreate();

        //indy 라이브러리를 초기화, 파라매터는 자기 자신을 받음
        IndyWrapper.init(this);
    }
}
