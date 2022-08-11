package com.foodproject.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.foodproject.R;
import com.foodproject.Utils.Headers;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.response.MenuResponse;

import java.util.ArrayList;
import java.util.List;

public class MenusFeatureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MenuResponse> menus = new ArrayList<>();
    private Context context;
    private final OnPlaceClickListener mListener;

    private SharedPrefManager sharedPrefManager;
    public static final int LOADING = 0;
    public static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public MenusFeatureAdapter(Context context){
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);

        try {
            this.mListener = ((OnPlaceClickListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnPlaceClickListener.");
        }
    }

    public List<MenuResponse> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuResponse> menus) {
        this.menus = menus;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                View view = LayoutInflater.from(context).inflate(R.layout.place_product_card, viewGroup, false);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder itemHolder, int position) {
        final MenuResponse menu =  menus.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ItemHolder holder = (ItemHolder) itemHolder;
                holder.mProductName.setText(menu.getName());
                holder.mProductDescription.setText(menu.getDescription());
                Glide.with(context)
                        .load(Headers.getUrlWithHeaders(menu.getThumbnailPic(), sharedPrefManager.getSPToken()))
                        .into(holder.imgMenu);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onTrendingClickListener(menu);
                    }
                });

                holder.mNoFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.OnPlaceFavoriteClick(menu);
                            holder.mNoFavorite.setVisibility(View.GONE);
                            holder.mFavorite.setVisibility(View.VISIBLE);
                        }
                    }
                });

                holder.mFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.OnPlaceNoFavoriteClick(menu);
                            holder.mNoFavorite.setVisibility(View.VISIBLE);
                            holder.mFavorite.setVisibility(View.GONE);
                        }
                    }
                });
                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) itemHolder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mProductName, mProductDescription;
        public ImageView mNoFavorite, mFavorite, imgMenu;
        public View mView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mProductName = itemView.findViewById(R.id.product_name);
            mProductDescription = itemView.findViewById(R.id.product_description);
            mFavorite = itemView.findViewById(R.id.favorite);
            mNoFavorite = itemView.findViewById(R.id.no_favorite);
            imgMenu = itemView.findViewById(R.id.product_menu_img);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Clicked Item", Toast.LENGTH_SHORT).show();
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }

    public interface OnPlaceClickListener {
        void OnPlaceNoFavoriteClick(MenuResponse menus);
        void OnPlaceFavoriteClick(MenuResponse menus);
        void onTrendingClickListener(MenuResponse menus);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == menus.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new MenuResponse());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = menus.size() - 1;
        MenuResponse result = getItem(position);

        if (result != null) {
            menus.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(MenuResponse movie) {
        menus.add(movie);
        notifyItemInserted(menus.size() - 1);
    }

    public void addAll(List<MenuResponse> moveResults) {
        for (MenuResponse result : moveResults) {
            add(result);
        }
    }

    public MenuResponse getItem(int position) {
        return menus.get(position);
    }
}
