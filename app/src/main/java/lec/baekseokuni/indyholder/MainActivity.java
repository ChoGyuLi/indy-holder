package lec.baekseokuni.indyholder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import lec.baekseokuni.indyholder.credential.CredentialListActivity;

public class MainActivity extends AppCompatActivity {
//    View.OnClickListener onStartDeeplink = v -> {
//        String testDeeplink = "indy://holder?secret=test1";
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testDeeplink));
//        startActivity(intent);
//        finish();
//    };

    View.OnClickListener onStartDeeplinkS = v -> {
        String testDeeplink = "indy://holder?secret=9XM6Xgqa2q";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testDeeplink));
        startActivity(intent);
        finish();
    };

    View.OnClickListener onStartDeeplinkB = v -> {
        String testDeeplink = "indy://holder?secret=N2vfEEpvNy";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testDeeplink));
        startActivity(intent);
        finish();
    };

    View.OnClickListener onStartDeeplinkT = v -> {
        String testDeeplink = "indy://holder?secret=iyRJiKVV2T";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testDeeplink));
        startActivity(intent);
        finish();
    };

    View.OnClickListener onStartDeeplinkP = v -> {
        String testDeeplink = "indy://holder?secret=CchCybr0oB";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testDeeplink));
        startActivity(intent);
        finish();
    };


    View.OnClickListener onNavToCredList = v -> {
        Intent intent = new Intent(getApplicationContext(), CredentialListActivity.class);
        startActivity(intent);
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button btnIssueTest = findViewById(R.id.btn_issue_test_cred);

        Button btnStarbucks = findViewById(R.id.btn_starbucks);
        Button btnBurgerking = findViewById(R.id.btn_burgerking);
        Button btnTwosome = findViewById(R.id.btn_twosome);
        Button btnStarpopeyes = findViewById(R.id.btn_popeyes);


        Button navCredList = findViewById(R.id.btn_nav_to_cred_list);
        btnStarbucks.setOnClickListener(onStartDeeplinkS);
        btnBurgerking.setOnClickListener(onStartDeeplinkB);
        btnTwosome.setOnClickListener(onStartDeeplinkT);
        btnStarpopeyes.setOnClickListener(onStartDeeplinkP);
        navCredList.setOnClickListener(onNavToCredList);

    }
}
