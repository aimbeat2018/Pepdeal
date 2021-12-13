package com.pepdeal.in.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.databinding.ActivityHomeBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.fragment.FavoriteFragment;
import com.pepdeal.in.fragment.HomeFragment;
import com.pepdeal.in.fragment.SuperShopFragment;
import com.pepdeal.in.fragment.TicketFragment;
import com.pepdeal.in.model.UsersHomeTabModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    ArrayList<UsersHomeTabModel> homeTabModelArrayList = new ArrayList<>();
    public static int pos = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.includeLayout.setHandler(new NavigationClick());
        setHomeTabData();
        navigationDrawer();
        // binding.setHandler(new HomeActivity.ClickHandler(this));

        SharedPref.putBol(HomeActivity.this, SharedPref.isLogin, true);
        SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        String usermobile = sharedPreferences.getString("mobile_no", "");
        String username = sharedPreferences.getString("username", "");

        binding.userid.setText(user_id);
        binding.usermobile.setText(usermobile);

        //used in user profile
       // binding.includeLayout.txtmobile.setText(username);
        //binding.includeLayout.txtname.setText(username);

       // requestUsersProfileParams();

        /*By Default Home fragment load*/
        pos = 1;
        loadFragment(new HomeFragment(HomeActivity.this));
    }

    @Override
    protected void onResume() {
        requestUsersProfileParams();


        super.onResume();
    }

    private void requestUsersProfileParams() {

        SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "");
        String usermobile = sharedPreferences.getString("mobile_no", "");
       // String username = sharedPreferences.getString("username", "");

        UserProfileRequestModel requestModel = new UserProfileRequestModel();
        requestModel.setUserId(user_id);
      //  requestModel.setUser_id(SharedPref.getVal(activity, SharedPref.user_id));

        userProfile(requestModel);

    }

    /* private void dismissDialog() {
            if (dialog != null && dialog.isShowing()) ;
            dialog.dismiss();
        }
    */
    private void userProfile(UserProfileRequestModel model) {
        //dialog.show();

        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, "", "");

        apiInterface.user_detail(model).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    String status = jsonObject.getString("status");

                    if (status.equals("1")) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("body");

                        String username = jsonObject1.getString("first_name");
                        String mobile = jsonObject1.getString("mobile_no");

                        binding.includeLayout.txtname.setText(username);
                        binding.includeLayout.txtmobile.setText(mobile);


                        Toast.makeText(HomeActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        // String otp = jsonObject.getString("otp");

                        //Intent intent = new Intent(HomeActivity.this, LoginActivity.class);

                     //   startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException | NumberFormatException | IOException e) {
                    e.printStackTrace();
                }
                //dismissDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable error) {
               // dismissDialog();
                error.printStackTrace();
                if (error instanceof HttpException) {
                    switch (((HttpException) error).code()) {
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(HomeActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(HomeActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(HomeActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(HomeActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(HomeActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public class NavigationClick {

        public void onCustomerClick(View view) {
            binding.includeLayout.lnrCustomerNavigation.setVisibility(View.VISIBLE);
            binding.includeLayout.lnrSellerNavigation.setVisibility(View.GONE);
            binding.includeLayout.lnrCustomerBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.includeLayout.lnrSellerBackground.setBackgroundColor(Color.parseColor("#F6B394"));
        }


        public void onUpdateClick(View view) {

            startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));

        }

        public void onSellerClick(View view) {
            binding.includeLayout.lnrCustomerNavigation.setVisibility(View.GONE);
            binding.includeLayout.lnrSellerNavigation.setVisibility(View.VISIBLE);
            binding.includeLayout.lnrSellerBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.includeLayout.lnrCustomerBackground.setBackgroundColor(Color.parseColor("#F6B394"));
        }

        public void onLogout(View view) {

            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

        }

        public void onHomeClick(View view) {
            binding.drawerLayout.closeDrawers();
        }

        public void onAddProductClick(View view) {
            binding.drawerLayout.closeDrawers();
            startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
        }

        public void onUpdateProductListClick(View view) {
            binding.drawerLayout.closeDrawers();
            startActivity(new Intent(HomeActivity.this, SellerProductListingActivity.class));
        }

        public void onStartShopClick(View view) {
            binding.drawerLayout.closeDrawers();
            startActivity(new Intent(HomeActivity.this, AddShopActivity.class));
        }

        public void onAboutShopClick(View view) {
            binding.drawerLayout.closeDrawers();
            startActivity(new Intent(HomeActivity.this, AddShopActivity.class));
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