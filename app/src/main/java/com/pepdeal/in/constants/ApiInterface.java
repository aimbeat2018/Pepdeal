package com.pepdeal.in.constants;

import com.pepdeal.in.model.ForgotPasswordOTPRequestModel;
import com.pepdeal.in.model.UserProfileUpdateModel;
import com.pepdeal.in.model.requestModel.AddBackgroundColorResponseModel;
import com.pepdeal.in.model.requestModel.AddProductCategoryResponseModel;
import com.pepdeal.in.model.requestModel.AddShopFontResponseModel;
import com.pepdeal.in.model.requestModel.AddShopRequestModel;
import com.pepdeal.in.model.requestModel.HelpRequestModel;
import com.pepdeal.in.model.requestModel.LoginRequestModel;
import com.pepdeal.in.model.requestModel.OTPRequestModel;
import com.pepdeal.in.model.requestModel.ResetPasswordRequestModel;
import com.pepdeal.in.model.requestModel.SellerServiceRequestModel;
import com.pepdeal.in.model.requestModel.SellerTicketStatusModel;
import com.pepdeal.in.model.requestModel.SendMessageRequestModel;
import com.pepdeal.in.model.requestModel.UserProfileRequestModel;
import com.pepdeal.in.model.requestModel.UserRegisterModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    @POST("enquiryForm")
    Call<ResponseBody> enquiryForm(@Body HelpRequestModel model);

    @POST("aboutshopupdate")
    Call<ResponseBody> aboutshopupdate(@Body AddShopRequestModel model);

    @POST("bgcolorupdate")
    Call<ResponseBody> bgcolorupdate(@Body AddShopRequestModel model);

    @POST("fontstyleList")
    Call<ResponseBody> fontstyleList(@Body UserProfileRequestModel model);

    @POST("fontcolorList")
    Call<ResponseBody> fontcolorList(@Body UserProfileRequestModel model);

    @POST("fontsizeList")
    Call<ResponseBody> fontsizeList(@Body UserProfileRequestModel model);

    @POST("categoryList")
    Call<ResponseBody> categoryList(@Body UserProfileRequestModel model);

    @FormUrlEncoded
    @POST("subCategoryList")
    Call<ResponseBody> subCategoryList(@Field("category_id") String category_id);

    @POST("shopMsgForUser")
    Call<ResponseBody> shopMsgForUser(@Body UserProfileRequestModel model);

    @POST("msgsList")
    Call<ResponseBody> msgsList(@Body UserProfileRequestModel model);

    @POST("brandList")
    Call<ResponseBody> brandList(@Body UserProfileRequestModel model);

    @POST("productList")
    Call<ResponseBody> productList(@Body UserProfileRequestModel model);

    @POST("favouriteList")
    Call<ResponseBody> favouriteList(@Body UserProfileRequestModel model);

    @POST("homePage")
    Call<ResponseBody> homePage(@Body UserProfileRequestModel model);

    @POST("citySearch")
    Call<ResponseBody> citySearch(@Body UserProfileRequestModel model);

    @POST("addFavourite")
    Call<ResponseBody> addFavourite(@Body UserProfileRequestModel model);

    @POST("favouriteRemove")
    Call<ResponseBody> favouriteRemove(@Body UserProfileRequestModel model);

    @POST("deleteproduct")
    Call<ResponseBody> deleteproduct(@Body UserProfileRequestModel model);

    @POST("productstatusChange ")
    Call<ResponseBody> productstatusChange(@Body UserProfileRequestModel model);

    @POST("productDetail")
    Call<ResponseBody> productDetail(@Body UserProfileRequestModel model);

    @POST("addTicket")
    Call<ResponseBody> addTicket(@Body UserProfileRequestModel model);

    @POST("ticketList")
    Call<ResponseBody> ticketList(@Body UserProfileRequestModel model);

    @POST("addSupershop")
    Call<ResponseBody> addSupershop(@Body UserProfileRequestModel model);

    @POST("removeSupershop")
    Call<ResponseBody> removeSupershop(@Body UserProfileRequestModel model);

    @POST("userinterest")
    Call<ResponseBody> userinterest(@Body UserProfileRequestModel model);

    @POST("supershopList")
    Call<ResponseBody> supershopList(@Body UserProfileRequestModel model);

    @POST("shopListWithDetail")
    Call<ResponseBody> shopListWithDetail(@Body UserProfileRequestModel model);

    @POST("sellerwiseTicket")
    Call<ResponseBody> sellerwiseTicket(@Body UserProfileRequestModel model);

    @POST("sellerServices")
    Call<ResponseBody> sellerServices(@Body SellerServiceRequestModel model);

    @POST("sellerticketStatus")
    Call<ResponseBody> sellerticketStatus(@Body SellerTicketStatusModel model);

    @POST("userstatusChange")
    Call<ResponseBody> userstatusChange(@Body SellerTicketStatusModel model);

    @POST("searchTags")
    Call<ResponseBody> searchTags(@Body UserProfileRequestModel model);

    @POST("sendMsgs")
    Call<ResponseBody> sendMsgs(@Body SendMessageRequestModel model);

    @POST("msgsCheckingstatus")
    Call<ResponseBody> msgsCheckingstatus(@Body SendMessageRequestModel model);

    @Multipart
    @POST("addproduct")
    Call<ResponseBody> addproduct(@Part("product_name") RequestBody product_name,
                                  @Part("brand_id") RequestBody brand_id,
                                  @Part("category_id") RequestBody category_id,
                                  @Part("sub_category_id") RequestBody sub_category_id,
                                  @Part("description") RequestBody description,
                                  @Part("description2") RequestBody description2,
                                  @Part("specification") RequestBody specification,
                                  @Part("warranty") RequestBody warranty,
                                  @Part("size_id") RequestBody size_id,
                                  @Part("color") RequestBody color,
                                  @Part("search_tags") RequestBody search_tags,
                                  @Part("mrp") RequestBody mrp,
                                  @Part("discount_mrp") RequestBody discount_mrp,
                                  @Part("selling_price") RequestBody selling_price,
                                  @Part("user_id") RequestBody user_id,
                                  @Part("shop_id") RequestBody shop_id,
                                  @Part("isActive") RequestBody isActive,
                                  @Part("on_call") RequestBody onflag,
                                  @Part List<MultipartBody.Part> productImages);

    @Multipart
    @POST("updateProduct")
    Call<ResponseBody> updateProduct(@Part("product_id") RequestBody product_id,
                                     @Part("product_name") RequestBody product_name,
                                     @Part("brand_id") RequestBody brand_id,
                                     @Part("category_id") RequestBody category_id,
                                     @Part("search_tag") RequestBody search_tag,
                                     @Part("description") RequestBody description,
                                     @Part("description2") RequestBody description2,
                                     @Part("specification") RequestBody specification,
                                     @Part("warranty") RequestBody warranty,
                                     @Part("size_id") RequestBody size_id,
                                     @Part("color") RequestBody color,
                                     @Part("search_tags") RequestBody search_tags,
                                     @Part("mrp") RequestBody mrp,
                                     @Part("discount_mrp") RequestBody discount_mrp,
                                     @Part("selling_price") RequestBody selling_price,
                                     @Part("user_id") RequestBody user_id,
                                     @Part("shop_id") RequestBody shop_id,
                                     @Part("isActive") RequestBody isActive,
                                     @Part("on_call") RequestBody onflag,
                                     @Part List<MultipartBody.Part> productImages);

    @Multipart
    @POST("productImageupdate")
    Call<ResponseBody> productImageupdate(@Part("product_id") RequestBody product_id,
                                          @Part("user_id") RequestBody user_id,
                                          @Part("image_id") RequestBody image_id,
                                          @Part MultipartBody.Part productImages);

    @POST("pagesList")
    Call<ResponseBody> pagesList(@Body UserProfileRequestModel model);

    @POST("userLogout")
    Call<ResponseBody> userLogout(@Body UserProfileRequestModel model);

    @POST("stateList")
    Call<ResponseBody> stateList(@Body UserProfileRequestModel model);

    @POST("cityListbystate")
    Call<ResponseBody> cityListbystate(@Body UserProfileRequestModel model);

    @POST("cityList")
    Call<ResponseBody> cityList(@Body UserProfileRequestModel model);

    @POST("chatListsponse")
    Call<ResponseBody> chatListsponse(@Body UserProfileRequestModel model);

    @POST("msgsCount")
    Call<ResponseBody> msgsCount(@Body UserProfileRequestModel model);

    @POST("msgscountStatuschange")
    Call<ResponseBody> msgscountStatuschange(@Body UserProfileRequestModel model);
}
