package com.pepdeal.in.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepdeal.in.R;
import com.pepdeal.in.activity.HomeActivity;
import com.pepdeal.in.activity.ShopDetailsActivity;
import com.pepdeal.in.adapter.ProductAdapter;
import com.pepdeal.in.databinding.FragmentHomeBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.model.UsersHomeTabModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    Activity activity;
    ArrayList<UsersHomeTabModel> homeTabModelArrayList = new ArrayList<>();
    FragmentHomeBinding binding;

    public HomeFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        binding.recList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recList.setAdapter(new ShopAdapter());

        return binding.getRoot();
    }

    public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHomeShopsListBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_home_shops_list, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            UsersHomeTabModel model = homeTabModelArrayList.get(position);
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemHomeShopsListBinding layoutBinding;

            public ViewHolder(@NonNull ItemHomeShopsListBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind() {
                layoutBinding.txtName.setOnClickListener(view -> {
                    startActivity(new Intent(activity, ShopDetailsActivity.class));
                });
                layoutBinding.recProduct.setLayoutManager(new GridLayoutManager(activity, 3));
                layoutBinding.recProduct.setAdapter(new ProductAdapter(activity));
            }
        }
    }

}