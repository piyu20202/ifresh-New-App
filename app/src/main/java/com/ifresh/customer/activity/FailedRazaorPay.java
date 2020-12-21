package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ifresh.customer.R;

public class FailedRazaorPay extends AppCompatActivity {

    public Toolbar toolbar;
    public TextView txt_msg,txnid,razorpay_amt,status,msg,txtuser_msg;
    Context context =  FailedRazaorPay.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conform_deposit__information);
        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.payment_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar = findViewById(R.id.toolbar);
        txt_msg = findViewById(R.id.txt_msg);
        txnid = findViewById(R.id.txnid);
        razorpay_amt = findViewById(R.id.razorpay_amt);
        status = findViewById(R.id.status);
        txtuser_msg = findViewById(R.id.txtuser_msg);

        Intent intent = getIntent();
        txnid.setText(intent.getStringExtra("txnid"));
        razorpay_amt.setText(intent.getStringExtra("razorpay_amt"));
        status.setText(intent.getStringExtra("status"));
        txt_msg.setText(intent.getStringExtra("msg"));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
