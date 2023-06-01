package com.pepdeal.in.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepdeal.in.R;
import com.pepdeal.in.constants.ApiClient;
import com.pepdeal.in.constants.ApiInterface;
import com.pepdeal.in.constants.FileUtils;
import com.pepdeal.in.constants.SharedPref;
import com.pepdeal.in.constants.Utils;
import com.pepdeal.in.databinding.ActivityAddProductBinding;
import com.pepdeal.in.model.productdetailsmodel.ProductDetailsDataModel;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.AddProductListRequestModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;
import com.pepdeal.in.model.requestModel.SubCategoryModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    ActivityAddProductBinding binding;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    ArrayList<AddProductCategoryResponseModel> productCategoryModelList = new ArrayList<>();
    ArrayList<SubCategoryModel> subCategoryModelArrayList = new ArrayList<>();
    ArrayList<AddProductCategoryResponseModel> productBrandModelList = new ArrayList<>();
    String categoryId = "", brandId = "", subCategoryId = "",flag = "0";
    ProgressDialog dialog;
    File directory;
    String tempImageName = "";
    public String IMAGE_FILE_PATH;
    String encodedImage;
    File fileImage1, fileImage2, fileImage3;
    int var = 0;
    List<File> fileArrayList = new ArrayList<>();
    String productId = "", from = "";
    List<ProductDetailsDataModel> productDataModelList = new ArrayList<>();
    String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product);
        binding.setHandler(new ClickHandler());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        fileArrayList = new ArrayList<>();

        tempImageName = "";
        IMAGE_FILE_PATH = getExternalFilesDir(null) + "/Pepdeal";
        directory = new File(getExternalFilesDir(null) + "/Pepdeal");
        if (directory.exists()) {
            File[] contents = directory.listFiles();
            if (contents != null) {
                if (contents.length > 0) {
                    for (int i = 0; i < contents.length; i++) {
                        if (contents[i].isFile()) {
                            File sourceFile = new File(contents[i].toString());
                            sourceFile.delete();
                        }
                    }
                }
            }
        } else {
            directory.mkdir();
        }

        from = getIntent().getStringExtra("from");

        binding.edtdescription.setOnTouchListener((view, motionEvent) -> {

            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        });
        binding.edtdescription1.setOnTouchListener((view, motionEvent) -> {

            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        });
        binding.edtSpecification.setOnTouchListener((view, motionEvent) -> {

            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        });

        if (Utils.isNetwork(AddProductActivity.this)) {
            categoryList();
            brandList();
        } else {
            Utils.InternetAlertDialog(AddProductActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
        }

        if (from.equals("edit")) {
            productId = getIntent().getStringExtra("product_id");

            if (Utils.isNetwork(AddProductActivity.this)) {
                getProductDetails(true);
                binding.txtSave.setText("Update");
                binding.txtSaveLive.setText("Update & Live");
            } else {
                Utils.InternetAlertDialog(AddProductActivity.this, getString(R.string.no_internet_title), getString(R.string.no_internet_desc));
            }
        }
        onSpinnerSelected();

        binding.edtdiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.edtmrp.getText().toString().equals("")) {
                    Toast.makeText(AddProductActivity.this, "Enter Mrp", Toast.LENGTH_SHORT).show();
                } else {
                    if (binding.edtdiscount.getText().toString().equals("")) {
                        binding.edtsale.setText(binding.edtmrp.getText().toString());
                    } else if (Integer.parseInt(binding.edtdiscount.getText().toString()) > 100) {
                        Toast.makeText(AddProductActivity.this, "Discount should not be greater than 100", Toast.LENGTH_SHORT).show();
                        binding.edtdiscount.setText("");
                        binding.edtsale.setText("");
                    } else {
                        double mrp = Double.parseDouble(binding.edtmrp.getText().toString());
                        double discount = Double.parseDouble(binding.edtdiscount.getText().toString());

                        double calculatedDiscount = mrp * discount / 100;

                        double sellingPrice = mrp - calculatedDiscount;
                        long displaySellingPrice = Math.round(sellingPrice);

                        binding.edtsale.setText(String.valueOf(displaySellingPrice));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.cboncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(binding.cboncall.isChecked()) {
                   flag="1";
                   binding.llPrice.setVisibility(View.GONE);
               }
               else {
                   flag="0";
                   binding.llPrice.setVisibility(View.VISIBLE);
               }
            }
        });
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public class ClickHandler {

        public void onBackClick(View view) {
            onBackPressed();
        }

        public void AddProductClick(View view) {
            if (from.equals("edit")) {
                /*add image to array list*/
                fileArrayList = new ArrayList<>();
                if (fileImage1 != null) {
                    fileArrayList.add(fileImage1);
                }
                if (fileImage2 != null) {
                    fileArrayList.add(fileImage2);
                }
                if (fileImage3 != null) {
                    fileArrayList.add(fileImage3);
                }
                if (binding.entproductName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Product Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtBrandName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Brand Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (categoryId.equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Category ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtdescription.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description ", Toasty.LENGTH_SHORT, true).show();
                }
                else if (binding.edtSearchTagm.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Search Tags ", Toasty.LENGTH_SHORT, true).show();
                }
                else if (binding.edtmrp.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                }
                //oncall commented
           /*     else if(flag.equalsIgnoreCase("0")) {
                    if (binding.edtmrp.getText().toString().equals("")) {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else if(binding.edtmrp.getText().toString().equals("0"))
                    {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else {
                        updateProduct("1");
                    }
                }*/

                /*else if (binding.edtdescription1.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description 1", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtSpecification.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Specification", Toasty.LENGTH_SHORT, true).show();
                }*/ /*else if (binding.entwarranty.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Warranty ", Toasty.LENGTH_SHORT, true).show();
            } */ /*else if (binding.entmrp.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsearchtag.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour ", Toasty.LENGTH_SHORT, true).show();
            }*/ /*else if (binding.edtdiscount.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Discount ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsale.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Sale", Toasty.LENGTH_SHORT, true).show();
            } */ /*else if (fileArrayList.size() != 3) {
                    Toasty.info(AddProductActivity.this, "Select Product Image", Toasty.LENGTH_SHORT, true).show();
                }*/ else {

                    if (Utils.isNetwork(AddProductActivity.this)) {
                        updateProduct("1");
                    } else {
                        Toast.makeText(AddProductActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                /*add image to array list*/
                fileArrayList = new ArrayList<>();
                if (fileImage1 != null) {
                    fileArrayList.add(fileImage1);
                }
                if (fileImage2 != null) {
                    fileArrayList.add(fileImage2);
                }
                if (fileImage3 != null) {
                    fileArrayList.add(fileImage3);
                }
                if (binding.entproductName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Product Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtBrandName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Brand Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (categoryId.equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Category ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtdescription.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description ", Toasty.LENGTH_SHORT, true).show();
                }
                else if (binding.edtSearchTagm.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Search Tags ", Toasty.LENGTH_SHORT, true).show();
                }
              /*  else if(flag.equalsIgnoreCase("0")) {
                    if (binding.edtmrp.getText().toString().equals("")) {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else if(binding.edtmrp.getText().toString().equals("0"))
                    {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else {
                        addProduct("1");
                    }
                }*/
                /*else if (binding.edtdescription1.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description 1", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtSpecification.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Specification", Toasty.LENGTH_SHORT, true).show();
                }*/ /*else if (binding.entwarranty.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Warranty ", Toasty.LENGTH_SHORT, true).show();
            } */
                else if (binding.edtmrp.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                }

                /*else if (binding.entmrp.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsearchtag.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour ", Toasty.LENGTH_SHORT, true).show();
            }*/ /*else if (binding.edtdiscount.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Discount ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsale.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Sale", Toasty.LENGTH_SHORT, true).show();
            } */ else if (fileArrayList.size() != 3) {
                    Toasty.info(AddProductActivity.this, "Select Product Image", Toasty.LENGTH_SHORT, true).show();
                } else {

                    if (Utils.isNetwork(AddProductActivity.this)) {
                        addProduct("1");
                    } else {
                        Toast.makeText(AddProductActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        public void AddProductLiveClick(View view) {
            if (from.equals("edit")) {
                /*add image to array list*/
                fileArrayList = new ArrayList<>();
                if (fileImage1 != null) {
                    fileArrayList.add(fileImage1);
                }
                if (fileImage2 != null) {
                    fileArrayList.add(fileImage2);
                }
                if (fileImage3 != null) {
                    fileArrayList.add(fileImage3);
                }
                if (binding.entproductName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Product Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtBrandName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Brand Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (categoryId.equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Category ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtdescription.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description ", Toasty.LENGTH_SHORT, true).show();
                }
                else if (binding.edtSearchTagm.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Search Tags ", Toasty.LENGTH_SHORT, true).show();
                }
                //oncall commented 01/06/2023
             /*   else if(flag.equalsIgnoreCase("0")) {
                    if (binding.edtmrp.getText().toString().equals("")) {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else if(binding.edtmrp.getText().toString().equals("0"))
                    {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else {
                        updateProduct("0");
                    }
                }*/
                /*else if (binding.edtdescription1.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description 1", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtSpecification.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Specification", Toasty.LENGTH_SHORT, true).show();
                } *//*else if (binding.entwarranty.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Warranty ", Toasty.LENGTH_SHORT, true).show();
            } */


                else if (binding.edtmrp.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                }

                /*else if (binding.entmrp.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsearchtag.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour ", Toasty.LENGTH_SHORT, true).show();
            }*/ /*else if (binding.edtdiscount.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Discount ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsale.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Sale", Toasty.LENGTH_SHORT, true).show();
            } */ /*else if (fileArrayList.size() != 3) {
                    Toasty.info(AddProductActivity.this, "Select Product Image", Toasty.LENGTH_SHORT, true).show();
                }*/ else {

                    if (Utils.isNetwork(AddProductActivity.this)) {
                        updateProduct("0");
                    } else {
                        Toast.makeText(AddProductActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                /*add image to array list*/
                fileArrayList = new ArrayList<>();
                if (fileImage1 != null) {
                    fileArrayList.add(fileImage1);
                }
                if (fileImage2 != null) {
                    fileArrayList.add(fileImage2);
                }
                if (fileImage3 != null) {
                    fileArrayList.add(fileImage3);
                }
                if (binding.entproductName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Product Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtBrandName.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Brand Name ", Toasty.LENGTH_SHORT, true).show();
                } else if (categoryId.equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Category ", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtdescription.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description ", Toasty.LENGTH_SHORT, true).show();
                }
                else if (binding.edtSearchTagm.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Search Tags ", Toasty.LENGTH_SHORT, true).show();
                }
                //oncall commented
             /*   else if(flag.equalsIgnoreCase("0")) {
                    if (binding.edtmrp.getText().toString().equals("")) {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else if(binding.edtmrp.getText().toString().equals("0"))
                    {
                        Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                    }
                    else {
                        addProduct("0");
                    }
                }
*/
                /*else if (binding.edtdescription1.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter description 1", Toasty.LENGTH_SHORT, true).show();
                } else if (binding.edtSpecification.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter Specification", Toasty.LENGTH_SHORT, true).show();
                }*/ /*else if (binding.entwarranty.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Warranty ", Toasty.LENGTH_SHORT, true).show();
            } */


                else if (binding.edtmrp.getText().toString().equals("")) {
                    Toasty.info(AddProductActivity.this, "Enter MRP", Toasty.LENGTH_SHORT, true).show();
                }

                /*else if (binding.entmrp.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsearchtag.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Colour ", Toasty.LENGTH_SHORT, true).show();
            }*/ /*else if (binding.edtdiscount.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Discount ", Toasty.LENGTH_SHORT, true).show();
            } else if (binding.edtsale.getText().toString().equals("")) {
                Toasty.info(AddProductActivity.this, "Enter Sale", Toasty.LENGTH_SHORT, true).show();
            } */ else if (fileArrayList.size() != 3) {
                    Toasty.info(AddProductActivity.this, "Select Product Image", Toasty.LENGTH_SHORT, true).show();
                } else {

                    if (Utils.isNetwork(AddProductActivity.this)) {
                        addProduct("0");
                    } else {
                        Toast.makeText(AddProductActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onImage1Click(View view) {
            var = 1;
            checkPermission();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onImage2Click(View view) {
            var = 2;
            checkPermission();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onImage3Click(View view) {
            var = 3;
            checkPermission();
        }
    }

    private void onSpinnerSelected() {
        binding.spinproductcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                categoryId = productCategoryModelList.get(i).getCategoryId();
                if (categoryId.equals("")) {
                    //    Toast.makeText(AddProductActivity.this, "Select Category", Toast.LENGTH_SHORT).show();
                } else {
                    subCategoryList(categoryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinproductsubcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                subCategoryId = subCategoryModelArrayList.get(i).getSubCategoryId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                brandId = productBrandModelList.get(i).getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void categoryList() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.categoryList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    productCategoryModelList = new ArrayList<>();
                    AddProductCategoryResponseModel model1 = new AddProductCategoryResponseModel();

                    model1.setCategoryId("");
                    model1.setCategoryName("Select Category");
                    productCategoryModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddProductCategoryResponseModel model = new AddProductCategoryResponseModel();

                            model.setCategoryId(jsonObject1.getString("category_id"));
                            model.setCategoryName(jsonObject1.getString("category_name"));

                            productCategoryModelList.add(model);
                        }
                    }
                    setCategory(productCategoryModelList, binding.spinproductcategory);

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

    private void subCategoryList(String categoryId) {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setCategory_id(categoryId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.subCategoryList(categoryId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    subCategoryModelArrayList = new ArrayList<>();
                    SubCategoryModel model1 = new SubCategoryModel();

                    model1.setSubCategoryId("");
                    model1.setSubCategoryName("Select Sub Category");
                    subCategoryModelArrayList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            SubCategoryModel model = new SubCategoryModel();

                            model.setSubCategoryId(jsonObject1.getString("sub_category_id"));
                            model.setSubCategoryName(jsonObject1.getString("sub_category_name"));

                            subCategoryModelArrayList.add(model);
                        }
                    }
                    setSubCategory(subCategoryModelArrayList, binding.spinproductsubcategory);

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

    private void getProductDetails(boolean isLoading) {
        dialog.show();
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId(SharedPref.getVal(AddProductActivity.this, SharedPref.user_id));
        model.setProduct_id(productId);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.productDetail(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ProductDetailsDataModel>>() {
                        }.getType();
                        productDataModelList = new ArrayList<>();
                        productDataModelList.addAll(gson.fromJson(jsonObject.getString("product Detail"), listType));

                        setData();
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                dismissDialog();
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

    private void setData() {
        ProductDetailsDataModel model = productDataModelList.get(0);

        binding.image1.setVisibility(View.VISIBLE);
        binding.imgCameraimage1.setVisibility(View.GONE);
        Glide.with(AddProductActivity.this).load(model.getProductImages().get(0).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.image1);
        binding.edtdescription.setText(model.getDescription());

        binding.image2.setVisibility(View.VISIBLE);
        binding.imgCameraimage2.setVisibility(View.GONE);
        Glide.with(AddProductActivity.this).load(model.getProductImages().get(1).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.image2);
        binding.edtdescription1.setText(model.getDescription2());

        binding.image3.setVisibility(View.VISIBLE);
        binding.imgCameraimage3.setVisibility(View.GONE);
        Glide.with(AddProductActivity.this).load(model.getProductImages().get(2).getProductImage())
                .error(R.drawable.loader).placeholder(R.drawable.loader).into(binding.image3);
        binding.edtSpecification.setText(model.getSpecification());

        binding.entproductName.setText(model.getProductName());
        binding.edtmrp.setText(model.getMrp());
        binding.edtsale.setText(model.getSellingPrice());
        binding.edtdiscount.setText(model.getDiscountMrp());
        binding.entwarranty.setText(model.getWarranty());
        binding.edtSearchTagm.setText(model.getSearchTags());
        binding.edtColour.setText(model.getColor());
        binding.edtBrandName.setText(model.getBrandId());
        flag="0";

       /* if(model.getOnCall().equalsIgnoreCase("1"))
        {
            flag="1";
            binding.llPrice.setVisibility(View.GONE);
            binding.cboncall.setChecked(true);
        }
        else {
            flag="0";
            binding.llPrice.setVisibility(View.VISIBLE);
            binding.cboncall.setChecked(false);
        }
*/
        for (int i = 0; i < productCategoryModelList.size(); i++) {
            if (productCategoryModelList.get(i).getCategoryId().equals(model.getCategoryId())) {
                binding.spinproductcategory.setSelection(i);
                categoryId = model.getCategoryId();
            }
        }
        for (int i = 0; i < productBrandModelList.size(); i++) {
            if (productBrandModelList.get(i).getCategoryId().equals(model.getBrandId())) {
                binding.spinBrand.setSelection(i);
                brandId = model.getCategoryId();
            }
        }
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);
   /*     //compress the image using Compressor lib
        Timber.d("size of image before compression --> " + file.getTotalSpace());
        compressedImageFile = new Compressor(this).compressToFile(file);
        Timber.d("size of image after compression --> " + compressedImageFile.getTotalSpace());*/
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
      /*  RequestBody requestFile =
                RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);*/

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    private MultipartBody.Part prepareFilePartEmpty(String partName) {

        // use the FileUtils to get the actual file by uri
//        File file = FileUtils.getFile(this, fileUri);
   /*     //compress the image using Compressor lib
        Timber.d("size of image before compression --> " + file.getTotalSpace());
        compressedImageFile = new Compressor(this).compressToFile(file);
        Timber.d("size of image after compression --> " + compressedImageFile.getTotalSpace());*/
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), "");
      /*  RequestBody requestFile =
                RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);*/

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, "", requestFile);
    }

    List<MultipartBody.Part> uploadImages(List<File> paths) {
        List<MultipartBody.Part> list = new ArrayList<>();
        int i = 0;
        for (File uri : paths) {
            String fileName = uri.getName();
            Uri uri1 = Uri.fromFile(uri);
            //very important files[]
            MultipartBody.Part imageRequest = prepareFilePart("product_images[]", uri1);
            list.add(imageRequest);
        }
        return list;
    }

    List<MultipartBody.Part> uploadImagesEmpty() {
        List<MultipartBody.Part> list = new ArrayList<>();
        int i = 0;
        MultipartBody.Part imageRequest = prepareFilePartEmpty("product_images[]");
        list.add(imageRequest);
        return list;
    }

    private void addProduct(String is_active) {
        dialog.show();
        List<MultipartBody.Part> list;
        list = uploadImages(fileArrayList);

        String userIdStr = SharedPref.getVal(AddProductActivity.this, SharedPref.user_id);
        String shopIdStr = SharedPref.getVal(AddProductActivity.this, SharedPref.shop_id);
        String oncall_flag=flag;
        RequestBody user_id =
                RequestBody.create(MediaType.parse("text/plain"), userIdStr);
        RequestBody shop_id =
                RequestBody.create(MediaType.parse("text/plain"), shopIdStr);
        RequestBody product_name =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.entproductName.getText()).toString());
       /* RequestBody brand_id =
                RequestBody.create(MediaType.parse("text/plain"), brandId);*/
        RequestBody brand_id =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtBrandName.getText()).toString());
        RequestBody category_id =
                RequestBody.create(MediaType.parse("text/plain"), categoryId);
        RequestBody sub_category_id =
                RequestBody.create(MediaType.parse("text/plain"), subCategoryId);
        RequestBody description =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtdescription.getText()).toString());
        RequestBody description2 =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtdescription1.getText()).toString());
        RequestBody specification =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtSpecification.getText()).toString());
        RequestBody warranty =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.entwarranty.getText()).toString());
        RequestBody size_id =
                RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody color =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtColour.getText()).toString());
        RequestBody search_tags =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtSearchTagm.getText()).toString());
        RequestBody mrp =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtmrp.getText()).toString());
        RequestBody discount_mrp =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtdiscount.getText()).toString());
        RequestBody selling_price =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtsale.getText()).toString());
        RequestBody isActive =
                RequestBody.create(MediaType.parse("text/plain"), is_active);
        RequestBody flag_on =
                RequestBody.create(MediaType.parse("text/plain"), oncall_flag);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.addproduct(product_name, brand_id, category_id, sub_category_id, description, description2, specification, warranty, size_id, color, search_tags, mrp,
                discount_mrp, selling_price, user_id, shop_id, isActive,flag_on, list).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
