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
import com.foodproject.R;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.response.IngredientResponse;

import java.util.ArrayList;
import java.util.List;

public class ExtrasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = "ExtrasAdapter";

    private List<IngredientResponse> mExtras = new ArrayList<>();
    private Context context;
    private final OnExtraClickListener mListener;

    private SharedPrefManager sharedPrefManager;
    public static final int LOADING = 0;
    public static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    private String[] extrasNames = {"Tuna, Salmon, Wasabi, Unagi, Vegetables, Noodles"};

    public ExtrasAdapter(Context context) {
        this.context = context;

        try {
            this.mListener = ((OnExtraClickListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnPlaceClickListener.");
        }

//        String[] extrasNames = {"Queijo", "Bacon", "Cheddar", "Hamburger 200g",
//                "Molho de Mostarda com Mel", "Salada"};
//        String[] extrasValues = {"2", "4", "2", "9", "4", "3"};
//
//        for (int i = 0; i < 6; i++){
//            Extra extra = new Extra((i + 1), extrasNames[i], "R$" + extrasValues[i] + ",00");
//            mExtras.add(extra);
//        }
    }

    public List<IngredientResponse> getmExtras() {
        return mExtras;
    }

    public void setmExtras(List<IngredientResponse> mExtras) {
        this.mExtras = mExtras;
    }

    public void addExtra(int extraId) {
//        if(mExtras.size() > 0) {
//            for (int i = 0; i < mExtras.size(); i++) {
//                if(mExtras.get(i).getId() == extraId) {
//                    if (!mExtras.get(i).isAdded()) {
//                        mExtras.get(i).addExtra(true);
//                        break;
//                    } else {
//                        mExtras.get(i).addExtra(false);
//                        break;
//                    }
//                }
//            }
//        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                View view = LayoutInflater.from(context).inflate(R.layout.extra_item, viewGroup, false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder itemHolder, int position) {
        final IngredientResponse extra = mExtras.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ItemHolder holder = (ItemHolder) itemHolder;
                holder.mItem = extra;

                holder.mExtraName.setText(extra.getName() + extra.getId());
                holder.mExtraValue.setText(extra.getPrice().toString());

//                if (holder.mItem.isAdded()) {
//                    holder.icAdd.setImageResource(R.drawable.ic_check);
//                } else {
//                    holder.icAdd.setImageResource(R.drawable.ic_plus);
//                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onExtraClickListener(extra);
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
        public TextView mExtraName, mExtraValue;
        public ImageView icAdd;
        public final View mView;
        public IngredientResponse mItem;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mExtraName = itemView.findViewById(R.id.extra_name);
            mExtraValue = itemView.findViewById(R.id.extra_value);
            icAdd = itemView.findViewById(R.id.ic_add);
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

    @Override
    public int getItemCount() {
        return mExtras.size();
    }

    public interface OnExtraClickListener {
        void onExtraClickListener(IngredientResponse extra);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mExtras.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new IngredientResponse());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mExtras.size() - 1;
        IngredientResponse result = getItem(position);

        if (result != null) {
            mExtras.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(IngredientResponse movie) {
        mExtras.add(movie);
        notifyItemInserted(mExtras.size() - 1);
    }

    public void addAll(List<IngredientResponse> moveResults) {
        for (IngredientResponse result : moveResults) {
            add(result);
        }
    }

    public IngredientResponse getItem(int position) {
        return mExtras.get(position);
    }
}
