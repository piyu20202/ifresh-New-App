package com.ifresh.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ifresh.customer.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class ImageFullView extends AppCompatActivity {
    private ZoomageView img_fullview;
    private Context mContext= ImageFullView.this;
    Toolbar toolbar;
    String imgpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        getSupportActionBar().setTitle(getResources().getString(R.string.image_full));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img_fullview = (ZoomageView)findViewById(R.id.full_imgview);
        imgpath= getIntent().getStringExtra("image");


        Picasso.with(mContext)
                .load(imgpath)
                .placeholder(R.drawable.progress_animationn)// optional
                .error(R.drawable.placeholder)
                .fit()
                .centerInside()
                .into(img_fullview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
