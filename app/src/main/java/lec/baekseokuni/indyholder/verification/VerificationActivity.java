package lec.baekseokuni.indyholder.verification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.hyperledger.indy.sdk.anoncreds.Anoncreds;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import kr.co.bdgen.indywrapper.AppExecutors;
import kr.co.bdgen.indywrapper.api.VerifyApi;
import kr.co.bdgen.indywrapper.config.RetrofitConfig;
import kr.co.bdgen.indywrapper.data.Credential;
import kr.co.bdgen.indywrapper.data.CredentialSearchItem;
import kr.co.bdgen.indywrapper.data.argument.CredentialVerifyPostVerifyProofArgs;
import kr.co.bdgen.indywrapper.data.payload.ProofRequest;
import kr.co.bdgen.indywrapper.data.payload.RequestedAttribute;
import lec.baekseokuni.indyholder.MyApplication;
import lec.baekseokuni.indyholder.R;
import lec.baekseokuni.indyholder.databinding.ActivityVerificationBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private final VerifyApi verifyApi = RetrofitConfig.INSTANCE.createApi(RetrofitConfig.issuerBaseUrl, VerifyApi.class);
    private final static String ARGS_SCHEMA_ID = "ARGS_SCHEMA_ID";
    private ActivityVerificationBinding binding;
    private List<RequestedAttributeSearchedItem> requestedAttributeSearchedItemList = new ArrayList<>();

    public static Intent createStartIntent(Context context, String schemaId) {
        return new Intent(context, VerificationActivity.class)
                .putExtra(ARGS_SCHEMA_ID, schemaId);
    }

    private final Gson gson = new GsonBuilder()
            .create();
    private final VerificationRecyclerViewAdapter adapter = new VerificationRecyclerViewAdapter();

    private String schemaId;

    @Nullable
    private ProofRequest proofRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSubmit.setOnClickListener(v -> {
            verify();
        });

        Intent intent = getIntent();
        String schemaId;
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            schemaId = intent.getData().getQueryParameter("schemaId");
        } else {
            schemaId = intent.getStringExtra(ARGS_SCHEMA_ID);
        }

        this.schemaId = schemaId;

        if (savedInstanceState != null)
            return;
        binding.listRequestedAttribute.setAdapter(adapter);
        createProof(this.schemaId);
    }

    private void createProof(String schemaId) {
        verifyApi.postCreateProof(schemaId)
                .enqueue(new Callback<ProofRequest>() {
                    @Override
                    public void onResponse(Call<ProofRequest> call, Response<ProofRequest> response) {
                        ProofRequest body = response.body();
                        if (!response.isSuccessful() || body == null) {
                            onError(new Exception("response fail"));
                            return;
                        }
                        proofRequest = body;
                        onCreateProofSuccess(body);
                    }

                    @Override
                    public void onFailure(Call<ProofRequest> call, Throwable t) {
                        onError(t);
                    }
                });
    }

    private void onCreateProofSuccess(ProofRequest proofRequest) {
        CompletableFuture.runAsync(() -> {
                    Wallet wallet = MyApplication.getWallet();

                    String proofRequestJson = gson.toJson(proofRequest);

                    // proof 에 전달될 증명서 선택
                    try {
                        CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq
                                .open(wallet, proofRequestJson, null)
                                .join();
                        for (Map.Entry<String, RequestedAttribute> entry : proofRequest.getRequestedAttributes().entrySet()) {
                            String key = entry.getKey();
                            String join = credentialsSearch.fetchNextCredentials(key, 100).join();
                            List<CredentialSearchItem> list = gson.fromJson(join, new TypeToken<List<CredentialSearchItem>>() {
                            }.getType());

                            requestedAttributeSearchedItemList.add(
                                    new RequestedAttributeSearchedItem(
                                            key,
                                            entry.getValue(),
                                            list.stream()
                                                    .map(CredentialSearchItem::getCredential)
                                                    .collect(Collectors.toList())
                                    )
                            );
                        }
                        credentialsSearch.close();


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenRunAsync(
                        () -> {
                            adapter.setItems(requestedAttributeSearchedItemList);
                            binding.btnSubmit.setEnabled(true);
                        },
                        AppExecutors.getInstance().mainThread()
                )
                .exceptionally(e -> {
                    runOnUiThread(() -> onError(e));
                    return null;
                });
    }

    private void verify() {
        CompletableFuture.supplyAsync(() -> {
                    Wallet wallet = MyApplication.getWallet();
                    String masterSecret = MyApplication.getMasterSecret(this);
                    // proof 에 전달될 증명서 선택
                    try {

                        JSONObject requestedAttributesJo = new JSONObject();
                        List<String> schemaIds = new ArrayList<>();
                        List<String> credDefIds = new ArrayList<>();
                        for (RequestedAttributeSearchedItem item : requestedAttributeSearchedItemList) {
                            Credential selectedCredential = item.getSelectedCredential();
                            requestedAttributesJo.put(
                                    item.getRequestedAttributeKey(),
                                    new JSONObject()
                                            .put("cred_id", selectedCredential != null ? selectedCredential.getId() : null)
                                            .put("revealed", selectedCredential != null)
                            );
                            if (selectedCredential == null)
                                continue;
                            schemaIds.add(selectedCredential.getSchemaId());
                            credDefIds.add(selectedCredential.getCredDefId());
                        }

                        String credentialsJson = new JSONObject()
                                .put("self_attested_attributes", new JSONObject())
                                .put("requested_attributes", requestedAttributesJo)
                                .put("requested_predicates", new JSONObject())
                                .toString();

                        Map<String, String> schemaIdToJsonMap = execute(verifyApi.getSchemaJson(schemaIds));
                        Map<String, String> credDefIdToJsonMap = execute(verifyApi.getDefinitionJson(credDefIds));

                        JSONObject schemasMap = new JSONObject();
                        JSONObject credDefsMap = new JSONObject();
                        for (Map.Entry<String, String> entry : schemaIdToJsonMap.entrySet()) {
                            schemasMap.put(entry.getKey(), new JSONObject(entry.getValue()));
                        }
                        for (Map.Entry<String, String> entry : credDefIdToJsonMap.entrySet()) {
                            credDefsMap.put(entry.getKey(), new JSONObject(entry.getValue()));
                        }

                        String schemas = schemasMap.toString();
                        String credDefs = credDefsMap.toString();
                        String revocState = new JSONObject().toString();

                        String proofRequestJson = gson.toJson(proofRequest);
                        // proof 생성
                        String proofJson = Anoncreds.proverCreateProof(
                                wallet,
                                proofRequestJson,
                                credentialsJson,
                                masterSecret,
                                schemas,
                                credDefs,
                                revocState
                        ).get();
                        // 베리파이어에게 proof 전달
                        String revocRegDefs = new JSONObject().toString();
                        String revocRegs = new JSONObject().toString();
                        return execute(verifyApi.postVerifyProof(
                                new CredentialVerifyPostVerifyProofArgs(
                                        proofRequestJson,
                                        proofJson,
                                        schemas,
                                        credDefs,
                                        revocRegDefs,
                                        revocRegs
                                )
                        ));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAcceptAsync(
                        result -> {
                            Toast.makeText(this, "제출 결과: " + result, Toast.LENGTH_SHORT).show();
                            runOnUiThread(() -> {
                                DialogInterface.OnClickListener onClickPositive = (dialog, which) -> {
                                    finish();
                                };
                                AlertDialog.Builder alert = new AlertDialog.Builder(this)
                                        .setTitle("증명서 제출 결과")
                                        .setCancelable(false)
                                        .setMessage("기관 검증 결과: " + result)
                                        .setPositiveButton("확인", onClickPositive);
                                alert.create().show();
                            });

                        },
                        AppExecutors.getInstance().mainThread()
                )
                .exceptionally(e -> {
                    runOnUiThread(() -> onError(e));
                    return null;
                });
    }

    private void onError(Throwable e) {
        e.printStackTrace();
        binding.btnSubmit.setEnabled(false);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private <T> T execute(Call<T> call) throws Exception {
        Response<T> response = call.execute();
        T body = response.body();
        if (!response.isSuccessful() || body == null) {
            throw new Exception("response fail");
        }
        return body;
    }
}