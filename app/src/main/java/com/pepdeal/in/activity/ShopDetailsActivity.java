package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepdeal.in.R;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.databinding.ActivityShopDetailsBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.databinding.ItemServiceAvaliableLayoutBinding;
import com.pepdeal.in.fragment.FavoriteFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.fragment.TicketFragment;
import com.pepdeal.in.model.UsersHomeTabModel;

import java.util.ArrayList;

public class ShopDetailsActivity extends AppCompatActivity {

    ActivityShopDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        binding.setHandler(new ClickHandler());
        binding.recTab.setLayoutManager(new LinearLayoutManager(ShopDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.recTab.setAdapter(new ServiceAdapter());
        binding.recList.setLayoutManager(new GridLayoutManager(ShopDetailsActivity.this, 3));
        binding.recList.setAdapter(new ProductAdapter(ShopDetailsActivity.this));
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemServiceAvaliableLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(ShopDetailsActivity.this), R.layout.item_service_avaliable_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           /* UsersHomeTabModel model = homeTabModelArrayList.get(position);
            holder.bind(model, position);*/
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemServiceAvaliableLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemServiceAvaliableLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(UsersHomeTabModel model, int position) {
            }
        }
    }
}