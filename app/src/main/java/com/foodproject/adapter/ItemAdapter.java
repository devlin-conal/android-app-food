package com.foodproject.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.foodproject.R;
import com.foodproject.Utils.Headers;
import com.foodproject.Utils.SharedPrefManager;
import com.foodproject.api.response.MenuResponse;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder>{

    private List<MenuResponse> items = new ArrayList<>();
    private Context context;
    private final onClickListener mListener;
    private SharedPrefManager sharedPrefManager;

    public ItemAdapter(Context context){
        this.context = context;
        sharedPrefManager = new SharedPrefManager(context);

        try {
            this.mListener = ((onClickListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnPlaceClickListener.");
        }
    }

    public List<MenuResponse> getItems() {
        return items;
    }

    public void setItems(List<MenuResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final MenuResponse item =  items.get(position);

        holder.itemName.setText(item.getId().toString());
        holder.itemPrice.setText(item.getPrice().toString());
        Glide.with(context)
                .load(Headers.getUrlWithHeaders(item.getThumbnailPic(), sharedPrefManager.getSPToken()))
                .into(holder.mImage);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClickListener(item);
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView itemName, itemPrice;
        public View mView;
        public ImageView mImage;


        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            mImage = itemView.findViewById(R.id.item_img);
        }

        @Override
        public void onClick(View v) {}
    }

    public interface onClickListener {
        void onClickListener(MenuResponse item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
