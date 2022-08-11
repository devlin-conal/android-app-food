package com.foodproject.api;

import com.foodproject.api.response.BaseResponse;
import com.foodproject.model.login.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiInterface {

    @POST("admin/login")
    Call<BaseResponse> postLogin(@Body User user);

    @GET("admin/refreshtoken")
    Call<BaseResponse> refreshToken(@Header("Authorization") String token, @Header("isRefreshToken") boolean refresh);

    @GET("categories/getByPage")
    Call<BaseResponse> getCategories(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    @GET("restaurants/getByPage")
    Call<BaseResponse> getRestaurants(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    @GET
    Call<ResponseBody> getByUrl(@Header("Authorization") String token, @Url String url);
}
