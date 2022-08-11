package com.foodproject.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String SP_LOGIN_APP = "spLoginApp";

    public static final String SP_NAME= "spName";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_TOKEN = "spToken";

    public static final String SP_READY_LOGIN = "spReadyLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;


    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_LOGIN_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPName(){
        return sp.getString(SP_NAME, "");
    }

    public String getSPEmail(){
        return sp.getString(SP_EMAIL, "");
    }

    public String getSPToken(){
        return sp.getString(SP_TOKEN, "");
    }

    public Boolean getSPReadyLogin(){
        return sp.getBoolean(SP_READY_LOGIN, false);
    }

}