package com.pepdeal.in.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pepdeal.in.R;
import com.pepdeal.in.activity.ProductDetailsActivity;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.model.UsersHomeTabModel;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context activity;

    public ProductAdapter(Context activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_product_list_layout, parent, false);
        return new ViewHolder(layoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          /*  UsersHomeTabModel model = homeTabModelArrayList.get(position);
            holder.bind(model, position);*/
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductListLayoutBinding layoutBinding;

        public ViewHolder(@NonNull ItemProductListLayoutBinding itemView) {
            super(itemView.getRoot());
            this.layoutBinding = itemView;
        }

        public void bind() {
            layoutBinding.lnrDetails.setOnClickListener(view -> {
                activity.startActivity(new Intent(activity, ProductDetailsActivity.class));
            });
            layoutBinding.relImage.setOnClickListener(view -> {
                activity.startActivity(new Intent(activity, ProductDetailsActivity.class));
            });
        }
    }
}
