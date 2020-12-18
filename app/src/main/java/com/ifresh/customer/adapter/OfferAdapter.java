package com.ifresh.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.ifresh.customer.R;
import com.ifresh.customer.activity.OfferProductListActivity;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.model.OfferImage;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    public ArrayList<String> offerlist;
    public ArrayList<OfferImage> offerImageArrayList;
    public OfferImage offerImage_obj;

    int layout;
    String from = "";
    Activity activity;
    Context ctx;


    public OfferAdapter(ArrayList<String> offerlist, ArrayList<OfferImage> offerImages, int layout, Context ctx) {
        this.offerlist = offerlist;
        this.offerImageArrayList = offerImages;
        this.layout = layout;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.offerImage.setDefaultImageResId(R.drawable.placeholder);
        holder.offerImage.setErrorImageResId(R.drawable.placeholder);
        holder.offerImage.setImageUrl(offerlist.get(position), Constant.imageLoader);
    }

    @Override
    public int getItemCount() {
        return offerlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView offerImage;
        public ViewHolder(View itemView)
        {
            super(itemView);
            offerImage = itemView.findViewById(R.id.offerImage);

            offerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    offerImage_obj = offerImageArrayList.get(pos);
                    Intent intent = new Intent(ctx, OfferProductListActivity.class);
                    intent.putExtra("from", "regular");
                    intent.putExtra("name", "");
                    intent.putExtra("position", -1);
                    intent.putExtra("offer_id",offerImage_obj.getId());
                    ctx.startActivity(intent);
                }
            });
        }
    }
}
