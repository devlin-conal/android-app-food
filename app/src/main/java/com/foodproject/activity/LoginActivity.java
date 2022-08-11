package com.foodproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.foodproject.R;
import com.foodproject.Utils.AndroidUtil;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.ApiClient;
import com.foodproject.api.ApiInterface;
import com.foodproject.api.response.BaseResponse;
import com.foodproject.api.response.TokenResponse;
import com.foodproject.model.login.User;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button mBtnSignIn;
    private static final String TAG = "LoginActivity";
    ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;
    EditText etEmail;
    EditText etPassword;
    Context mContext;
    Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        setupWidgets();

    }

    private void initialize() {
        mBtnSignIn = findViewById(R.id.btn_sign);
        mContext = this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);
        etEmail = findViewById(R.id.edt_email);
        etPassword = findViewById(R.id.edt_password);
        gson = new Gson();
    }

    private void setupWidgets() {

        //change status bar color to transparent
        Window window = getWindow();
        AndroidUtil.statusBarColorTransparentWithKeyboard(this, window);

        //set the background image in login screen
//        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.splash4));

        //go to home
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<BaseResponse> postLogin = apiInterface.postLogin(new User(etEmail.getText().toString(), etPassword.getText().toString()));
                postLogin.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                        progressDialog.dismiss();

                        if (response.code() == 200) {
                            BaseResponse user = response.body();
                            TokenResponse token = gson.fromJson(user.getResult(), TokenResponse.class);
                            sharedPrefManager.saveSPString(SharedPrefManager.SP_TOKEN, "Bearer " + token.getToken());
                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_READY_LOGIN, true);
                        } else {
                            startActivity(new Intent(mContext, HomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                            Toast.makeText(mContext, "Emai/Password salah", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
//                        progressDialog.dismiss();
                        Log.d("failed", ""+t+"\n"+call);
                        Toast.makeText(mContext, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
//                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//                startActivity(i);
//                finish();
            }
        });
    }
}
