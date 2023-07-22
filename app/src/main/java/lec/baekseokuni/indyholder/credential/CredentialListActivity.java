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
                    .setMessage("증명서 삭제?")
                    .setCancelable(false)
                    .setPositiveButton("확인", (dialog, which) -> {
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