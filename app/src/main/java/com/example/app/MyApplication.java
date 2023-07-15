package com.example.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;

import java.util.concurrent.ExecutionException;

import kotlin.Pair;
import kr.co.bdgen.indywrapper.IndyWrapper;
import kr.co.bdgen.indywrapper.config.PoolConfig;
import kr.co.bdgen.indywrapper.config.WalletConfig;
// 새로 만든 MyApplication을 Application으로 확장함,

public class MyApplication extends Application {
    public static final String WALLET_PREFERENCES = "WALLET_PREFERENCES";
    public static final String PREF_KEY_DID = "PREF_KEY_DID";
    public static final String PREF_KEY_VER_KEY = "PREF_KEY_VER_KEY";
    public static final String PREF_KEY_MASTER_SECRET = "PREF_KEY_MASTER_SECRET";

    private static Wallet wallet;

    public static Wallet getWallet(){return wallet;}


    public static String getDid(Context context) {
        SharedPreferences pref = context.getSharedPreferences(WALLET_PREFERENCES, MODE_PRIVATE);
        String did =  pref.getString(PREF_KEY_DID, "");
        return did;

    }

    public static String getMasterSecret(Context context) {
        SharedPreferences pref = context.getSharedPreferences(WALLET_PREFERENCES, MODE_PRIVATE);
        String masterSecret = pref.getString(PREF_KEY_MASTER_SECRET,"");
        return masterSecret;
    }
    @Override
    public void onCreate() {
        // 앱이 실행됐을때 처음으로 호출되는 함수
        super.onCreate();

        //1. indy 라이브러리 초기화, 파라매터로는 자기 자신을 받음
        IndyWrapper.init(this);

        //2. pool 정보 설정 -> 암호화를 위한 정보가 담긴 pool, 한 번 생성하면 재활용할 수 있음 -> PoolConfig.kt (genesis 파일을 읽고 설정함)
        String poolName = PoolConfig.getPoole(this);
        try {
            //3. ledger 환경 사용을 위해 pool ledger 열기
            Pool.openPoolLedger(poolName,"{}").get();

            //4. did를 사용하기 위해 wallet 생성
            WalletConfig.createWallet(this).get();

            //5. wallet 열기
            wallet = WalletConfig.openWallet().get();

            //6. 지갑에서 아이디 카드 생성 -> did,verKey(암호화를 풀기위해 사용) 생성
            Pair<String, String> didAndVerKey = WalletConfig.createDid(wallet).get();

            //7. 나만의 키 정보 master secret id 생성 (초기 생성이기 때문에 null로 생성함)
            String masterSecret = WalletConfig.createMasterSecret(wallet,null);

            //8. 매번 생성할 수 없기 때문에 앱 내부에 저장-> 생성한 did,verKey,masterSecret 저장
            SharedPreferences prefs = getSharedPreferences(WALLET_PREFERENCES,MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_KEY_DID,didAndVerKey.getFirst());
            editor.putString(PREF_KEY_VER_KEY, didAndVerKey.getSecond());
            editor.putString(PREF_KEY_MASTER_SECRET,masterSecret);
            editor.apply();

            Toast.makeText(this,"wallet success!",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("[INDY ERROR!]",e.getMessage(),e);
        }

    }
}