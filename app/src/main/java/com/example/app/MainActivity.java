package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import kr.co.bdgen.indywrapper.data.payload.OfferPayload;
import kr.co.bdgen.indywrapper.repository.CredentialRepository;
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


        //1. offer 받기(이슈잉 : 증명서를 발급하는 것)
        IssuingRepository repository = new IssuingRepository(); //레퍼지토리 객체 생성
        repository.reuqestOffer(
                secret,
                offerPayload -> {
                    Log.d("[SUCCESS!]",offerPayload.getCredDefJson() + "\n" + offerPayload.getCredOfferJson());
                    offer = offerPayload;

                    //2. request and issue credential
                    repository.requestCredential(
                            MyApplication.getWallet(),
                            MyApplication.getDid(this),
                            MyApplication.getMasterSecret(this),
                            secret,
                            offer,
                            (credentialInfo, issuePayload) -> {
                                Log.d(
                                        "[SUCCESS]",
                                        credentialInfo.getCredReqMetadataJson() +
                                                "\n" +
                                                credentialInfo.getCredReqJson() +
                                                "\n" +
                                                credentialInfo.getCredDefJson()

                                );

                                //3. store credential
                                //이슈잉을 마치고 크래딧을 만들었고,이를 저장하기 위한 지갑을 만드는 과정. sqllite에 credential이 저장됨
                                repository.storeCredential(
                                        MyApplication.getWallet(),
                                        credentialInfo,
                                        issuePayload, // 이슈잉해서 받은 정보들
                                        cred -> {
                                            Log.i("[SUCCESS]","credential = " + cred);
                                            return null;
                                        },
                                        error -> {
                                            Log.e("[ERROR!]",error.getMessage(),error);
                                            return null;
                                        }
                                );
                                return null;
                            },
                            throwable -> {
                                Log.e("[ERROR!]",throwable.getMessage(),throwable);
                                return null;
                            }
                    );

                    return null;
                },
                error -> {
                    Log.e("[ERROR!]",error.getMessage(),error);
                    return null;
                }
        );


    }

    /**
     * 저장한 credential 정보를 받아오기 위한 함수
     * @return credential json array를 담은 raw data
     */
    private String getCredential() {
        String credential;
        CredentialRepository credentialRepository = new CredentialRepository();
        credential = credentialRepository.getRawCredentials(
                MyApplication.getWallet(),
                "{}"
        );
        return credential;
    }
}