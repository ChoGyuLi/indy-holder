package lec.baekseokuni.indyholder.verification;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import kr.co.bdgen.indywrapper.data.Credential;
import lec.baekseokuni.indyholder.databinding.ItemRequestedAttributeBinding;

public class VerificationRecyclerViewAdapter extends RecyclerView.Adapter<VerificationRecyclerViewAdapter.ViewHolder> {
    private List<RequestedAttributeSearchedItem> items = Collections.emptyList();

    public VerificationRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRequestedAttributeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestedAttributeSearchedItem item = items.get(position);
        holder.binding.txtAttributeName.setText(item.getRequestedAttribute().getName());
        Credential credential = item.getSelectedCredential();
        Map<String, String> attrs = credential != null ? credential.getAttrs() : null;
        holder.binding.txtAttributeValue.setText(
                attrs != null
                        ? attrs.get(item.getRequestedAttribute().getName())
                        : null
        );
    } // 계좌번호, 사업자등록번호 빼

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<RequestedAttributeSearchedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRequestedAttributeBinding binding;

        public ViewHolder(ItemRequestedAttributeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
