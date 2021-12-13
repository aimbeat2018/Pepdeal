package com.pepdeal.in.constants;

import com.pepdeal.in.model.ForgotPasswordOTPRequestModel;
import com.pepdeal.in.model.UserProfileUpdateModel;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;
import com.pepdeal.in.model.requestModel.LoginRequestModel;
import com.pepdeal.in.model.requestModel.OTPRequestModel;
import com.pepdeal.in.model.requestModel.ResetPasswordRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

   /* @FormUrlEncoded
    @POST("send_otp")
    Call<ResponseBody> send_otp(@Field("mobile_no") String mobile_no);
*/



    @POST("send_otp")
    Call<ResponseBody> send_otp(@Body OTPRequestModel requestModel);

    @POST("forgotpassword_send_otp")
    Call<ResponseBody> forgotpassword_send_otp(@Body ForgotPasswordOTPRequestModel requestModel);


/*

    @POST("registerUser")
    Call<ResponseBody> registerUser(@Field("username") String username,@Field("password") String password,@Field("mobile_number") String mobile_no,@Field("device_token") String devicetoken);
*/



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
    Call<ResponseBody> bgcolorList(@Body AddBackgroundColorResponseModel addBackgroundColorResponseModel);


    @POST("fontstyleList")
    Call<ResponseBody> fontstyleList(@Body AddShopFontResponseModel addShopFontResponseModel);

    @POST("categoryList")
    Call<ResponseBody> categoryList(AddProductCategoryResponseModel addProductCategoryResponseModel);

}
