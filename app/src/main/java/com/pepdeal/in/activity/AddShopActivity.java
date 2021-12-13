package com.pepdeal.in.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.pepdeal.in.databinding.ActivityAddShopBinding;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AddShopActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ActivityAddShopBinding binding;


    ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList = new ArrayList<>();
    ArrayList<AddShopFontResponseModel> shopFontModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop);
        binding.setHandler(new ClickHandler());

        binding.spinbackcolor.setOnItemSelectedListener(this);
        binding.spinfont.setOnItemSelectedListener(this);


    }


    @Override
    protected void onResume() {
        requestBackgroundColor();
        requestShopFont();
        super.onResume();
    }

    private void requestShopFont() {

        AddShopFontResponseModel addShopFontResponseModel = new AddShopFontResponseModel();

        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.fontstyleList(addShopFontResponseModel).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String colordata = jsonObject.getString("msgs");
                    if (colordata.equals("Font Style List")) {
                        shopFontModelList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                           shopFontModelList = new ArrayList<>();

                            AddShopFontResponseModel addShopFontResponseModel = new AddShopFontResponseModel();
                            addShopFontResponseModel.setFontStyleId(jsonObject1.getString("font_style_id"));
                            addShopFontResponseModel.setFontStyleName(jsonObject1.getString("font_style_name"));
                            addShopFontResponseModel.setIsActive(jsonObject1.getString("isActive"));
                            addShopFontResponseModel.setCreatedAt(jsonObject1.getString("created_at"));
                            addShopFontResponseModel.setUpdatedAt(jsonObject1.getString("updated_at"));


                            shopFontModelList.add(addShopFontResponseModel);

                            setShopFontStyle(shopFontModelList, binding.spinfont);


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
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopFontStyle(ArrayList<AddShopFontResponseModel> shopFontModelList, AppCompatSpinner spinnershopfont) {


        ArrayAdapter<AddShopFontResponseModel> modelArrayAdapter = new ArrayAdapter<AddShopFontResponseModel>(AddShopActivity.this,
                R.layout.custom_spinner_shopfont, shopFontModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);
                TextView font_id = (TextView) v.findViewById(R.id.shopfont);
                //  colorid.setText(backgroundcolorModelList.get(position).getIsActive());
              font_name.setText(shopFontModelList.get(position).getFontStyleName());
                //font_name.setBackgroundColor(Color.parseColor(shopFontModelList.get(position).getBgColorName()));


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
                    v = vi.inflate(R.layout.custom_spinner_shopfont, null);
                }

                TextView font_name = (TextView) v.findViewById(R.id.fontname);
                TextView font_id = (TextView) v.findViewById(R.id.shopfont);

                font_name.setText(shopFontModelList.get(position).getFontStyleName());


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
        spinnershopfont.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }


    private void requestBackgroundColor() {

        AddBackgroundColorResponseModel addBackgroundColorResponseModel = new AddBackgroundColorResponseModel();
       /* addBackgroundColorResponseModel.setBgColorId(bgcolor_id);
        addBackgroundColorResponseModel.setBgColorName(bgcolor_name);
*/
        ApiInterface client = ApiClient.createService(ApiInterface.class, "", "");
        client.bgcolorList(addBackgroundColorResponseModel).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String colordata = jsonObject.getString("msgs");
                    if (colordata.equals("Background Color List")) {
                        backgroundcolorModelList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            backgroundcolorModelList = new ArrayList<>();

                            AddBackgroundColorResponseModel addBackgroundColorResponseModel1 = new AddBackgroundColorResponseModel();

                            addBackgroundColorResponseModel1.setBgColorId(jsonObject1.getString("bg_color_id"));
                            addBackgroundColorResponseModel1.setBgColorName(jsonObject1.getString("bg_color_name"));
                            addBackgroundColorResponseModel1.setIsActive(jsonObject1.getString("isActive"));
                            addBackgroundColorResponseModel1.setCreatedAt(jsonObject1.getString("created_at"));
                            addBackgroundColorResponseModel1.setUpdatedAt(jsonObject1.getString("updated_at"));


                            backgroundcolorModelList.add(addBackgroundColorResponseModel1);

                            setShopBackgroundcolor(backgroundcolorModelList, binding.spinbackcolor);


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
                            Toast.makeText(AddShopActivity.this, getString(R.string.unauthorised_user), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_FORBIDDEN:
                            Toast.makeText(AddShopActivity.this, getString(R.string.forbidden), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                            Toast.makeText(AddShopActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                            break;
                        case HttpsURLConnection.HTTP_BAD_REQUEST:
                            Toast.makeText(AddShopActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(AddShopActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setShopBackgroundcolor(ArrayList<AddBackgroundColorResponseModel> backgroundcolorModelList, AppCompatSpinner spinbackcolor) {


        ArrayAdapter<AddBackgroundColorResponseModel> modelArrayAdapter = new ArrayAdapter<AddBackgroundColorResponseModel>(AddShopActivity.this,
                R.layout.custom_spinner_backgroundcolor, backgroundcolorModelList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor, null);
                }
                TextView colorname = (TextView) v.findViewById(R.id.bacKcolor);
                TextView colorid = (TextView) v.findViewById(R.id.colorname);
                //  colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                // colorname.setText(backgroundcolorModelList.get(position).getBgColorName());
                colorname.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(position).getBgColorName()));


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
                    v = vi.inflate(R.layout.custom_spinner_backgroundcolor, null);
                }

                TextView colorname = (TextView) v.findViewById(R.id.bacKcolor);
                TextView colorid = (TextView) v.findViewById(R.id.colorname);

                //colorid.setText(backgroundcolorModelList.get(position).getIsActive());
                //colorname.setText(backgroundcolorModelList.get(position).getBgColorName());
                colorname.setBackgroundColor(Color.parseColor(backgroundcolorModelList.get(position).getBgColorName()));


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
        spinbackcolor.setAdapter(modelArrayAdapter);
        modelArrayAdapter.notifyDataSetChanged();


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // Toast.makeText(getApplicationContext(), colorNames[position], Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ClickHandler {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onAddShop(View view) {

        }


    }


}