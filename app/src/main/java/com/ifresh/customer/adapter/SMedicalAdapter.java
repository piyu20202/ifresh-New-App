package com.ifresh.customer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customer.R;
import com.ifresh.customer.activity.MedicalListActivity_2;
import com.ifresh.customer.activity.ProductListActivity_2;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.model.ModelSCategory;

import java.util.ArrayList;

public class SMedicalAdapter extends RecyclerView.Adapter<SMedicalAdapter.MyViewHolder> {
    Context ctx;
    String url;
    int row_index;
    public static String  cat_id_adapter="";
    private static ArrayList<ModelSCategory> arrayList_horizontal;

    public SMedicalAdapter(ArrayList<ModelSCategory> arrayList_horizontal, Context ctx)
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


        String str_1 =  horizontal_subCategory.getTitle();

        if(str_1.contains(" "))
        {
            String[] str_arr_2 = str_1.split("\\s+");
            String str_val;

            if(str_arr_2[0].length() > 5) {
                str_val = str_arr_2[0] + "\n"  + str_arr_2[1];
            }
            else{
                str_val = str_1;
            }

            Log.d("value", str_val);

            String cap = str_val.substring(0, 1).toUpperCase() + str_val.substring(1);
            holder.txt_subcat.setText(cap);
        }
        else{
            String cap = str_1.substring(0, 1).toUpperCase() + str_1.substring(1);
            holder.txt_subcat.setText(cap);
        }

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
            layoutParams.width = 60;
            layoutParams.height = 60;
            holder.img_subcat.setLayoutParams(layoutParams);

        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                String cat_id = horizontal_subCategory.getId();
                cat_id_adapter = cat_id;
                ((MedicalListActivity_2)ctx).callApiMedicallist(cat_id,false);
                notifyDataSetChanged();


            }
        });


        if(row_index == position){
           holder.relative_background_broder.setBackgroundResource(R.drawable.bg_round_ractview_green);
       }
       else{
           holder.relative_background_broder.setBackgroundResource(R.drawable.bg_round_ractview_gray);
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
