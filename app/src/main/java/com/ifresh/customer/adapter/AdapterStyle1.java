package com.ifresh.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.ifresh.customer.R;
import com.ifresh.customer.activity.ProductDetailActivity_2;
import com.ifresh.customer.helper.AppController;
import com.ifresh.customer.model.ModelProduct;
import com.ifresh.customer.model.ModelProductVariation;

/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle1 extends RecyclerView.Adapter<AdapterStyle1.VideoHolder> {

    public ArrayList<ModelProduct> productList;

    public Activity activity;
    public int itemResource;
    ImageLoader netImageLoader = AppController.getInstance().getImageLoader();

    public AdapterStyle1(Activity activity, ArrayList<ModelProduct> productList, int itemResource) {
        this.activity = activity;
        this.productList = productList;
        this.itemResource = itemResource;

    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public NetworkImageView thumbnail;
        public TextView v_title, v_date, description;
        public RelativeLayout relativeLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            v_title = (TextView) itemView.findViewById(R.id.title);
            v_date = (TextView) itemView.findViewById(R.id.date);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.play_layout);

        }


    }


    @Override
    public int getItemCount() {
        int product;
        if (productList.size() > 4) {
            product = 4;
        } else {
            product = productList.size();
        }
        return product;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final ModelProduct product = productList.get(position);
        final ArrayList<ModelProductVariation> priceVariations = product.getPriceVariations();
        product.setGlobalStock(Double.parseDouble(priceVariations.get(0).getStock()));
        holder.thumbnail.setImageUrl(product.getProduct_img(), netImageLoader);
        holder.v_title.setText(product.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetailActivity_2.class).
                        putExtra("vpos", 0).
                        putExtra("model", product)
                );


            }
        });
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new VideoHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
