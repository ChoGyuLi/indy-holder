package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import kr.co.bdgen.indywrapper.data.payload.OfferPayload;
import kr.co.bdgen.indywrapper.repository.IssuingRepository;

public class MainActivity extends AppCompatActivity {
    //화면 로직(초기화 로직은 따로 빼서 만든다 -> MyApplication.java
    // -> 실행되는 파일이 MainActivity.java라서 매번 초기화 로직을 실행하는 것은 비효율적이기 때문임)
    private OfferPayload offer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deeplink 처리
        //deeplink = indy://holder?secret=blarblar
        //scheme = indy
        //host = holder
        //queryParameter = secret
        Uri data = getIntent().getData();//MainActivity-Manifest에서 intent의 <data>를 가져옴

        if(data == null)
            return;

        //deeplink에서 받아오는 값인 secret은 제공되는 api와 통함
        String secret = data.getQueryParameter("secret");
        Log.d("[SUCCESS]",secret);


        //1. offer 받기(이슈잉 ?)
        IssuingRepository repository = new IssuingRepository(); //레퍼지토리 객체 생성
        repository.reuqestOffer( //시크릿 값, 성공했을 경우 실행될 내용, 실패했을 때 실행될 내용 선언
                secret,
                offerPayload -> {
                    Log.d("[SUCCESS!]",offerPayload.getCredDefJson() + "\n" + offerPayload.getCredOfferJson());
                    offer = offerPayload;
                    return null;
                },
                error -> {
                    Log.e("[ERROR!]",error.getMessage(),error);
                    return null;
                }
        );

        //2. request and issue credential


        //3.

    }
}