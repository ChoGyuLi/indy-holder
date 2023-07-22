package lec.baekseokuni.indyholder.credential;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import kr.co.bdgen.indywrapper.data.Credential;
import lec.baekseokuni.indyholder.R;

public class CredentialActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_ARG_KEY_CRED = "INTENT_EXTRA_ARG_KEY_CRED";
    View.OnClickListener submit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //verify 딥링크 실행
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("indy://verify?schemaId=EtAGQxkwjMBgCkG4M6jXjP:2:use-of-multiple-containers:1.1"));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential);
        ActionBar appBar = getSupportActionBar();
        if (appBar != null) {
            appBar.setTitle("증명서 상세");
            appBar.setDisplayHomeAsUpEnabled(true);
        }
        //증명서 목록에서 받아온 상세 증명서 데이터
        Credential cred = getIntent().getParcelableExtra(INTENT_EXTRA_ARG_KEY_CRED);
        if (cred == null)
            return;
        Map<String, String> attrs = cred.getAttrs();
        if (attrs == null)
            attrs = new HashMap<>();

        TextView txt = findViewById(R.id.txt_schema_id_in_detail);
        String schemaId = cred.getSchemaId();
        if(schemaId != null) {
            if(schemaId.contains("use-of-multiple-containers")) {
                txt.setText("다회용기 사용 증명서");
            }
            else {
                txt.setText(schemaId);
            }
        }

        RecyclerView rvAttributes = findViewById(R.id.list_attribute);
        AttributeRecyclerViewAdapter adapter = new AttributeRecyclerViewAdapter(attrs);
        rvAttributes.setAdapter(adapter);

//        Button submitButton = findViewById(R.id.del_btn);
//        submitButton.setOnClickListener(submit);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}