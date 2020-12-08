package com.ifresh.customerr.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ifresh.customerr.R;
import com.ifresh.customerr.activity.CartActivity_2;
import com.ifresh.customerr.activity.CheckoutActivity_2;
import com.ifresh.customerr.activity.MainActivity;
import com.ifresh.customerr.activity.ProductListActivity_2;
import com.ifresh.customerr.activity.SetDefaultAddress_2;
import com.ifresh.customerr.activity.UploadMedicine;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.kotlin.FillAddress;
import com.ifresh.customerr.kotlin.SignInActivity_K;
import com.ifresh.customerr.model.Category;

import org.json.JSONException;
import org.json.JSONObject;


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
        Log.d("url_image", model.getImage());

        holder.imgcategory.setImageUrl(model.getImage(), Constant.imageLoader);





    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView txttitle;
        NetworkImageView imgcategory;
        LinearLayout lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            imgcategory = itemView.findViewById(R.id.imgcategory);
            txttitle = itemView.findViewById(R.id.txttitle);

            lytMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Category model = categorylist.get(position);
                    if(model.getId().equalsIgnoreCase("0"))
                    {
                        // no click event
                    }
                    else{
                        /*if(model.getAllow_upload())
                        {

                        }
                        else if(!model.getAllow_upload()){
                            Intent intent  = new Intent(activity, ProductListActivity_2.class);
                            intent.putExtra("id", model.getId());
                            session.setData(Constant.CAT_ID, model.getId());
                            activity.startActivity(intent);
                        }*/
                        Intent intent  = new Intent(activity, ProductListActivity_2.class);
                        intent.putExtra("id", model.getId());
                        session.setData(Constant.CAT_ID, model.getId());
                        activity.startActivity(intent);

                    }


                }
            });

        }

    }






}
