package lec.baekseokuni.indyholder.credential;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;
import java.util.Map;

import kr.co.bdgen.indywrapper.data.Credential;
import lec.baekseokuni.indyholder.databinding.ItemCredentialBinding;

public class CredentialRecyclerViewAdapter extends RecyclerView.Adapter<CredentialRecyclerViewAdapter.ViewHolder> {

    private List<Credential> credentialList;
    @Nullable
    private Consumer<Credential> onDeleteCred;

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
        holder.binding.btnDeleteCred.setOnClickListener(v -> {
            if (onDeleteCred == null)
                return;
            onDeleteCred.accept(credData);
        });
        Map<String,String> attributes = credData.getAttrs();

        String userName = attributes.get("name");
        holder.binding.userName.setText(userName);

        String accountNumber = attributes.get("account_number");
        holder.binding.accountNumber.setText(accountNumber);

        String companyRegistrationNumber = attributes.get("company_registration_number");
        holder.binding.companyRegistrationNumber.setText(companyRegistrationNumber);

        String packagingDate = attributes.get("packaging_date");
        holder.binding.packagingDate.setText(packagingDate);

        String reusableCup = attributes.get("reusable_cup");
        holder.binding.reusableCup.setText(reusableCup);

        String reusableContainer = attributes.get("reusable_container");
        holder.binding.reusableContainer.setText(reusableContainer);

    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    public void setCredentialList(List<Credential> credentialList) {
        this.credentialList = credentialList;
        notifyDataSetChanged();
    }

    public void setOnDeleteCred(@Nullable Consumer<Credential> onDeleteCred) {
        this.onDeleteCred = onDeleteCred;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemCredentialBinding binding;

        public ViewHolder(ItemCredentialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}