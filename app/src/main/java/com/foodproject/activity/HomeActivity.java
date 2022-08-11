package com.foodproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.foodproject.R;
import com.foodproject.Utils.BottomNavigationViewHelper;
import com.foodproject.Utils.GsonUtil;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.adapter.HomeCategoriesAdapter;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import retrofit2.Call;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.foodproject.activity.RestaurantListActivity.ARG_CATEGORY;
import static com.foodproject.activity.RestaurantListActivity.ARG_RESTAURANT;

public class HomeActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener,
        HomeCategoriesAdapter.OnCategoryClickListener {

    private RecyclerView mCategoryRecycler, mRestaurantRecycler;
    private HomeCategoriesAdapter mCategoriesAdapter;
    private RestaurantAdapter mRestaurantAdapter;
    private TextView mRestaurantList, mCategoriesList;
    SharedPrefManager sharedPrefManager;

    private static final int ACTIVITY_NUM = 0;
    private Context mContext = HomeActivity.this;

    private ApiInterface apiInterface;
    private int categoryPage = 0, categoryLimit = 2;
    private int restaurantPage = 0, restaurantLimit = 2;
    private boolean isRestaurantLoading = false;
    private boolean isRestaurantLastPage = false;
    private boolean isCategoryLoading = false;
    private boolean isCategoryLastPage = false;

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefManager = new SharedPrefManager(this);

        initComponents();
        setupWidgets();
        setupBottomNavigationView();
    }

    private void initComponents() {
        mCategoryRecycler = findViewById(R.id.trending_recycler_view);
        mRestaurantRecycler = findViewById(R.id.place_recycler_view);
        mRestaurantList = findViewById(R.id.place_list);
        mCategoriesList = findViewById(R.id.categories_list);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    private void setupWidgets() {

        LinearLayoutManager llmTrending = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCategoryRecycler.setLayoutManager(llmTrending);
        mCategoriesAdapter = new HomeCategoriesAdapter(this);
        mCategoryRecycler.setAdapter(mCategoriesAdapter);

        LinearLayoutManager llmRestaurant = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRestaurantRecycler.setLayoutManager(llmRestaurant);
        mRestaurantAdapter = new RestaurantAdapter(this);
        mRestaurantRecycler.setAdapter(mRestaurantAdapter);

        mRestaurantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, RestaurantListActivity.class);
                i.putExtra(ARG_CATEGORY, new CategoriesListActivity.PutCategory(null, false));
                startActivity(i);
            }
        });

        mCategoriesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CategoriesListActivity.class));
            }
        });

        mCategoryRecycler.addOnScrollListener(new PaginationScrollListener(llmTrending) {
            @Override
            protected void loadMoreItems() {
                isCategoryLoading = true;
                categoryPage += 1;
                loadCategoryNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isCategoryLastPage;
            }

            @Override
            public boolean isLoading() {
                return isCategoryLoading;
            }
        });

        mRestaurantRecycler.addOnScrollListener(new PaginationScrollListener(llmRestaurant) {
            @Override
            protected void loadMoreItems() {
                isRestaurantLoading = true;
                restaurantPage += 1;
                loadRestaurantNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isRestaurantLastPage;
            }

            @Override
            public boolean isLoading() {
                return isRestaurantLoading;
            }
        });

        loadCategoryFirstPage();
        loadRestaurantFirstPage();
    }

    //    BottomNavigationView setup
    private void setupBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onCategoryClickListener(CategoryResponse category) {
        Intent i = new Intent(HomeActivity.this, RestaurantListActivity.class);
        i.putExtra(ARG_CATEGORY, new CategoriesListActivity.PutCategory(category, true));
        startActivity(i);
    }

    @Override
    public void onRestaurantClickListener(RestaurantResponse restaurant) {
        Intent i = new Intent(HomeActivity.this, RestaurantActivity.class);
        i.putExtra(ARG_RESTAURANT, restaurant);
        startActivity(i);
    }

    @Override
    public void onRestaurantFavoriteClick(RestaurantResponse place) {
        mRestaurantAdapter.setFavorite(place.getId().intValue());
        mRestaurantAdapter.notifyDataSetChanged();
    }

    private void loadCategoryNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCategoriesAdapter.removeLoadingFooter();
                isCategoryLoading = false;

                BasePageResponse<CategoryResponse> results = loadCategories(categoryPage, categoryLimit);
                mCategoriesAdapter.addAll(results.getContent());

                if (categoryPage != results.getTotalPages()) mCategoriesAdapter.addLoadingFooter();
                else isCategoryLastPage = true;
            }
        }, 200);
    }


    private void loadCategoryFirstPage() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                BasePageResponse<CategoryResponse> results = loadCategories(categoryPage, categoryLimit);
                mCategoriesAdapter.addAll(results.getContent());

                if (categoryPage <= results.getTotalPages()) mCategoriesAdapter.addLoadingFooter();
                else isCategoryLastPage = true;
            }
        });
    }

    private BasePageResponse<CategoryResponse> loadCategories(int pageFun, int limitFun) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(threadPool);
        ListenableFuture<BasePageResponse<CategoryResponse>> guavaFuture = (ListenableFuture<BasePageResponse<CategoryResponse>>) service.submit(()-> {
            try {
                Call<BaseResponse> response = apiInterface.getCategories(sharedPrefManager.getSPToken(), pageFun, limitFun);
                BasePageResponse<CategoryResponse> pageResponse = GsonUtil.getGson().fromJson(response.execute().body().getResult(), new TypeToken<BasePageResponse<CategoryResponse>>(){}.getType());
                return pageResponse;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });

        try {
            BasePageResponse<CategoryResponse> res = guavaFuture.get();
            return res;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadRestaurantNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRestaurantAdapter.removeLoadingFooter();
                isRestaurantLoading = false;

                BasePageResponse<RestaurantResponse> results = loadRestaurants(restaurantPage, restaurantLimit);
                mRestaurantAdapter.addAll(results.getContent());

                if (restaurantPage != results.getTotalPages()) mRestaurantAdapter.addLoadingFooter();
                else isRestaurantLastPage = true;
            }
        }, 200);
    }


    private void loadRestaurantFirstPage() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                BasePageResponse<RestaurantResponse> results = loadRestaurants(restaurantPage, restaurantLimit);
                mRestaurantAdapter.addAll(results.getContent());

                if (restaurantPage <= results.getTotalPages()) mRestaurantAdapter.addLoadingFooter();
                else isRestaurantLastPage = true;
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
}