//                        Toast.makeText(AddProductActivity.this, "Product Added successfully", Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(AddProductActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Product added successfully and sent to our team for verification.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        finish();
                                    }
                                })
                                .show();

                    } else {
                        Toast.makeText(AddProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                dismissDialog();
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

    private void updateProduct(String is_active) {
        dialog.show();
        List<MultipartBody.Part> list;
        if (fileArrayList.size() == 0) {
            list = uploadImagesEmpty();
        } else {
            list = uploadImages(fileArrayList);
        }

        RequestBody product_id =
                RequestBody.create(MediaType.parse("text/plain"), productId);
        RequestBody user_id =
                RequestBody.create(MediaType.parse("text/plain"), SharedPref.getVal(AddProductActivity.this, SharedPref.user_id));
        RequestBody shop_id =
                RequestBody.create(MediaType.parse("text/plain"), SharedPref.getVal(AddProductActivity.this, SharedPref.shop_id));
        RequestBody product_name =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.entproductName.getText()).toString());
//        RequestBody brand_id =
//                RequestBody.create(MediaType.parse("text/plain"), brandId);
        RequestBody brand_id =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtBrandName.getText()).toString());
        RequestBody category_id =
                RequestBody.create(MediaType.parse("text/plain"), categoryId);
        RequestBody description =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtdescription.getText()).toString());
        RequestBody description2 =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtdescription1.getText()).toString());
        RequestBody specification =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtSpecification.getText()).toString());
        RequestBody warranty =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.entwarranty.getText()).toString());
        RequestBody size_id =
                RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody color =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtColour.getText()).toString());
        RequestBody search_tags =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtSearchTagm.getText()).toString());
        RequestBody mrp =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtmrp.getText()).toString());
        RequestBody discount_mrp =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtdiscount.getText()).toString());
        RequestBody selling_price =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtsale.getText()).toString());
        RequestBody searchTag =
                RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(binding.edtSearchTagm.getText()).toString());
        RequestBody isActive =
                RequestBody.create(MediaType.parse("text/plain"), is_active);
        RequestBody flag_onu =
                RequestBody.create(MediaType.parse("text/plain"), flag);


        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.updateProduct(product_id, product_name, brand_id, category_id, searchTag, description, description2, specification, warranty, size_id, color, search_tags, mrp,
                discount_mrp, selling_price, user_id, shop_id, isActive, flag_onu, list).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
