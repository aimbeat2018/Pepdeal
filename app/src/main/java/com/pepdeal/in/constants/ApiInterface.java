package com.pepdeal.in.constants;

import com.pepdeal.in.model.ForgotPasswordOTPRequestModel;
import com.pepdeal.in.model.UserProfileUpdateModel;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.LoginRequestModel;
import com.pepdeal.in.model.requestModel.OTPRequestModel;
import com.pepdeal.in.model.requestModel.ResetPasswordRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("send_otp")
    Call<ResponseBody> send_otp(@Body OTPRequestModel requestModel);

    @POST("forgotpassword_send_otp")
    Call<ResponseBody> forgotpassword_send_otp(@Body ForgotPasswordOTPRequestModel requestModel);

    @POST("registerUser")
    Call<ResponseBody> registerUser(@Body UserRegisterModel userRegisterModel);


    @POST("forgot_password")
    Call<ResponseBody> forgot_password(@Body ResetPasswordRequestModel resetPasswordRequestModel);

    @POST("updateUserDetail")
    Call<ResponseBody> updateUserDetail(@Body UserProfileUpdateModel userProfileUpdateModel);

    @POST("user_detail")
    Call<ResponseBody> user_detail(@Body UserProfileRequestModel userProfileRequestModel);

    @POST("login")
    Call<ResponseBody> login(@Body LoginRequestModel loginRequestModel);

    @POST("bgcolorList")
    Call<ResponseBody> bgcolorList(@Body UserProfileRequestModel model);

    @POST("shopAdd")
    Call<ResponseBody> shopAdd(@Body AddShopRequestModel model);

    @POST("fontstyleList")
    Call<ResponseBody> fontstyleList(@Body UserProfileRequestModel model);

    @POST("fontsizeList")
    Call<ResponseBody> fontsizeList(@Body UserProfileRequestModel model);

    @POST("categoryList")
    Call<ResponseBody> categoryList(@Body UserProfileRequestModel model);

    @POST("brandList")
    Call<ResponseBody> brandList(@Body UserProfileRequestModel model);

    @Multipart
    @POST("addproduct")
    Call<ResponseBody> addproduct(@Part("product_name") RequestBody product_name,
                                  @Part("brand_id") RequestBody brand_id,
                                  @Part("category_id") RequestBody category_id,
                                  @Part("description") RequestBody description,
                                  @Part("warranty") RequestBody warranty,
                                  @Part("size_id") RequestBody size_id,
                                  @Part("color") RequestBody color,
                                  @Part("search_tags") RequestBody search_tags,
                                  @Part("mrp") RequestBody mrp,
                                  @Part("discount_mrp") RequestBody discount_mrp,
                                  @Part("selling_price") RequestBody selling_price,
                                  @Part("user_id") RequestBody user_id,
                                  @Part("isActive") RequestBody isActive,
                                  @Part List<MultipartBody.Part> productImages);
}
