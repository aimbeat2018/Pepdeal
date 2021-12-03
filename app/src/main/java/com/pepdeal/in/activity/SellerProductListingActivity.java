package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.databinding.ActivitySellerProductListingBinding;
import com.pepdeal.in.databinding.ActivityShopDetailsBinding;
import com.pepdeal.in.databinding.ItemSellerProductListingLayoutBinding;
import com.pepdeal.in.databinding.ItemServiceAvaliableLayoutBinding;
import com.pepdeal.in.model.UsersHomeTabModel;

public class SellerProductListingActivity extends AppCompatActivity {

    ActivitySellerProductListingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_product_listing);
        binding.setHandler(new ClickHandler());
        binding.recList.setLayoutManager(new LinearLayoutManager(SellerProductListingActivity.this));
        binding.recList.setAdapter(new ProductAdapter());
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSellerProductListingLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(SellerProductListingActivity.this),
                    R.layout.item_seller_product_listing_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           /* UsersHomeTabModel model = homeTabModelArrayList.get(position);
            holder.bind(model, position);*/
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemSellerProductListingLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemSellerProductListingLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind() {
                layoutBinding.cardUpdate.setOnClickListener(view -> {
                    startActivity(new Intent(SellerProductListingActivity.this, AddProductActivity.class));
                });
            }
        }
    }
}