//                        Toast.makeText(AddProductActivity.this, "Product Updated successfully", Toast.LENGTH_SHORT).show();


                        new AlertDialog.Builder(AddProductActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Product updated successfully and sent to our team for verification.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(AddProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                dismissDialog();
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

    private void updateProductImage(String image_id, String selectedFile) {
        dialog.show();
        MultipartBody.Part document = null;
        if (selectedFile != null) {
            if (!selectedFile.equals("")) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(selectedFile));
                document = MultipartBody.Part.createFormData("product_images", new File(selectedFile).getName(), requestFile);
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                document = MultipartBody.Part.createFormData("product_images", "", requestFile);
            }
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            document = MultipartBody.Part.createFormData("document", "", requestFile);
        }

        RequestBody product_id =
                RequestBody.create(MediaType.parse("text/plain"), productId);
        RequestBody user_id =
                RequestBody.create(MediaType.parse("text/plain"), SharedPref.getVal(AddProductActivity.this, SharedPref.user_id));
        RequestBody imageId =
                RequestBody.create(MediaType.parse("text/plain"), image_id);

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.productImageupdate(product_id, user_id, imageId, document).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(AddProductActivity.this, "Product Image Updated successfully", Toast.LENGTH_SHORT).show();
//                        finish();
                    } else {
                        Toast.makeText(AddProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable error) {
                // binding.recProductlist.hideShimmer();
                dismissDialog();
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

    private void brandList() {
        UserProfileRequestModel model = new UserProfileRequestModel();
        model.setUserId("");

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.brandList(model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    productBrandModelList = new ArrayList<>();
                    AddProductCategoryResponseModel model1 = new AddProductCategoryResponseModel();

                    model1.setCategoryId("");
                    model1.setCategoryName("Select Brand");
                    productBrandModelList.add(model1);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            AddProductCategoryResponseModel model = new AddProductCategoryResponseModel();

                            model.setCategoryId(jsonObject1.getString("brand_id"));
                            model.setCategoryName(jsonObject1.getString("brand_name"));

                            productBrandModelList.add(model);
                        }
                    }
                    setCategory(productBrandModelList, binding.spinBrand);

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

    private void setCategory(ArrayList<AddProductCategoryResponseModel> arrayList, AppCompatSpinner spinnershopfont) {

        ArrayAdapter<AddProductCategoryResponseModel> modelArrayAdapter = new ArrayAdapter<AddProductCategoryResponseModel>(AddProductActivity.this,
                R.layout.custom_spinner_shopfont, arrayList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);
                //  colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                font_name.setText(arrayList.get(position).getCategoryName());
                //font_name.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getBgColorName()));


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                switch (position) {
                    case 0:
                        font_name.setTextColor(Color.GRAY);
                        break;
                    default:
                        font_name.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont1, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);

                font_name.setText(arrayList.get(position).getCategoryName());


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                switch (position) {
                    case 0:
                        font_name.setTextColor(Color.GRAY);
                        break;
                    default:
                        font_name.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    private void setSubCategory(ArrayList<SubCategoryModel> arrayList, AppCompatSpinner spinnershopfont) {

        ArrayAdapter<SubCategoryModel> modelArrayAdapter = new ArrayAdapter<SubCategoryModel>(AddProductActivity.this,
                R.layout.custom_spinner_shopfont, arrayList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);
                //  colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                font_name.setText(arrayList.get(position).getSubCategoryName());
                //font_name.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getBgColorName()));


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                switch (position) {
                    case 0:
                        font_name.setTextColor(Color.GRAY);
                        break;
                    default:
                        font_name.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont1, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);

                font_name.setText(arrayList.get(position).getSubCategoryName());


//                image.setImageResource(paymentModeArrayList.get(position).getImage());

                switch (position) {
                    case 0:
                        font_name.setTextColor(Color.GRAY);
                        break;
                    default:
                        font_name.setTextColor(Color.BLACK);
                        break;
                }
                return v;
            }
        };
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(AddProductActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(AddProductActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(AddProductActivity.this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (AddProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (AddProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale
                    (AddProductActivity.this, Manifest.permission.CAMERA)) {

                Snackbar.make(AddProductActivity.this.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                        PERMISSIONS_MULTIPLE_REQUEST);
//                                }
                            }
                        }).show();
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission
                                .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSIONS_MULTIPLE_REQUEST);
//                }
            }
        } else {
            // write your logic code if permission already granted

            boolean isclick = true;
            if (isclick) {
//                imgUri = openCameraIntent();
                ImagePicker.Companion.with(this)
                        .start();
               /* ImagePicker.Companion.with(this)
                        .saveDir(directory)
                        .compress(1024)
                        .start();*/
                isclick = false;
            } else {
//                dialog.dismiss();
                isclick = true;
                isclick = true;
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(intent);
            if (resuleCode == RESULT_OK) {
                try {
                    Uri uri = result.getUri();

                    if (uri.getPath().length() > 0) {
                        File file = new File(uri.getPath());

                        Uri mImageUri = Uri.fromFile(new File(uri.getPath()));
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                            selectedImageBitmap = getResizedBitmap(selectedImageBitmap, 500);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ExifInterface ei = new ExifInterface(uri.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                selectedImageBitmap = rotateImage(selectedImageBitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                selectedImageBitmap = rotateImage(selectedImageBitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                selectedImageBitmap = rotateImage(selectedImageBitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                selectedImageBitmap = selectedImageBitmap;
                        }

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                        if (var == 1) {
                            binding.image1.setImageBitmap(selectedImageBitmap);
                            binding.imgCameraimage1.setVisibility(View.GONE);
                            binding.image1.setVisibility(View.VISIBLE);
                            fileImage1 = file;

                            if (from.equals("edit")) {
                                imageId = productDataModelList.get(0).getProductImages().get(0).getProductImageID();
                                updateProductImage(imageId, fileImage1.getPath());
                            }
//                        binding.image1.setVisibility(View.VISIBLE);
                        } else if (var == 2) {
                            binding.image2.setImageBitmap(selectedImageBitmap);
                            binding.imgCameraimage2.setVisibility(View.GONE);
                            binding.image2.setVisibility(View.VISIBLE);
                            fileImage2 = file;

                            if (from.equals("edit")) {
                                imageId = productDataModelList.get(0).getProductImages().get(1).getProductImageID();
                                updateProductImage(imageId, fileImage2.getPath());
                            }
//                        binding.image1.setVisibility(View.VISIBLE);
                        } else if (var == 3) {
                            binding.image3.setImageBitmap(selectedImageBitmap);
                            fileImage3 = file;
                            binding.imgCameraimage3.setVisibility(View.GONE);
                            binding.image3.setVisibility(View.VISIBLE);

                            if (from.equals("edit")) {
                                imageId = productDataModelList.get(0).getProductImages().get(2).getProductImageID();
                                updateProductImage(imageId, fileImage3.getPath());
                            }
//                        binding.image1.setVisibility(View.VISIBLE);
                        }
//                    binding.imgProfile.setImageURI(uri);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Image Captured", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //From here you can load the image however you need to, I recommend using the Glide library

            } else if (resuleCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == 2404 && resuleCode == RESULT_OK) {
            try {
                Uri uri = intent.getData();
//                File file = ImagePicker.Companion.getFile(intent);

                /*image cropping*/
                CropImage.activity(uri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(3, 3)
                        .setAutoZoomEnabled(false)
                        .setFixAspectRatio(true)
                        .start(this);


            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (resuleCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(intent), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && readExternalFile && writeExternalFile) {
                        // write your logic here
                        boolean isclick = true;
                        if (isclick) {
//                            imgUri = openCameraIntent();
                            ImagePicker.Companion.with(this)
                                    .start();
                         /*   ImagePicker.Companion.with(this)
                                    .saveDir(directory)
                                    .compress(1024)
                                    .start();*/
                            isclick = false;
                        } else {
//                            dialog.dismiss();
                            isclick = true;
                        }
                    } else {
                        Snackbar.make(AddProductActivity.this.findViewById(android.R.id.content),
                                "Please Grant Permissions to upload photo",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{Manifest.permission
                                                            .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                                    PERMISSIONS_MULTIPLE_REQUEST);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

}

