package com.pepdeal.in.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.FragmentFavoriteBinding;
import com.pepdeal.in.databinding.FragmentTicketBinding;
import com.pepdeal.in.databinding.ItemFavoriteLayoutBinding;
import com.pepdeal.in.databinding.ItemTicketLayoutBinding;


public class FavoriteFragment extends Fragment {

    Activity activity;
    FragmentFavoriteBinding binding;

    public FavoriteFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);

        binding.recList.setLayoutManager(new LinearLayoutManager(activity));
        binding.recList.setAdapter(new ProductAdapter());

        return binding.getRoot();
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFavoriteLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_favorite_layout, parent, false);
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
            ItemFavoriteLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemFavoriteLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind() {
            }
        }
    }
}