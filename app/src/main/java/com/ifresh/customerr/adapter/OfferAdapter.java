package com.ifresh.customerr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.ifresh.customerr.R;
import com.ifresh.customerr.activity.OfferImageDetail;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.model.OfferImage;

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
                    int img_scroll = offerImage_obj.getIs_imgscroll();
                    if(img_scroll == 1)
                    {
                        // go to offer OfferImageDetail
                        Intent intent = new Intent(ctx, OfferImageDetail.class);
                        intent.putExtra("parent_id",offerImage_obj.getId());
                        intent.putExtra("youtube_code",offerImage_obj.getYoutube_str());
                        Log.d("imgpath->", offerImage_obj.getImage());
                        intent.putExtra("imgpath",offerImage_obj.getImage());
                        ctx.startActivity(intent);
                    }
                    else if(img_scroll == 0)
                    {
                        //no to action
                    }
                }
            });

        }



    }
}
