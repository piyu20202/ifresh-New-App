package com.ifresh.customerr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customerr.R;
import com.ifresh.customerr.activity.ProductListActivity_2;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.model.ModelSCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SCategoryAdapter extends RecyclerView.Adapter<SCategoryAdapter.MyViewHolder> {
    Context ctx;
    String url;
    int row_index;
    private static ArrayList<ModelSCategory> arrayList_horizontal;

    public SCategoryAdapter(ArrayList<ModelSCategory> arrayList_horizontal, Context ctx)
    {
        this.arrayList_horizontal=arrayList_horizontal;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_single_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ModelSCategory horizontal_subCategory = arrayList_horizontal.get(position);
        holder.txt_subcat.setText(horizontal_subCategory.getTitle());
        if(horizontal_subCategory.getCatagory_img().contentEquals(""))
        {
            url = "no image" ;
        }
        else{
            url = horizontal_subCategory.getCatagory_img();
        }

        holder.img_subcat.setDefaultImageResId(R.drawable.placeholder);
        holder.img_subcat.setErrorImageResId(R.drawable.placeholder);
        holder.img_subcat.setImageUrl(url, Constant.imageLoader);

        if(position == 0)
        {
            ViewGroup.LayoutParams layoutParams = holder.img_subcat.getLayoutParams();
            layoutParams.width = 80;
            layoutParams.height = 80;
            holder.img_subcat.setLayoutParams(layoutParams);
        }



        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                String cat_id = horizontal_subCategory.getId();
                ((ProductListActivity_2)ctx).callApiProductlist(cat_id,false);
                notifyDataSetChanged();

                //((ProductListActivity_2)ctx).callApiProductlist(cat_id,false,0);
            }
        });

       if(row_index == position){
           holder.relative_background_broder.setBackgroundResource(R.drawable.circle_white);
       }
       else{
           holder.relative_background_broder.setBackgroundResource(R.drawable.circle_white_2);
       }

    }

    @Override
    public int getItemCount() {
        return arrayList_horizontal.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt_subcat;
        NetworkImageView img_subcat;
        LinearLayout linearLayout;
        RelativeLayout relative_background_broder;
        public MyViewHolder(View itemView) {
            super(itemView);
            txt_subcat = (TextView) itemView.findViewById(R.id.txt_subcat);
            img_subcat = (NetworkImageView)itemView.findViewById(R.id.img_subcat);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linear);
            relative_background_broder = (RelativeLayout)itemView.findViewById(R.id.relative_background_broder);
        }

    }
}
