package com.foodproject.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.foodproject.R;
import com.foodproject.Utils.Headers;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.ApiClient;
import com.foodproject.api.ApiInterface;
import com.foodproject.api.response.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class HomeCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<CategoryResponse> categories = new ArrayList<>();
    private Context context;
    private final OnCategoryClickListener mListener;
    private ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;

    public static final int LOADING = 0;
    public static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public HomeCategoriesAdapter(Context context){
        this.context = context;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(context);

        try {
            this.mListener = ((OnCategoryClickListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnPlaceClickListener.");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                View view = LayoutInflater.from(context).inflate(R.layout.home_category_item, viewGroup, false);
                viewHolder = new ItemHolder(view);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewLoading.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                );
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CategoryResponse category =  categories.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ItemHolder itemHolder = (ItemHolder) holder;
                itemHolder.mCategoryName.setText(category.getName());

                Glide.with(context)
                        .load(Headers.getUrlWithHeaders(category.getThumbnailPic(), sharedPrefManager.getSPToken()))
                        .into(itemHolder.mCategoryImage);

                itemHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onCategoryClickListener(category);
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
        public TextView mCategoryName;
        public ImageView mCategoryImage;
        public View mView;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mCategoryName = itemView.findViewById(R.id.category_name);
            mCategoryImage = itemView.findViewById(R.id.category_photo);
        }

        @Override
        public void onClick(View v) {}
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
//        private LinearLayout linearLayout;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
//            linearLayout = (LinearLayout) itemView.findViewById(R.id.layout_progress);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClickListener(CategoryResponse category);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == categories.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new CategoryResponse());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = categories.size() - 1;
        CategoryResponse result = getItem(position);

        if (result != null) {
            categories.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(CategoryResponse movie) {
        categories.add(movie);
        notifyItemInserted(categories.size() - 1);
    }

    public void addAll(List<CategoryResponse> moveResults) {
        for (CategoryResponse result : moveResults) {
            add(result);
        }
    }

    public CategoryResponse getItem(int position) {
        return categories.get(position);
    }
}
