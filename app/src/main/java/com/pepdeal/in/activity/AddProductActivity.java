package com.pepdeal.in.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.databinding.DataBindingUtil;

import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAddProductBinding;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.AddProductListRequestModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    ActivityAddProductBinding binding;
    ArrayList<AddProductCategoryResponseModel> productCategoryModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product);
        binding.setHandler(new ClickHandler());

        addcategory();

    }


    private void addcategory() {

        AddProductCategoryResponseModel addProductCategoryResponseModel = new AddProductCategoryResponseModel();



        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.categoryList(addProductCategoryResponseModel).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String categorydata = jsonObject.getString("msgs");

                    if (categorydata.equals("Category List")) {
                        productCategoryModelList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            productCategoryModelList = new ArrayList<>();

                            AddProductCategoryResponseModel addProductCategoryResponseModel1 = new AddProductCategoryResponseModel();

                            addProductCategoryResponseModel1.setCategoryId(jsonObject1.getString("category_id"));
                            addProductCategoryResponseModel1.setCategoryName(jsonObject1.getString("category_name"));
                            addProductCategoryResponseModel1.setCategoryImages(jsonObject1.getString("category_images"));



                            productCategoryModelList.add(addProductCategoryResponseModel1);

                            setProductCategory(productCategoryModelList, binding.spinproductcategory);


                        }

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
                            Toast.makeText(AddProductActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddProductActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddProductActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddProductActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddProductActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setProductCategory(ArrayList<AddProductCategoryResponseModel> productCategoryModelList, AppCompatSpinner spinproductcat) {


        ArrayAdapter<AddProductCategoryResponseModel> modelArrayAdapter = new ArrayAdapter<AddProductCategoryResponseModel>(AddProductActivity.this,
                R.layout.custom_spinner_backgroundcolor, productCategoryModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_productcategory, null);
                }
                TextView colorname = (TextView) v.findViewById(R.id.catname);
               // TextView colorid = (TextView) v.findViewById(R.id.colorname);
                //  colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                // colorname.setText(backgroundcolorModelList.get(position).getBgColorName());
                colorname.setBackgroundColor(Color.parseColor(productCategoryModelList.get(position).getCategoryName()));


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                /*switch (position) {
                    case 0:
                        tv.setTextColor(Color.GRAY);
                        break;
                    default:
                        tv.setTextColor(Color.BLACK);
                        break;
                }*/
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_productcategory, null);
                }

                TextView colorname = (TextView) v.findViewById(R.id.catname);
                //TextView colorid = (TextView) v.findViewById(R.id.colorname);

                //colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                colorname.setText(productCategoryModelList.get(position).getCategoryName());
                //colorname.setBackgroundColor(Color.parseColor(productCategoryModelList.get(position).getCategoryName()));


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

              /*  switch (position) {
                    case 0:
                        colorid.setTextColor(Color.GRAY);
                        break;
                    default:
                        colorid.setTextColor(Color.BLACK);
                        break;
                }*/
                return v;
            }
        };
        spinproductcat.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }


    
    public class ClickHandler {

        public void onBackClick(View view) {
            onBackPressed();
        }

        public void AddProductClick(View view) {

           /* if (binding.entproductName.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Product Name ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.entbrand.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Brand Name ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.entcategory.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Category ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtdescription.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter description ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.entwarranty.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Warranty ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.entmrp.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter MRP ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.entmrp.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsearchtag.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtdiscount.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Disocunt ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsale.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Sale ", Toasty.LENGTH_SHORT, true).show();
            } else {
                Intent intent = new Intent(AddProductActivity.this, OtpVerificationActivity.class);
                intent.putExtra("from", "register");
                startActivity(intent);

*/
               /* if (Utils.isNetwork(AddProductActivity.this)) {

                    AddProductListRequestModel model = new AddProductListRequestModel();

                    addProductList(model);

                } else {
                    Toast.makeText(AddProductActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }*/
            }


        }



}

