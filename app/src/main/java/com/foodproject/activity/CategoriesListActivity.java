package com.foodproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.foodproject.R;
import com.foodproject.Utils.GsonUtil;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.adapter.CategoriesAdapter;
import com.foodproject.adapter.PaginationScrollListener;
import com.foodproject.api.ApiClient;
import com.foodproject.api.ApiInterface;
import com.foodproject.api.response.BasePageResponse;
import com.foodproject.api.response.BaseResponse;
import com.foodproject.api.response.CategoryResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.foodproject.activity.RestaurantListActivity.ARG_CATEGORY;

public class CategoriesListActivity extends AppCompatActivity implements CategoriesAdapter.OnCategoryClickListener{

    private RelativeLayout mBack;
    private RecyclerView mRecyclerView;
    private List<CategoryResponse> mListCategory;
    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private CategoriesAdapter categoriesAdapter;
    private ProgressBar progressBar;
    private Context context;

    private int page = 0, limit = 2;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);
        context = this;

        initialize();
        setupWidgets();
    }

    private void initialize() {
        mBack = findViewById(R.id.back);
        mRecyclerView = findViewById(R.id.recycler_view);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);
        mListCategory = new ArrayList<>();
        progressBar = findViewById(R.id.progressbar);
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

        GridLayoutManager lnlGrid = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(lnlGrid);
        categoriesAdapter = new CategoriesAdapter(this);
        mRecyclerView.setAdapter(categoriesAdapter);

        lnlGrid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(categoriesAdapter.getItemViewType(position)){
                    case CategoriesAdapter.ITEM:
                        return 1;
                    case CategoriesAdapter.LOADING:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(lnlGrid) {
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
                categoriesAdapter.removeLoadingFooter();
                isLoading = false;

                BasePageResponse<CategoryResponse> results = loadCategories(page, limit);
                categoriesAdapter.addAll(results.getContent());

                if (page != results.getTotalPages()) categoriesAdapter.addLoadingFooter();
                else isLastPage = true;
            }
        }, 200);
    }


    private void loadFirstPage() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                BasePageResponse<CategoryResponse> results = loadCategories(page, limit);
                progressBar.setVisibility(View.GONE);
                categoriesAdapter.addAll(results.getContent());

                if (page <= results.getTotalPages()) categoriesAdapter.addLoadingFooter();
                else isLastPage = true;
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

    @Override
    public void onCategoryClickListener(CategoryResponse category) {
        Intent i = new Intent(CategoriesListActivity.this, RestaurantListActivity.class);
        i.putExtra(ARG_CATEGORY, (Serializable) new PutCategory(category, true));
        startActivity(i);
    }

    public static class PutCategory implements Serializable {
        private CategoryResponse category;
        private boolean checkFrom;

        public PutCategory() {
        }

        public PutCategory(CategoryResponse category, boolean checkFrom) {
            this.category = category;
            this.checkFrom = checkFrom;
        }

        public CategoryResponse getCategory() {
            return category;
        }

        public void setCategory(CategoryResponse category) {
            this.category = category;
        }

        public boolean isCheckFrom() {
            return checkFrom;
        }

        public void setCheckFrom(boolean checkFrom) {
            this.checkFrom = checkFrom;
        }
    }
}
