package com.example.app;

import android.app.Application;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;

import java.util.concurrent.ExecutionException;

import kr.co.bdgen.indywrapper.IndyWrapper;
import kr.co.bdgen.indywrapper.config.PoolConfig;
// 새로 만든 MyApplication을 Application으로 확장함,

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        // 앱이 실행됐을때 처음으로 호출되는 함수
        super.onCreate();

        //indy 라이브러리를 초기화, 파라매터로는 자기 자신을 받음
        IndyWrapper.init(this);

        //pool 정보 설정 -> 암호화를 위한 정보가 담긴 pool, 한 번 생성하면 재활용할 수 있음 -> PoolConfig.kt (genesis 파일을 읽고 설정함)
        String poolName = PoolConfig.getPoole(this);
        try {
            Pool.openPoolLedger(poolName,"{}").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
