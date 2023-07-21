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
    View.OnClickListener onStartDeeplink = v -> {
        String testDeeplink = "indy://holder?secret=test1";
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
    }
}
