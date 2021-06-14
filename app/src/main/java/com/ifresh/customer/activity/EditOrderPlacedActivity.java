package com.ifresh.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ifresh.customer.R;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.StorePrefrence;

public class EditOrderPlacedActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper databaseHelper;
    StorePrefrence storePrefrence;
    Context ctx = EditOrderPlacedActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editactivity_order_placed);
        storePrefrence = new StorePrefrence(ctx);

        databaseHelper = new DatabaseHelper(EditOrderPlacedActivity.this);
        //databaseHelper.DeleteAllOrderData();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.quantum_grey900));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnshopping) {
            startActivity(new Intent(EditOrderPlacedActivity.this, MainActivity.class));
            finishAffinity();
        } else if (id == R.id.txtsummary) {
            startActivity(new Intent(EditOrderPlacedActivity.this, OrderListActivity_2.class));
            finishAffinity();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
        //return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
