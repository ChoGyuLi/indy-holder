package lec.baekseokuni.indyholder.credential;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bdgen.indywrapper.data.Credential;
import lec.baekseokuni.indyholder.databinding.ItemCredentialBinding;

public class CredentialRecyclerViewAdapter extends RecyclerView.Adapter<CredentialRecyclerViewAdapter.ViewHolder> {

    private final List<Credential> credentialList;

    public CredentialRecyclerViewAdapter(List<Credential> items) {
        credentialList = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCredentialBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Credential credData = credentialList.get(position);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CredentialActivity.class);
            intent.putExtra(CredentialActivity.INTENT_EXTRA_ARG_KEY_CRED, (Parcelable) credData);
            v.getContext().startActivity(intent);
        });
        holder.txtCredId.setText(credData.getId());
        holder.txtSchemaId.setText(credData.getSchemaId());
        holder.txtCredDefId.setText(credData.getCredDefId());
    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtCredId;
        public final TextView txtSchemaId;
        public final TextView txtCredDefId;

        public ViewHolder(ItemCredentialBinding binding) {
            super(binding.getRoot());
            txtCredId = binding.txtCredId;
            txtSchemaId = binding.txtSchemaId;
            txtCredDefId = binding.txtCredDefId;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + txtCredId.getText() + "'";
        }
    }
}