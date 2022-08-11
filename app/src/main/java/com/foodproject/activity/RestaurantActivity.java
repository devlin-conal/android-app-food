package com.foodproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.foodproject.R;
import com.foodproject.Utils.AndroidUtil;
import com.foodproject.Utils.Headers;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.adapter.ItemAdapter;
import com.foodproject.adapter.MenusFeatureAdapter;
import com.foodproject.api.response.MenuResponse;
import com.foodproject.api.response.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;

import static com.foodproject.activity.RestaurantListActivity.ARG_MENU;
import static com.foodproject.activity.RestaurantListActivity.ARG_RESTAURANT;

public class RestaurantActivity extends AppCompatActivity implements MenusFeatureAdapter.OnPlaceClickListener,
        ItemAdapter.onClickListener {

    private Context mContext = RestaurantActivity.this;
    private static final String TAG = "PlaceActivity";

    private RelativeLayout mBack;

    private RecyclerView mProductRecycler, recyclerView1, recyclerView2, recyclerView3;
    private ItemAdapter mPlaceAdapter;
    private LinearLayout menu_item1, menu_item2, menu_item3;
    private ImageView mArrow1, mArrow2, mArrow3;
    private TextView txtName, txtLocation, txtReview, txtDelivery, txtSchedule, txtContact;
    private ImageView imgRestaurant;
    private AppCompatRatingBar ratingBar;
    private Context context;

    private RestaurantResponse restaurant;
    private List<MenuResponse> listMenus = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        context = this;
        sharedPrefManager = new SharedPrefManager(context);

        initComponents();
        getArgs();
        setupWidgets();
    }

    private void initComponents() {
        mBack = findViewById(R.id.back);
        mProductRecycler = findViewById(R.id.feature_recycler);
        recyclerView1 = findViewById(R.id.recycler);
        recyclerView2 = findViewById(R.id.recycler2);
        recyclerView3 = findViewById(R.id.recycler3);

        menu_item1 = findViewById(R.id.menu_item1);
        menu_item2 = findViewById(R.id.menu_item2);
        menu_item3 = findViewById(R.id.menu_item3);

        mArrow1 = findViewById(R.id.arrow1);
        mArrow2 = findViewById(R.id.arrow2);
        mArrow3 = findViewById(R.id.arrow3);

        txtName = findViewById(R.id.restaurant_name);
        txtLocation = findViewById(R.id.restaurant_location);
        txtReview = findViewById(R.id.total_review);
        txtDelivery = findViewById(R.id.txt_delivery);
        txtSchedule = findViewById(R.id.txt_schedule);
        txtContact = findViewById(R.id.txt_contact);
        imgRestaurant = findViewById(R.id.restaurant_image);
        ratingBar = findViewById(R.id.place_rating);


        recyclerView1.setVisibility(View.VISIBLE);
        mArrow1.setRotation(90);

        recyclerView2.setVisibility(View.GONE);
        recyclerView3.setVisibility(View.GONE);
    }

    private void getArgs() {
        Intent i = getIntent();
        restaurant = (RestaurantResponse) i.getSerializableExtra(ARG_RESTAURANT);
        listMenus.addAll(restaurant.getMenus());
    }

    private void setupWidgets() {
        Window window = getWindow();
        AndroidUtil.statusBarColorTransparent(window);

        //setup product recycler view
        LinearLayoutManager llmProduct = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mProductRecycler.setLayoutManager(llmProduct);
        MenusFeatureAdapter mProductAdapter1 = new MenusFeatureAdapter(this);
        mProductRecycler.setAdapter(mProductAdapter1);

        LinearLayoutManager lnl1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(lnl1);
        mPlaceAdapter = new ItemAdapter(this);
        mPlaceAdapter.setItems(listMenus);
        recyclerView1.setAdapter(mPlaceAdapter);

        LinearLayoutManager lnl2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(lnl2);
        mPlaceAdapter = new ItemAdapter(this);
        recyclerView2.setAdapter(mPlaceAdapter);

        LinearLayoutManager lnl3 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(lnl3);
        mPlaceAdapter = new ItemAdapter(this);
        recyclerView3.setAdapter(mPlaceAdapter);

        if (restaurant != null) {
            txtName.setText(restaurant.getName());
            txtLocation.setText(restaurant.getLocation());
            txtReview.setText("");
            txtDelivery.setText("");
            txtSchedule.setText("");
            txtContact.setText(restaurant.getPhone());
            ratingBar.setNumStars(restaurant.getRating().intValue());
            Glide.with(context)
                    .load(Headers.getUrlWithHeaders(restaurant.getThumbnailPic(), sharedPrefManager.getSPToken()))
                    .into(imgRestaurant);

            mProductAdapter1.setMenus(listMenus);
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        menu_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView1.getVisibility() == View.VISIBLE) {
                    recyclerView1.setVisibility(View.GONE);
                    mArrow1.setRotation(-90);
                } else {
                    recyclerView1.setVisibility(View.VISIBLE);
                    mArrow1.setRotation(90);
                }
            }
        });

        menu_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView2.getVisibility() == View.VISIBLE) {
                    recyclerView2.setVisibility(View.GONE);
                    mArrow2.setRotation(-90);
                } else {
                    recyclerView2.setVisibility(View.VISIBLE);
                    mArrow2.setRotation(90);
                }
            }
        });

        menu_item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView3.getVisibility() == View.VISIBLE) {
                    recyclerView3.setVisibility(View.GONE);
                    mArrow3.setRotation(-90);
                } else {
                    recyclerView3.setVisibility(View.VISIBLE);
                    mArrow3.setRotation(90);
                }
            }
        });
    }

    @Override
    public void OnPlaceNoFavoriteClick(MenuResponse menus) {

    }

    @Override
    public void OnPlaceFavoriteClick(MenuResponse menus) {

    }

    @Override
    public void onTrendingClickListener(MenuResponse menus) {
        Intent i = new Intent(RestaurantActivity.this, ItemDetailsActivity.class);
        i.putExtra(ARG_MENU, menus);
        startActivity(i);
    }

    @Override
    public void onClickListener(MenuResponse item) {
        Intent i = new Intent(RestaurantActivity.this, ItemDetailsActivity.class);
        i.putExtra(ARG_MENU, item);
        startActivity(i);
    }
}
