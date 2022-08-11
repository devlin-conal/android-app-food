package com.foodproject.api.interceptor;

import android.content.Intent;
import com.foodproject.MyApp;
import com.foodproject.Utils.GsonUtil;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.activity.LoginActivity;
import com.foodproject.api.ApiClient;
import com.foodproject.api.ApiInterface;
import com.foodproject.api.response.BaseResponse;
import com.foodproject.api.response.TokenResponse;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TokenAuthenticator implements Interceptor {

    SharedPrefManager sharedPrefManager;

    public TokenAuthenticator() {

        sharedPrefManager = new SharedPrefManager(MyApp.getContext());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mainResponse = chain.proceed(chain.request());
        Request mainRequest = chain.request();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if ( mainResponse.code() == 401 || mainResponse.code() == 403 ) {
            String token = sharedPrefManager.getSPToken();
            retrofit2.Response<BaseResponse> refreshToken = apiInterface.refreshToken(token, true).execute();
            if (refreshToken.isSuccessful()) {
                TokenResponse resToken = GsonUtil.getGson().fromJson(refreshToken.body().getResult(), TokenResponse.class);
                sharedPrefManager.saveSPString(SharedPrefManager.SP_TOKEN, "Bearer " +
                        resToken.getToken());
                Request.Builder builder = mainRequest.newBuilder().header("Authorization",
                                sharedPrefManager.getSPToken())
                        .method(mainRequest.method(), mainRequest.body());
                mainResponse = chain.proceed(builder.build());
            }

            //Jika tidak ingin refresh token dan langsung logout
//            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
//            Intent i = new Intent(MyApp.getContext(), LoginActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            MyApp.getContext().startActivity(i);

        } else if ( mainResponse.code() == 500 ){
            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_READY_LOGIN, false);
            Intent i = new Intent(MyApp.getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApp.getContext().startActivity(i);
        }

        return mainResponse;
    }
}
