package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleObserver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.ifresh.customer.R;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.OfferImage;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfferImageDetail extends AppCompatActivity {

    private String parent_id, youtube_code, imgpath, image_url;
    private ImageView img;
    private Context mContext = OfferImageDetail.this;
    private ArrayList<OfferImage> offerImgArrayList;
    YouTubePlayerView youTubePlayerView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_image_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.refer_earn));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        img = findViewById(R.id.img);
        getLifecycle().addObserver((LifecycleObserver) youTubePlayerView);

        youtube_code= getIntent().getStringExtra("youtube_code");
        image_url= getIntent().getStringExtra("image_url");


        Picasso.with(mContext)
                .load(image_url)
                .placeholder(R.drawable.placeholder)// optional
                .error(R.drawable.placeholder)
                .into(img);


        call_player();


        /*img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImageFullView.class);
                intent.putExtra("image",image_url);
                startActivity(intent);
             }
        });*/


    }

    public void GetOfferImage() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_OFFER_IMAGE, Constant.GetVal);
        params.put(Constant.PARENT_ID,parent_id);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        ArrayList<String> offerList = new ArrayList<>();
                        JSONObject objectbject = new JSONObject(response);
                        offerImgArrayList = new ArrayList<>();
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);

                            JSONObject object = jsonArray.getJSONObject(0);
                            imgpath = object.getString("image");
                            youtube_code = object.getString("youtube");

                            Picasso.with(mContext)
                                    .load(imgpath)
                                    .placeholder(R.drawable.placeholder)// optional
                                    .error(R.drawable.placeholder)
                                    .into(img);

                            call_player();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, OfferImageDetail.this, Constant.OFFER_URL, params, false);
    }

    private void call_player() {
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                String videoId = youtube_code;
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
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