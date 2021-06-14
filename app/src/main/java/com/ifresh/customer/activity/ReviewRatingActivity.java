package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ifresh.customer.R;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.AppController;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReviewRatingActivity extends AppCompatActivity {

    RatingBar ratingProduct,ratingBoy;
    EditText editReview;
    Button submit;
    String orderId,home;
    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_rating);

        home = getIntent().getStringExtra("home");
        orderId = getIntent().getStringExtra("order_id");

        if (home.equalsIgnoreCase("home"))
        {
            orderId = getIntent().getStringExtra("home_order_id");
        }
        else
        {
            orderId = getIntent().getStringExtra("order_id");
        }

        ratingBoy = findViewById(R.id.ratingBoy);
        ratingProduct = findViewById(R.id.ratingProduct);
        editReview = findViewById(R.id.edtReview);
        submit = findViewById(R.id.submitReview);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editReview.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(ReviewRatingActivity.this, "Please comment first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendReview();
                }
            }
        });
    }

    public void SendReview()
    {
        String review = editReview.getText().toString().trim();

        if (AppController.isConnected(ReviewRatingActivity.this)) {
            //Map<String, String> params = new HashMap<String, String>();
            try {
                Map<String, String> obj_sendParam = new HashMap<String, String>();
                obj_sendParam.put(Constant.REFER_ORDER_ID, orderId);
                obj_sendParam.put(Constant.REFER_DELIVERY_RATE, String.valueOf(ratingBoy.getRating()));
                obj_sendParam.put(Constant.REFER_PRODUCT_RATE, String.valueOf(ratingProduct.getRating()));
                obj_sendParam.put(Constant.REFER_COMMENT,review);
                obj_sendParam.put(Constant.REFER_COMMENT_WHY,"");
                /*object = new JSONObject();

                object.put(Constant.REFER_ORDER_ID, orderId);
                object.put(Constant.REFER_DELIVERY_RATE,ratingBoy.getNumStars());
                object.put(Constant.REFER_PRODUCT_RATE,ratingProduct.getNumStars());
                object.put(Constant.REFER_COMMENT,review);
                object.put(Constant.REFER_COMMENT_WHY,"");

                String order_param = object.toString();
                obj_sendParam.put("order_param", order_param);*/
                System.out.println("=====param" + obj_sendParam.toString());

                ApiConfig.RequestToVolley_POST(new VolleyCallback()
                {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        if (result) {
                            try {
                                System.out.println("====place order res " + response);
                                JSONObject object = new JSONObject(response);
                                if (object.getInt("success")==200)
                                {
                                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ReviewRatingActivity.this, MainActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //  System.out.println("========order=======" + response);
                        Log.d("url", Constant.BASEPATH + Constant.SEND_REVIEW);
                    }
                }, ReviewRatingActivity.this, Constant.BASEPATH + Constant.SEND_REVIEW, obj_sendParam, true);

            } catch (Exception e) {
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }

            //   System.out.println("==========params " + params);
        }
    }
}