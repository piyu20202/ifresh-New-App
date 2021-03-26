package com.ifresh.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.ifresh.customer.R;
import com.ifresh.customer.activity.MedicalListActivity_2;
import com.ifresh.customer.activity.ProductListActivity_2;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.model.Category;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    public ArrayList<Category> categorylist;
    int layout;
    String from = "";
    Activity activity;
    Session session;



    public CategoryAdapter(Activity activity, ArrayList<Category> categorylist, int layout, String from, Session session) {
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
        this.session = session;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category model = categorylist.get(position);
        holder.txttitle.setText(model.getName());
        holder.imgcategory.setDefaultImageResId(R.drawable.placeholder);
        holder.imgcategory.setErrorImageResId(R.drawable.placeholder);
        //Log.d("url_image", model.getImage());

        holder.imgcategory.setImageUrl(model.getImage(), Constant.imageLoader);
        //Log.d("value", ""+  model.getIs_comingsoon());


        if(model.getIs_comingsoon())
        {
            // category is coming soon
            holder.img_comingsoon.setVisibility(View.VISIBLE);

        }
        else{
            // category is not coming soon
            holder.img_comingsoon.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txttitle;
        NetworkImageView imgcategory;
        ImageView img_comingsoon;
        LinearLayout lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            imgcategory = itemView.findViewById(R.id.imgcategory);
            txttitle = itemView.findViewById(R.id.txttitle);
            img_comingsoon = itemView.findViewById(R.id.img_comingsoon);




            lytMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Category model = categorylist.get(position);


                    if(model.getIs_comingsoon())
                    {
                        // category is coming soon
                        Toast.makeText(activity,"Category is Coming Soon", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //category is not coming soon
                        if(model.getId().equalsIgnoreCase("0"))
                        {
                            // no click event
                        }
                        else{
                            if(model.getAllow_upload())
                            {
                                Intent intent  = new Intent(activity, MedicalListActivity_2.class);
                                intent.putExtra("id", model.getId());
                                session.setData(Constant.CAT_ID, model.getId());
                                activity.startActivity(intent);
                            }
                            else if(!model.getAllow_upload()){
                                Intent intent  = new Intent(activity, ProductListActivity_2.class);
                                intent.putExtra("id", model.getId());
                                session.setData(Constant.CAT_ID, model.getId());
                                activity.startActivity(intent);
                            }
                        }

                    }


                }
            });

        }

    }






}
