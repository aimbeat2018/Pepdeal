package com.pepdeal.in.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.GPSTracker;
import com.pepdeal.in.constants.LocationTrack;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityHomeBinding;
import com.pepdeal.in.databinding.ItemCategoryHomeLayoutBinding;
import com.pepdeal.in.fragment.FavoriteFragment;
import com.pepdeal.in.fragment.HelpFragment;
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
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements LocationListener/*, GpsStatus.Listener*/ {

    ActivityHomeBinding binding;
    ArrayList<UsersHomeTabModel> homeTabModelArrayList = new ArrayList<>();
    public static int pos = 1;
    String user_status = "", shop_name = "", shop_id = "", username = "", shopStatus = "";
    String msgFlagDefault = "0";
    public static final int REQUEST_CHECK_SETTINGS = 125;
    public static final int PERMISSIONS_LOCATION_REQUEST = 124;
    private LocationManager mLocationManager;
    LocationTrack locationTrack;
    Geocoder geocoder;
    GPSTracker gpsTracker;
    List<Address> addresses;
    Location myLocation;
    public static double longitude = 0.0;
    public static double latitude = 0.0;
    public static String address = "";
    String newLeadCount = "";
    CardView cardNewMessage;
    UsersTabAdapter adapter;
    String seacrh_By;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.includeLayout.setHandler(new NavigationClick());
        binding.setHandler(new ClickHandler());

        SharedPref.putVal(HomeActivity.this, SharedPref.msgFlag, msgFlagDefault);

        geocoder = new Geocoder(this, Locale.getDefault());
        gpsTracker = new GPSTracker(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        setHomeTabData();
        navigationDrawer();
        // binding.setHandler(new HomeActivity.ClickHandler(this));

        SharedPref.putBol(HomeActivity.this, SharedPref.isLogin, true);

        binding.searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        // This method will be executed once the timer is over
                        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                        intent.putExtra("key", binding.searchView.getText().toString());
                        startActivity(intent);
                    }
                }, 1000);
                return true;
            }
            return false;
        });
        checkLocationPermission();

        binding.lnrLocation.setOnClickListener(view -> {
//            startActivity(new Intent(HomeActivity.this, StateSearchListActivity.class));
//            startActivity(new Intent(HomeActivity.this, SelectCurrentLocationActivity.class));
            Intent destinationIntent = new Intent(this, SelectCurrentLocationActivity.class);
//                Intent destinationIntent = new Intent(this, SelectLocationActivity.class);
            destinationIntent.putExtra("data", true);
            startActivityForResult(destinationIntent, 100);
//            binding.drawerLayout.closeDrawers();

        });
        binding.ivSearch.setOnClickListener(view -> {
            onSearchbutton();
            //    startActivity(new Intent(HomeActivity.this, SearchActivity.class));
//            binding.drawerLayout.closeDrawers();

        });
        /*By Default Home fragment load*/
        pos = 1;
        loadFragment(new HomeFragment());


    }

    private void onSearchbutton() {
        showSearchDialog();
     /*   new AlertDialog.Builder(HomeActivity.this,R.style.MyDialogTheme)
                .setTitle("Search")
                .setMessage("How do you want to search?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.search_by_shop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        seacrh_By = "shop";
                        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                        // intent.putExtra("key", binding.searchView.getText().toString());
                        intent.putExtra("searchby", seacrh_By);
                        startActivity(intent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.search_by_product, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        seacrh_By = "product";
                        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                        //    intent.putExtra("key", binding.searchView.getText().toString());
                        intent.putExtra("searchby", seacrh_By);
                        startActivity(intent);
                    }
                })
                .show();*/
    }

    private void showSearchDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.search_item_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        Button btnshop = dialog.findViewById(R.id.btn_byshop);
        Button btnproduct = dialog.findViewById(R.id.btn_product);

        btnshop.setOnClickListener(v -> {
            seacrh_By = "shop";
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            // intent.putExtra("key", binding.searchView.getText().toString());
            intent.putExtra("searchby", seacrh_By);
            startActivity(intent);
            dialog.dismiss();
        });

        btnproduct.setOnClickListener(v -> {
            seacrh_By = "product";
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            //    intent.putExtra("key", binding.searchView.getText().toString());
            intent.putExtra("searchby", seacrh_By);
            startActivity(intent);
            dialog.dismiss();

        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetwork(HomeActivity.this)) {
            requestUsersProfileParams();
            getLeadCount("user");
        } else {
            Utils.InternetAlertDialog(HomeActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }
    }

    private void requestUsersProfileParams() {

        UserProfileRequestModel requestModel = new UserProfileRequestModel();
        requestModel.setUserId(SharedPref.getVal(HomeActivity.this, SharedPref.user_id));

        userProfile(requestModel);

    }

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

                        username = jsonObject1.getString("first_name");
                        String mobile = jsonObject1.getString("mobile_no");
                        shop_name = jsonObject1.getString("shop_name");
                        user_status = jsonObject1.getString("user_status");
                        shopStatus = jsonObject1.getString("shop_status");
                        shop_id = jsonObject1.getString("shop_id");
                        binding.includeLayout.txtname.setText(username);
                        binding.includeLayout.txtmobile.setText(mobile);

                        SharedPref.putVal(HomeActivity.this, SharedPref.shop_id, shop_id);
                        SharedPref.putVal(HomeActivity.this, SharedPref.userName, username);
                        SharedPref.putVal(HomeActivity.this, SharedPref.shopName, shop_name);

                        if (binding.includeLayout.lnrCustomerNavigation.getVisibility() == View.VISIBLE) {
                            binding.includeLayout.txtname.setText(username);
                            binding.includeLayout.relEditProfile.setVisibility(View.VISIBLE);
                            binding.includeLayout.relShopArrow.setVisibility(View.GONE);
                        } else {
                            binding.includeLayout.txtname.setText(shop_name);
                            binding.includeLayout.relEditProfile.setVisibility(View.GONE);
                            binding.includeLayout.relShopArrow.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.includeLayout.txtname.setText("username");
                        binding.includeLayout.txtmobile.setText("mobile");
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
            binding.includeLayout.lnrCustomerBackground.setBackgroundColor(Color.parseColor("#000000"));
            binding.includeLayout.txtCustomer.setTextColor(Color.parseColor("#FFF27F"));
            binding.includeLayout.lnrSellerBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.includeLayout.txtSeller.setTextColor(Color.parseColor("#000000"));
            msgFlagDefault = "0";
            SharedPref.putVal(HomeActivity.this, SharedPref.msgFlag, msgFlagDefault);
            binding.includeLayout.txtname.setText(username);
            binding.includeLayout.relEditProfile.setVisibility(View.VISIBLE);
            binding.includeLayout.relShopArrow.setVisibility(View.GONE);
            if (Utils.isNetwork(HomeActivity.this)) {
                getLeadCount("user");
            } else {
                Utils.InternetAlertDialog(HomeActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }

        }


        public void onUpdateClick(View view) {
            if (binding.includeLayout.lnrCustomerNavigation.getVisibility() == View.VISIBLE) {
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
            } else {
                if (shopStatus.equals("0")) {
                    startActivity(new Intent(HomeActivity.this, ShopDetailsActivity.class).putExtra("shop_id", shop_id));
                } else {
                    Dialog dialog = new Dialog(HomeActivity.this);
                    dialog.setContentView(R.layout.ok_item_layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView txt_title = dialog.findViewById(R.id.txt_title);
                    TextView txt_alert = dialog.findViewById(R.id.txt_alert);
                    txt_alert.setVisibility(View.VISIBLE);
                    Button yes = dialog.findViewById(R.id.btn_ok);
                    txt_title.setText("Waiting for approval.");

                    yes.setOnClickListener(v -> {
                        dialog.dismiss();
                        binding.drawerLayout.closeDrawers();
                    });

                    dialog.show();


                 /*   new AlertDialog.Builder(HomeActivity.this,R.style.MyDialogTheme)
                            .setTitle("Alert!!!")
                            .setMessage("Waiting for approval.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                    binding.drawerLayout.closeDrawers();
                                }
                            })
                            .show();*/
                }
            }

            binding.drawerLayout.closeDrawers();
        }

        public void onSellerClick(View view) {
            if (user_status.equals("1")) {
                if (shopStatus.equals("0")) {
                    binding.includeLayout.lnrCustomerNavigation.setVisibility(View.GONE);
                    binding.includeLayout.lnrSellerNavigation.setVisibility(View.VISIBLE);
                    binding.includeLayout.lnrSellerBackground.setBackgroundColor(Color.parseColor("#000000"));
                    binding.includeLayout.txtSeller.setTextColor(Color.parseColor("#FFF27F"));
                    binding.includeLayout.lnrCustomerBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    binding.includeLayout.txtCustomer.setTextColor(Color.parseColor("#000000"));
                    msgFlagDefault = "1";
                    SharedPref.putVal(HomeActivity.this, SharedPref.msgFlag, msgFlagDefault);

                    binding.includeLayout.txtname.setText(shop_name);
                    binding.includeLayout.relEditProfile.setVisibility(View.GONE);
                    binding.includeLayout.relShopArrow.setVisibility(View.VISIBLE);

                    if (Utils.isNetwork(HomeActivity.this)) {
                        getLeadCount("shop");
                    } else {
                        Utils.InternetAlertDialog(HomeActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                    }
                } else {
                    Dialog dialog = new Dialog(HomeActivity.this);
                    dialog.setContentView(R.layout.ok_item_layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView txt_title = dialog.findViewById(R.id.txt_title);
                    TextView txt_alert = dialog.findViewById(R.id.txt_alert);
                    txt_alert.setVisibility(View.VISIBLE);
                    Button yes = dialog.findViewById(R.id.btn_ok);
                    txt_title.setText("Waiting for approval.");

                    yes.setOnClickListener(v -> {
                        dialog.dismiss();
                        binding.drawerLayout.closeDrawers();
                    });

                    dialog.show();

                /*    new AlertDialog.Builder(HomeActivity.this,R.style.MyDialogTheme)
                            .setTitle("Alert!!!")
                            .setMessage("Waiting for approval.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                    binding.drawerLayout.closeDrawers();
                                }
                            })
                            .show();*/
                }
            } else {
                Toast.makeText(HomeActivity.this, "First Add Your Shop", Toast.LENGTH_SHORT).show();
            }
        }


        public void onLogout(View view) {
            Dialog dialog = new Dialog(HomeActivity.this);
            dialog.setContentView(R.layout.delete_popup);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView txt_title = dialog.findViewById(R.id.txt_title);
            TextView txt_alert = dialog.findViewById(R.id.txt_alert);
            ImageView img_delete = dialog.findViewById(R.id.img_delete);
            img_delete.setVisibility(View.GONE);
            txt_alert.setVisibility(View.VISIBLE);
            Button yes = dialog.findViewById(R.id.yes);
            Button no = dialog.findViewById(R.id.no);
            txt_alert.setText("Logout");
            txt_title.setText("Are you sure you want to logout?");

            yes.setOnClickListener(v -> {
                // Continue with delete operation
                if (Utils.isNetwork(HomeActivity.this)) {
                    logoutUser();
                    dialog.dismiss();
                } else {
//                                            binding.lnrMainLayout.setVisibility(View.GONE);
                    Utils.InternetAlertDialog(HomeActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
                }
                dialog.dismiss();
            });

            no.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
          /*  new AlertDialog.Builder(HomeActivity.this,R.style.MyDialogTheme)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            logoutUser();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();*/
        }

        public void onHomeClick(View view) {
            binding.drawerLayout.closeDrawers();
            pos = 1;
            loadFragment(new HomeFragment());
        }

        public void onAddProductClick(View view) {
            startActivity(new Intent(HomeActivity.this, AddProductActivity.class).putExtra("from", "add"));
            binding.drawerLayout.closeDrawers();
        }

        public void onUpdateProductListClick(View view) {
            startActivity(new Intent(HomeActivity.this, SellerProductListingActivity.class));
            binding.drawerLayout.closeDrawers();
        }

        public void onCategoryListClick(View view) {
            startActivity(new Intent(HomeActivity.this, AllCategoryListActivity.class));
            binding.drawerLayout.closeDrawers();
        }

        public void onStartShopClick(View view) {
//            binding.drawerLayout.closeDrawers();
            if (user_status.equals("0")) {
                startActivity(new Intent(HomeActivity.this, AddShopActivity.class));
            } else {
//                Toast.makeText(HomeActivity.this, "Shop already Added", Toast.LENGTH_SHORT).show();
                if (shopStatus.equals("0")) {
//                    startActivity(new Intent(HomeActivity.this, AddShopActivity.class));

                    startActivity(new Intent(HomeActivity.this, ShopDetailsActivity.class).putExtra("shop_id", shop_id));
                } else {
                    Dialog dialog = new Dialog(HomeActivity.this);
                    dialog.setContentView(R.layout.ok_item_layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView txt_title = dialog.findViewById(R.id.txt_title);
                    TextView txt_alert = dialog.findViewById(R.id.txt_alert);
                    txt_alert.setVisibility(View.VISIBLE);
                    Button yes = dialog.findViewById(R.id.btn_ok);
                    txt_title.setText("Waiting for approval.");

                    yes.setOnClickListener(v -> {
                        dialog.dismiss();
                        binding.drawerLayout.closeDrawers();
                    });

                    dialog.show();
                   /* new AlertDialog.Builder(HomeActivity.this,R.style.MyDialogTheme)
                            .setTitle("Alert!!!")
                            .setMessage("Waiting for approval.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                    binding.drawerLayout.closeDrawers();
                                }
                            })
                            .show();*/
                }
            }
            binding.drawerLayout.closeDrawers();
        }

        public void onAboutShopClick(View view) {
            startActivity(new Intent(HomeActivity.this, UpdateAboutShopActivity.class)
                    .putExtra("shop_id", SharedPref.getVal(HomeActivity.this, SharedPref.shop_id)));
            binding.drawerLayout.closeDrawers();
        }

        public void onSettingClick(View view) {
            startActivity(new Intent(HomeActivity.this, SellerShopServicesListActivity.class));
            binding.drawerLayout.closeDrawers();
        }

        public void onMessageClick(View view) {
            startActivity(new Intent(HomeActivity.this, LeadsActivity.class).putExtra("from", "shop"));
            binding.drawerLayout.closeDrawers();
        }

        public void onSellerTicketClick(View view) {
            startActivity(new Intent(HomeActivity.this, SellerTicketListActivity.class));
            binding.drawerLayout.closeDrawers();
        }

        public void onAboutUs(View view) {
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class).putExtra("from", "about"));
            binding.drawerLayout.closeDrawers();
        }

        public void onShopSignBoardClick(View view) {
            startActivity(new Intent(HomeActivity.this, ShopSignBoardActivity.class));
            binding.drawerLayout.closeDrawers();
        }

        /* if (from.equals("about")) {
            binding.txtTitle.setText("About Us");
        } else if (from.equals("privacy")) {
            binding.txtTitle.setText("Privacy policy");
        } else if (from.equals("terms")) {
            binding.txtTitle.setText("Terms & Condition");
        } else if (from.equals("contact")) {
            binding.txtTitle.setText("Contact Us");
        }*/

        public void onContactUs(View view) {
            startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
            binding.drawerLayout.closeDrawers();
        }

        public void onLegalUs(View view) {
            startActivity(new Intent(HomeActivity.this, LegalActivity.class));
            binding.drawerLayout.closeDrawers();
        }


    }

    public class ClickHandler {
        public void onHomeClick(View view) {
            pos = 1;
            loadFragment(new HomeFragment());
//            binding.nestedScroll.fullScroll(View.FOCUS_UP);
        }

        public void onTicketClick(View view) {
            pos = 2;
            loadFragment(new TicketFragment());
        }

        public void onSuperShopClick(View view) {
            pos = 3;
            loadFragment(new SuperShopFragment());
        }

        public void onFavoriteClick(View view) {
            pos = 4;
            loadFragment(new FavoriteFragment());
        }

        public void onHelpClick(View view) {
            pos = 5;
            loadFragment(new HelpFragment());
        }

        public void onMessageClick(View view) {
            pos = 6;
            startActivity(new Intent(HomeActivity.this, LeadsActivity.class).putExtra("from", "user"));
        }
    }

    private void navigationDrawer() {
        //Navigation Drawer
        binding.navView.bringToFront();

        binding.ivMenu.setOnClickListener(view -> {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            else binding.drawerLayout.openDrawer(GravityCompat.START);
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
        UsersHomeTabModel model5 = new UsersHomeTabModel();
        model5.setColor(Color.parseColor("#F65F5F"));
        model5.setIcon(R.drawable.ic_home_black);
        model5.setTitle(getString(R.string.home));
        homeTabModelArrayList.add(model5);

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
        adapter = new UsersTabAdapter();
        binding.recTab.setAdapter(adapter);
    }

    /*new Lead*/
    private void getLeadCount(String from) {
        UserProfileRequestModel model = new UserProfileRequestModel();
        if (from.equals("user")) {
            model.setUserId(SharedPref.getVal(HomeActivity.this, SharedPref.user_id));
            model.setShop_id("");
        } else {
            model.setUserId("");
            model.setShop_id(SharedPref.getVal(HomeActivity.this, SharedPref.shop_id));
        }

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.msgsCount(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
//                        newLeadCount = jsonObject.getString("count");
                        if (from.equals("user")) {
//                                newLeadCount = jsonObject.getString("count");
//                                if (!newLeadCount.equals("")) {
                          /*  if (!jsonObject.getString("status").equals("")) {//status -deal complted
                                binding.cardNewMessage.setVisibility(View.VISIBLE);
                            }
                            else{
                                binding.cardNewMessage.setVisibility(View.GONE);
                            }*/
                            if (Integer.parseInt(jsonObject.getString("count")) > 0) {
                                binding.cardNewMessage.setVisibility(View.VISIBLE);
                            } else {
                                binding.cardNewMessage.setVisibility(View.GONE);
                            }
//                                }
                        } else {
                            if (Integer.parseInt(jsonObject.getString("count")) > 0) {
                                binding.includeLayout.txtNewMessage.setVisibility(View.VISIBLE);
                            }
                            else {
                                binding.cardNewMessage.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        binding.includeLayout.txtNewMessage.setVisibility(View.GONE);
                        binding.cardNewMessage.setVisibility(View.GONE);
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
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

    /*LogoutUser*/
    private void logoutUser() {
//        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(HomeActivity.this, SharedPref.user_id));

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.userLogout(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        // Continue with delete operation
                        SharedPref.clearData(HomeActivity.this);
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
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
                cardNewMessage = layoutBinding.cardNewMessage;
            }

            public void bind(UsersHomeTabModel model, int position) {
                layoutBinding.setModel(model);
                layoutBinding.lnrBack.setBackgroundColor(model.getColor());

                layoutBinding.cardTab.setOnClickListener(view -> {
                    if (position == 0) {
                        pos = 1;
                        loadFragment(new HomeFragment());
//                        binding.nestedScroll.fullScroll(View.FOCUS_UP);
                    } else if (position == 1) {
                        pos = 2;
                        loadFragment(new TicketFragment());
                    } else if (position == 2) {
                        pos = 3;
                        loadFragment(new SuperShopFragment());
                    } else if (position == 3) {
                        pos = 4;
                        loadFragment(new FavoriteFragment());
                    } else if (position == 4) {
                        pos = 5;
                        loadFragment(new HelpFragment());
                    } else if (position == 5) {
                        pos = 6;
                        startActivity(new Intent(HomeActivity.this, LeadsActivity.class).putExtra("from", "user"));
                    }
                });
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(HomeActivity.this.findViewById(android.R.id.content),
                        "Please Grant Permissions to access your location",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_LOCATION_REQUEST);
//                                }
                            }
                        }).show();
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission
                                .ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION_REQUEST);
//                }
            }
        } else {
            // write your logic code if permission already granted
//            mLocationManager.add(this);
            boolean isclick = true;
            if (isclick) {
                locationTrack = new LocationTrack(HomeActivity.this);

                if (gpsTracker.canGetLocation()) {

                    longitude = gpsTracker.getLongitude();
                    latitude = gpsTracker.getLatitude();

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        if (addresses.size() == 0) {
                            address = addresses.get(0).getAddressLine(0);
                            Toast.makeText(HomeActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                        } else {
                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        }
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        binding.txtLocation.setText(address);
//                        binding.txtLocation.setText(address + " " + city + " " + state + " " + country + " " + knownName + " "  + postalCode);
//                        binding.txtLocation.setText(city + " " + state + " " + country + " " + knownName + " "  + postalCode);
//                        String name = getRegionName(latitude, longitude);
                        /*binding.txtSelectedLocation.setVisibility(View.VISIBLE);
//                        binding.txtSelectedLocation.setText(address + " " + city + " " + state + " " + country + " " + knownName + " " + name + " " + postalCode);
                        binding.txtSelectedLocation.setText(address);
                        checkPincode(postalCode);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    binding.txtSelectedLocation.setText(name);
//                    checkCity(name);
//                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {

//                    locationTrack.showSettingsAlert();
                    switchOnGPS();
                }
                isclick = false;
            } else {
               /* dialog.dismiss();
                isclick = true;*/
                isclick = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_LOCATION_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && readExternalFile && writeExternalFile) {
                        // write your logic here
                        boolean isclick = true;
                        if (isclick) {
                            locationTrack = new LocationTrack(HomeActivity.this);

                            if (locationTrack.canGetLocation()) {

                                longitude = locationTrack.getLongitude();
                                latitude = locationTrack.getLatitude();

                                try {
//                                    binding.edtShopLatLong.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                    if (addresses.size() == 0) {
                                        Toast.makeText(HomeActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                                    } else {
                                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    }
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    binding.txtLocation.setText(city);
//                                    binding.txtLocation.setText(address + " " + city + " " + state + " " + country + " " + knownName + " "  + postalCode);
//                                    binding.txtLocation.setText(city + " " + state + " " + country + " " + knownName + " "  + postalCode);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {

                                locationTrack.showSettingsAlert();
                            }
                            isclick = false;
                        } else {
                           /* dialog.dismiss();
                            isclick = true;*/
                            isclick = true;
                        }
                    } else {
                        Snackbar.make(HomeActivity.this.findViewById(android.R.id.content),
                                "Please Grant Permissions to Access your current location",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{Manifest.permission
                                                            .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                                    PERMISSIONS_LOCATION_REQUEST);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void switchOnGPS() {
        @SuppressLint("RestrictedApi") LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(HomeActivity.this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //open setting and switch on GPS manually

                        break;
                }
            }
        });
        //Give permission to access GPS
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);
    }

//    @Override
//    public void onGpsStatusChanged(int i) {
//        switch (i) {
//            case GpsStatus.GPS_EVENT_STOPPED:
//                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    switchOnGPS();
//                }
//                break;
//            case GpsStatus.GPS_EVENT_FIRST_FIX:
//                break;
//        }
//    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        myLocation = location;

        longitude = myLocation.getLongitude();
        latitude = myLocation.getLatitude();


        try {
//            binding.edtShopLatLong.setText(String.valueOf(latitude) + "," + String.valueOf(longitude));
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size() == 0) {
                Toast.makeText(HomeActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
            } else {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }

            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            binding.txtLocation.setText(city);
//            binding.txtLocation.setText(city + " " + state + " " + country + " " + knownName + " "  + postalCode);

                     /*   String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        String name = getRegionName(latitude, longitude);
                        binding.txtSelectedLocation.setVisibility(View.VISIBLE);
//                        binding.txtSelectedLocation.setText(address + " " + city + " " + state + " " + country + " " + knownName + " " + name + " " + postalCode);
                        binding.txtSelectedLocation.setText(address);
                        checkPincode(postalCode);*/
        } catch (Exception e) {
            e.printStackTrace();
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
            loadFragment(new HomeFragment());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                address = data.getStringExtra("d_address");
                binding.txtLocation.setText(address);
                latitude = Double.parseDouble(data.getStringExtra("lat"));
                longitude = Double.parseDouble(data.getStringExtra("long"));
            }

        }
    }
}