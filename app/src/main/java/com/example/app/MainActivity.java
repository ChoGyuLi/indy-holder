package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //화면 로직(초기화 로직은 따로 빼서 만든다 -> MyApplication.java
    // -> 실행되는 파일이 MainActivity.java라서 매번 초기화 로직을 실행하는 것은 비효율적이기 때문임)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}