package com.foodproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.foodproject.R;
import com.foodproject.Utils.GsonUtil;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.adapter.PaginationScrollListener;
import com.foodproject.adapter.RestaurantAdapter;
import com.foodproject.api.ApiClient;
import com.foodproject.api.ApiInterface;
import com.foodproject.api.response.BasePageResponse;
import com.foodproject.api.response.BaseResponse;
import com.foodproject.api.response.CategoryResponse;
import com.foodproject.api.response.RestaurantResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantListActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener{

    private RelativeLayout mBack;
    private RecyclerView mRecyclerView;
    private TextView mTitle;

    public static String ARG_CATEGORY = "ARG_CATEGORY";
    public static String ARG_RESTAURANT = "ARG_RESTAURANT";
    public static String ARG_MENU = "ARG_MENU";
    CategoriesListActivity.PutCategory categoryPut;
    private List<RestaurantResponse> restaurantsList = new ArrayList<>();

    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private RestaurantAdapter mRestaurantAdapter;
    private Context context = RestaurantListActivity.this;

    private int page = 0, limit = 2;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        initialize();
        getArgs();
        setupWidgets();
    }

    private void initialize() {
        mBack = findViewById(R.id.back);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTitle = findViewById(R.id.toolbar_title);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);
    }

    private void getArgs() {
        Intent i = getIntent();
        categoryPut = (CategoriesListActivity.PutCategory) i.getSerializableExtra(ARG_CATEGORY);
        CategoryResponse categoryRestaurant = categoryPut.getCategory();
        if(categoryRestaurant != null) {
            restaurantsList.addAll(categoryRestaurant.getRestaurants());
            mTitle.setText(categoryRestaurant.getId().toString());
        }else {
            mTitle.setText(getResources().getString(R.string.places_list_toolbar));
        }
    }

    private void setupWidgets() {
        //change status bar color to transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.headerColor));
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LinearLayoutManager llmRestaurant = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llmRestaurant);
        mRestaurantAdapter = new RestaurantAdapter(this);
        mRecyclerView.setAdapter(mRestaurantAdapter);

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(llmRestaurant) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                page += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRestaurantAdapter.removeLoadingFooter();
                isLoading = false;

                BasePageResponse<RestaurantResponse> results = loadRestaurants(page, limit);
                List<RestaurantResponse> mlist = new ArrayList<>();
                if (categoryPut.isCheckFrom()) {
                    for (int i = page * limit; i < restaurantsList.size(); i++) {
                        if (i < (page + 1) * limit) {
                            mlist.add(restaurantsList.get(i));
                        }
                    }
                    mRestaurantAdapter.addAll(mlist);
                    if((page + 1) * limit < restaurantsList.size()) mRestaurantAdapter.addLoadingFooter();
                    else isLastPage = true;
                }else {
                    mRestaurantAdapter.addAll(results.getContent());
                    if (page != results.getTotalPages()) mRestaurantAdapter.addLoadingFooter();
                    else isLastPage = true;
                }

            }
        }, 200);
    }


    private void loadFirstPage() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                BasePageResponse<RestaurantResponse> results = loadRestaurants(page, limit);
                List<RestaurantResponse> mlist = new ArrayList<>();
                if (categoryPut.isCheckFrom()) {
                    for (int i = page * limit; i < restaurantsList.size(); i++) {
                        if (i < (page + 1) * limit) {
                            mlist.add(restaurantsList.get(i));
                        }
                    }
                    mRestaurantAdapter.addAll(mlist);
                    if (!restaurantsList.isEmpty()) mRestaurantAdapter.addLoadingFooter();
                    else isLastPage = true;
                }else {
                    mRestaurantAdapter.addAll(results.getContent());
                    if (page <= results.getTotalPages()) mRestaurantAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private BasePageResponse<RestaurantResponse> loadRestaurants(int pageFun, int limitFun) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(threadPool);
        ListenableFuture<BasePageResponse<RestaurantResponse>> guavaFuture = (ListenableFuture<BasePageResponse<RestaurantResponse>>) service.submit(()-> {
            try {
                Call<BaseResponse> response = apiInterface.getRestaurants(sharedPrefManager.getSPToken(), pageFun, limitFun);
                BasePageResponse<RestaurantResponse> pageResponse = GsonUtil.getGson().fromJson(response.execute().body().getResult(), new TypeToken<BasePageResponse<RestaurantResponse>>(){}.getType());
                return pageResponse;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });

        try {
            BasePageResponse<RestaurantResponse> res = guavaFuture.get();
            return res;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRestaurantClickListener(RestaurantResponse place) {
        startActivity(new Intent(RestaurantListActivity.this, RestaurantActivity.class));
    }

    @Override
    public void onRestaurantFavoriteClick(RestaurantResponse place) {

    }
}
