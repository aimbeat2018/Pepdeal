package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityMessageUsersListBinding;
import com.pepdeal.in.databinding.ItemCategoryListLayoutBinding;
import com.pepdeal.in.databinding.ItemMessageUsersListLayoutBinding;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;

public class MessageUsersListActivity extends AppCompatActivity {

    ActivityMessageUsersListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_users_list);
        binding.setHandler(new ClickHandler());

        binding.recList.setLayoutManager(new LinearLayoutManager(MessageUsersListActivity.this));
        binding.recList.setAdapter(new MessageAdapter());
    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMessageUsersListLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(MessageUsersListActivity.this),
                    R.layout.item_message_users_list_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            /*AddProductCategoryResponseModel model = productCategoryModelList.get(position);
            holder.bind(model, position);*/
        }

        @Override
        public int getItemCount() {
//            return productCategoryModelList.size();
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemMessageUsersListLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemMessageUsersListLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            /*public void bind(AddProductCategoryResponseModel model, int position) {
                Glide.with(AllCategoryListActivity.this).load(model.getCategoryImages())
                        .error(R.drawable.loader).placeholder(R.drawable.loader).into(layoutBinding.imgProductImage);
                layoutBinding.txtCategoryName.setText(model.getCategoryName());
            }*/

        }
    }
}