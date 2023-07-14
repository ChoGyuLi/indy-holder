package com.example.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;

import kr.co.bdgen.indywrapper.IndyWrapper;
import kr.co.bdgen.indywrapper.config.PoolConfig;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IndyWrapper.init(this);
        String poolName = PoolConfig.getPoole(this);
        try {
            Pool.openPoolLedger(poolName, "{}").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}