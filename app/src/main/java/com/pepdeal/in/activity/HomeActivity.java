package com.pepdeal.in.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pepdeal.in.R;
import com.pepdeal.in.databinding.ActivityHomeBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.databinding.ItemHomeShopsListBinding;
import com.pepdeal.in.databinding.ItemProductListLayoutBinding;
import com.pepdeal.in.fragment.FavoriteFragment;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.fragment.TicketFragment;
import com.pepdeal.in.model.UsersHomeTabModel;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    ArrayList<UsersHomeTabModel> homeTabModelArrayList = new ArrayList<>();
    public static int pos = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.includeLayout.setHandler(new NavigationClick());
        setHomeTabData();
        navigationDrawer();

        /*By Default Home fragment load*/
        pos = 1;
        loadFragment(new HomeFragment(HomeActivity.this));
    }

    public class NavigationClick {
        public void onCustomerClick(View view) {
            binding.includeLayout.lnrCustomerNavigation.setVisibility(View.VISIBLE);
            binding.includeLayout.lnrSellerNavigation.setVisibility(View.GONE);

            binding.includeLayout.lnrCustomerBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.includeLayout.lnrSellerBackground.setBackgroundColor(Color.parseColor("#F6B394"));
        }

        public void onSellerClick(View view) {
            binding.includeLayout.lnrCustomerNavigation.setVisibility(View.GONE);
            binding.includeLayout.lnrSellerNavigation.setVisibility(View.VISIBLE);

            binding.includeLayout.lnrSellerBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.includeLayout.lnrCustomerBackground.setBackgroundColor(Color.parseColor("#F6B394"));
        }

        public void onHomeClick(View view) {
            binding.drawerLayout.closeDrawers();
        }
    }

    private void navigationDrawer() {

        //Navigation Drawer
        binding.navView.bringToFront();

        binding.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.drawerLayout.isDrawerVisible(GravityCompat.START))
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                else binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment);
        transaction.commit();
    }

    private void setHomeTabData() {
        homeTabModelArrayList = new ArrayList<>();

        /*Tab 1*/
        UsersHomeTabModel model = new UsersHomeTabModel();
        model.setColor(Color.parseColor("#FFCD59"));
        model.setIcon(R.drawable.ic_tickets);
        model.setTitle(getString(R.string.ticket));
        homeTabModelArrayList.add(model);
        /*Tab 2*/
        UsersHomeTabModel model1 = new UsersHomeTabModel();
        model1.setColor(Color.parseColor("#B3FAA6"));
        model1.setIcon(R.drawable.ic_super_shop);
        model1.setTitle(getString(R.string.super_shop));
        homeTabModelArrayList.add(model1);
        /*Tab 3*/
        UsersHomeTabModel model2 = new UsersHomeTabModel();
        model2.setColor(Color.parseColor("#A9B8FA"));
        model2.setIcon(R.drawable.ic_favorite);
        model2.setTitle(getString(R.string.favorite));
        homeTabModelArrayList.add(model2);
        /*Tab 4*/
        UsersHomeTabModel model3 = new UsersHomeTabModel();
        model3.setColor(Color.parseColor("#FFBAE4"));
        model3.setIcon(R.drawable.ic_help);
        model3.setTitle(getString(R.string.help));
        homeTabModelArrayList.add(model3);
        /*Tab 5*/
        UsersHomeTabModel model4 = new UsersHomeTabModel();
        model4.setColor(Color.parseColor("#8FEDED"));
        model4.setIcon(R.drawable.ic_message);
        model4.setTitle(getString(R.string.message));
        homeTabModelArrayList.add(model4);

        binding.recTab.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.recTab.setAdapter(new UsersTabAdapter());
    }

    public class UsersTabAdapter extends RecyclerView.Adapter<UsersTabAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCategoryHomeLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(HomeActivity.this), R.layout.item_category_home_layout, parent, false);
            return new ViewHolder(layoutBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            UsersHomeTabModel model = homeTabModelArrayList.get(position);
            holder.bind(model, position);
        }

        @Override
        public int getItemCount() {
            return homeTabModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemCategoryHomeLayoutBinding layoutBinding;

            public ViewHolder(@NonNull ItemCategoryHomeLayoutBinding itemView) {
                super(itemView.getRoot());
                this.layoutBinding = itemView;
            }

            public void bind(UsersHomeTabModel model, int position) {
                layoutBinding.setModel(model);
                layoutBinding.lnrBack.setBackgroundColor(model.getColor());

                layoutBinding.cardTab.setOnClickListener(view -> {
                    if (position == 0) {
                        pos = 2;
                        loadFragment(new TicketFragment(HomeActivity.this));
                    } else if (position == 1) {
                        pos = 3;
                        loadFragment(new SuperShopFragment(HomeActivity.this));
                    } else if (position == 2) {
                        pos = 4;
                        loadFragment(new FavoriteFragment(HomeActivity.this));
                    }
                });
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (pos == 1) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            loadFragment(new HomeFragment(HomeActivity.this));
        }
    }
}