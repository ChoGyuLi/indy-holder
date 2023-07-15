package com.example.app;

import android.app.Application;

import androidx.core.util.Pools;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;

import kr.co.bdgen.indywrapper.IndyWrapper;
import kr.co.bdgen.indywrapper.config.PoolConfig;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IndyWrapper.init(this);
        String poolName = PoolConfig.getPoole(this);
        try {
            Pool.openPoolLedger(poolName,"{}").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
