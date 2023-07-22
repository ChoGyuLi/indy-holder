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
import java.util.Map;

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
        Map<String,String> attributes = credData.getAttrs();

        String userName = attributes.get("name");
        holder.userName.setText(userName);

        String accountNumber = attributes.get("account_number");
        holder.accountNumber.setText(accountNumber);

        String companyRegistrationNumber = attributes.get("company_registration_number");
        holder.companyRegistrationNumber.setText(companyRegistrationNumber);

        String packagingDate = attributes.get("packaging_date");
        holder.packagingDate.setText(packagingDate);

        String reusableCup = attributes.get("reusable_cup");
        holder.reusableCup.setText(reusableCup);

        String reusableContainer = attributes.get("reusable_container");
        holder.reusableContainer.setText(reusableContainer);

    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView userName;
        public final TextView accountNumber;
        public final TextView companyRegistrationNumber;
        public final TextView packagingDate;
        public final TextView reusableCup;
        public final TextView reusableContainer;
        public ViewHolder(ItemCredentialBinding binding) {
            super(binding.getRoot());
            userName = binding.userName;
            accountNumber = binding.accountNumber;
            companyRegistrationNumber = binding.companyRegistrationNumber;
            packagingDate = binding.packagingDate;
            reusableCup = binding.reusableCup;
            reusableContainer = binding.reusableContainer;

        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + userName.getText() + "'";
        }
    }
}