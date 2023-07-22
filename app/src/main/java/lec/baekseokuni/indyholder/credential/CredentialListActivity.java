package lec.baekseokuni.indyholder.credential;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bdgen.indywrapper.data.Credential;
import kr.co.bdgen.indywrapper.repository.CredentialRepository;
import lec.baekseokuni.indyholder.MyApplication;
import lec.baekseokuni.indyholder.R;

public class CredentialListActivity extends AppCompatActivity {
    private final CredentialRepository repository = new CredentialRepository();
    private final CredentialRecyclerViewAdapter adapter = new CredentialRecyclerViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential_list);
        ActionBar appBar = getSupportActionBar();
        if (appBar != null) {
            appBar.setTitle("증명서 목록");
            appBar.setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView rvCredList = findViewById(R.id.list_credential);

        adapter.setOnDeleteCred(credential -> {
            new AlertDialog.Builder(this)
                    .setTitle("증명서 삭제")
                    .setMessage("증명서를 삭제하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("삭제", (dialog, which) -> {
                        // 삭제 버튼을 눌렀을 때 처리하는 부분
                        repository.deleteCredential(
                                MyApplication.getWallet(),
                                credential.getId(),
                                () -> {
                                    updateCredentialList();
                                },
                                (t) -> {
                                    t.printStackTrace();
                                }
                        );
                    })
                    .setNegativeButton("취소", (dialog, which) -> {
                        // 취소 버튼을 눌렀을 때 처리하는 부분
                        dialog.dismiss(); // 대화상자를 닫음
                    })
                    .show();
        });
        rvCredList.setAdapter(adapter);
        updateCredentialList();
    }

    private void updateCredentialList() {
        List<Credential> credentialList = repository.getCredentialList(MyApplication.getWallet(), null);
        adapter.setCredentialList(credentialList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}