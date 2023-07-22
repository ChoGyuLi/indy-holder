package lec.baekseokuni.indyholder.issue;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.bdgen.indywrapper.data.argument.CredentialInfo;
import kr.co.bdgen.indywrapper.data.payload.IssuePayload;
import kr.co.bdgen.indywrapper.data.payload.OfferPayload;
import kr.co.bdgen.indywrapper.repository.IssueRepository;
import lec.baekseokuni.indyholder.MainActivity;
import lec.baekseokuni.indyholder.MyApplication;
import lec.baekseokuni.indyholder.R;

public class IssueActivity extends AppCompatActivity {
    private final IssueRepository repository = new IssueRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        ActionBar appBar = getSupportActionBar();
        if (appBar != null) {
            appBar.setTitle("증명서 발급");
            appBar.setDisplayHomeAsUpEnabled(true);
        }

        //deeplink 처리
        //deeplink = indy://holder?secret=blarblar
        // scheme = indy
        // host = holder
        // queryParameter = secret
        Uri data = getIntent().getData();
        if (data == null)
            return;
        String secret = data.getQueryParameter("secret");
        TextView txtIssueSecret = findViewById(R.id.txt_issue_secret);
        txtIssueSecret.setText(secret);
        Log.d("[SUCCESS/DEEPLINK]", secret);

        //증명서 발급 과정: offer -> issue -> store
        getOffer(secret);
    }

    private void getOffer(String secret) {
        //1. offer 받기
        repository.requestOffer(
                secret,
                offerPayload -> {
                    //응답 성공 시
                    issueCredential(secret, offerPayload);
                    runOnUiThread(() -> {
                        TextView txtOffer = findViewById(R.id.txt_issue_offer);
                        txtOffer.setText(offerPayload.toString());
                    });
                    Log.d("[SUCCESS/OFFER]", "credDef = " + offerPayload.getCredDefJson() + "\n" + "offer = " + offerPayload.getCredOfferJson());
                    return null;
                },
                error -> {
                    //응답 실패 시
                    Log.e("[ERROR!/OFFER]", error.getMessage(), error);
                    return null;
                }
        );
    }

    private void issueCredential(String secret, OfferPayload offerPayload) {
        //2. request and issue credential
        repository.requestCredential(
                MyApplication.getWallet(),
                MyApplication.getDid(this),
                MyApplication.getMasterSecret(this),
                secret,
                offerPayload,
                (credentialInfo, issuePayload) -> {
                    //발급 성공 시
                    DialogInterface.OnClickListener onClickPositive = (dialog, which) -> {
                        storeCredential(credentialInfo, issuePayload);
                    };
                    AlertDialog.Builder alert = new AlertDialog.Builder(this)
                            .setTitle("증명서 발급")
                            .setCancelable(false)
                            .setMessage("발급한 증명서를 저장하시겠습니까?")
                            .setPositiveButton("증명서 저장", onClickPositive)
                            .setNegativeButton("취소", null);
                    runOnUiThread(() -> {
                        TextView txtCredReq = findViewById(R.id.txt_issue_credReq);
                        TextView txtIssue = findViewById(R.id.txt_issue);
                        txtCredReq.setText(credentialInfo.toString());
                        txtIssue.setText(issuePayload.toString());
                        alert.create().show();
                    });
                    Log.d("[SUCCESS/ISSUE]", "credReqMeta = " + credentialInfo.getCredReqMetadataJson() + "\n" + "credReq = " + credentialInfo.getCredReqJson() + "\n" + "credDef = " + credentialInfo.getCredDefJson() + "\n" + "cred = " + issuePayload.getCredentialJson());
                    return null;
                },
                error -> {
                    //발급 실패 시
                    Log.e("[ERROR!/ISSUE]", error.getMessage(), error);
                    return null;
                }
        );
    }

    private void storeCredential(CredentialInfo credentialInfo, IssuePayload issuePayload) {
        //3. store credential
        repository.storeCredential(
                MyApplication.getWallet(),
                credentialInfo,
                issuePayload,
                cred -> {
                    //저장 성공 시
                    runOnUiThread(() -> {
                        TextView txtCredId = findViewById(R.id.txt_issue_cred_id);
                        txtCredId.setText(cred);
                        navigateToMain();
                    });
                    Log.i("[SUCCESS/STORE]", "credId(referent) = " + cred);
                    return null;
                },
                error -> {
                    //저장 실패 시
                    Log.e("[ERROR!/STORE]", error.getMessage(), error);
                    return null;
                }
        );
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navigateToMain();
        return super.onOptionsItemSelected(item);
    }
}