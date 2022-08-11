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
import com.bumptech.glide.Glide;
import com.foodproject.R;
import com.foodproject.Utils.Headers;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.response.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CategoryResponse> categories;
    private Context context;
    private final OnCategoryClickListener mListener;
    private SharedPrefManager sharedPrefManager;

    public static final int LOADING = 0;
    public static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public CategoriesAdapter(Context context) {
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);
        categories = new ArrayList<>();

        try {
            this.mListener = ((OnCategoryClickListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnPlaceClickListener.");
        }
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponse> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.category_item, viewGroup, false);
                viewHolder = new ItemHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder itemHolder, int position) {
        final CategoryResponse category = categories.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ItemHolder holder = (ItemHolder) itemHolder;
                holder.mCategoryName.setText(category.getId().toString());
                Glide.with(context)
                        .load(Headers.getUrlWithHeaders(category.getThumbnailPic(), sharedPrefManager.getSPToken()))
                        .into(holder.mCategoryImage);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onCategoryClickListener(category);
                    }
                });
                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) itemHolder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
