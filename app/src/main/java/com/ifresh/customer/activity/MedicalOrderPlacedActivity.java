package com.ifresh.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ifresh.customer.R;
import com.ifresh.customer.helper.DatabaseHelper;

public class MedicalOrderPlacedActivity extends AppCompatActivity {
    Toolbar toolbar;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalorder_placed);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnshopping) {
            startActivity(new Intent(MedicalOrderPlacedActivity.this, MainActivity.class));
            finishAffinity();
        } else if (id == R.id.txtsummary) {
            startActivity(new Intent(MedicalOrderPlacedActivity.this, OrderListActivity_2.class));
            finishAffinity();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
