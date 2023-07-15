package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

        TextView text = (TextView) findViewById(R.id.txt_main);
        String credJsonArray = getCredential();
        text.setText(credJsonArray);

        //deeplink처리
        //deeplink = indy://holder?secret = blarblar
        //scheme = indy
        //host = holder
        //queryParameter = secret

        Uri data = getIntent().getData();
        if (data == null)
            return;

        String secret = data.getQueryParameter("secret");
        Log.d("[Success]", secret);

        //1. offer 받기
        IssuingRepository repository = new IssuingRepository();
        repository.reuqestOffer(
                secret,
                offerPayload -> {
                    Log.d("[Success]", offerPayload.getCredDefJson() + "\n" + offerPayload.getCredOfferJson());
                    offer = offerPayload;

                    //2, request and issue credential
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
                                                credentialInfo.getCredDefJson() +
                                                "\n" +
                                                issuePayload.getCredentialJson()
                                );
                                //3. state credential
                                repository.storeCredential(
                                        MyApplication.getWallet(),
                                        credentialInfo,
                                        issuePayload,
                                        cred -> {
                                            Log.i("[SUCCESS]", "credential = " + cred);
                                            return null;
                                        },
                                        error -> {
                                            Log.e("[ERROR!]", error.getMessage(), error);
                                            return null;
                                        }
                                );
                                return null;
                            },
                            throwable -> {
                                Log.e("[ERROR!]", throwable.getMessage(), throwable);
                                return null;
                            }
                    );
                    return null;
                },
                error -> {
                    Log.e("[ERROR!]", error.getMessage(), error);
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