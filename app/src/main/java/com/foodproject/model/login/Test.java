package com.foodproject.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Test {
    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("method")
    private String method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Test(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public Test() {
    }
}
