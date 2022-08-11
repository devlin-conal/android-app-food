package com.foodproject.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.foodproject.R;
import com.foodproject.Utils.Headers;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.response.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<RestaurantResponse> mRestaurants = new ArrayList<>();
    private final OnRestaurantClickListener mListener;

    private Context context;
    private SharedPrefManager sharedPrefManager;
    public static final int LOADING = 0;
    public static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public RestaurantAdapter(Context context){
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);

        try {
            this.mListener = ((OnRestaurantClickListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnRestaurantClickListener.");
        }
    }

    public void setFavorite(int restaurantId) {
        if(mRestaurants.size() > 0) {
            for (int i = 0; i < mRestaurants.size(); i++) {
                if(mRestaurants.get(i).getId()== restaurantId) {
                    if (mRestaurants.get(i).getRating() != 0) {
                        mRestaurants.get(i).setRating(5D);
                        break;
                    } else {
                        mRestaurants.get(i).setRating(0D);
                        break;
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                View view = LayoutInflater.from(context).inflate(R.layout.restaurant_card, viewGroup, false);
                viewHolder = new ItemHolder(view);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final RestaurantResponse restaurant =  mRestaurants.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ItemHolder itemHolder = (ItemHolder) holder;
                itemHolder.mItem = restaurant;

                itemHolder.restaurantName.setText(restaurant.getId().toString());
                itemHolder.restaurantLocation.setText(restaurant.getLocation());
                itemHolder.restaurantRating.setText(restaurant.getRating().toString());
                itemHolder.restaurantDelivery.setText(restaurant.getSamePrice().toString());

                Glide.with(context)
                        .load(Headers.getUrlWithHeaders(restaurant.getThumbnailPic(), sharedPrefManager.getSPToken()))
                        .into(itemHolder.restaurantImg);

                if (itemHolder.mItem.getRating() != 0) {
                    itemHolder.icFavorite.setImageResource(R.drawable.star);
                } else {
                    itemHolder.icFavorite.setImageResource(R.drawable.star2);
                }

                itemHolder.lnlFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onRestaurantFavoriteClick(restaurant);
                    }
                });

                itemHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onRestaurantClickListener(restaurant);
                    }
                });

                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView restaurantName, restaurantLocation, restaurantRating, restaurantDelivery;
        public RelativeLayout lnlFavorite;
        public ImageView icFavorite;
        public final View mView;
        public RestaurantResponse mItem;
        public ImageView restaurantImg;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantLocation = itemView.findViewById(R.id.restaurant_location);
            restaurantRating = itemView.findViewById(R.id.restaurant_rating);
            restaurantDelivery = itemView.findViewById(R.id.restaurant_delivery);
            lnlFavorite = itemView.findViewById(R.id.lnl_favorite);
            icFavorite = itemView.findViewById(R.id.ic_favorite);
            restaurantImg = itemView.findViewById(R.id.restaurant_image);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }

    public interface OnRestaurantClickListener {
        void onRestaurantClickListener(RestaurantResponse restaurant);
        void onRestaurantFavoriteClick(RestaurantResponse restaurant);
    }

    @Override
    public int getItemCount() {
        return mRestaurants == null ? 0 : mRestaurants.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mRestaurants.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RestaurantResponse());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mRestaurants.size() - 1;
        RestaurantResponse result = getItem(position);

        if (result != null) {
            mRestaurants.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(RestaurantResponse movie) {
        mRestaurants.add(movie);
        notifyItemInserted(mRestaurants.size() - 1);
    }

    public void addAll(List<RestaurantResponse> moveResults) {
        for (RestaurantResponse result : moveResults) {
            add(result);
        }
    }

    public RestaurantResponse getItem(int position) {
        return mRestaurants.get(position);
    }
}
