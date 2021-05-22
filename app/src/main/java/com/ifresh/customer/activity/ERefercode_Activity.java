package com.ifresh.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ifresh.customer.R;
import com.ifresh.customer.adapter.FriendListReferAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.FriendCodeUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ERefercode_Activity extends AppCompatActivity {

    private EditText edtRefercode;
    private Button btn_avl, btn_update;
    private TextView txtmsg;
    Toolbar toolbar;
    Activity activity = ERefercode_Activity.this;
    ProgressBar progressbar;
    Session session;
    Context ctx = ERefercode_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_refercode_);
        edtRefercode = findViewById(R.id.edtRefercode);
        btn_avl = findViewById(R.id.btn_avl);
        btn_update = findViewById(R.id.btn_update);
        progressbar = findViewById(R.id.progressBar);
        txtmsg = findViewById(R.id.txtmsg);
        session = new Session(ERefercode_Activity.this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.your_code));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_update.setEnabled(false);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_api_updaterefercode(edtRefercode.getText().toString());
            }
        });

        btn_avl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(edtRefercode.getText().toString().length() == 0)
               {
                   ApiConfig.setSnackBar("Refer Code Is Empty", "RETRY", activity);
               }
               else if( edtRefercode.getText().toString().length() < 6)
               {
                   ApiConfig.setSnackBar("Refer Code Must Be Six Char", "RETRY", activity);
               }
               else if(checkcodeAlphaNumeric(edtRefercode.getText().toString()))
               {
                   call_api_finduser(edtRefercode.getText().toString());

               }
            }
        });
    }

    private void call_api_finduser(final String refer_code) {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        String url = Constant.BASEPATH+Constant.FINDUSERBYCODE+"?_id="+session.getData(Session.KEY_id) + "&refer_code=" + refer_code;
                        Log.d("Url===>",url);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("sucess").equalsIgnoreCase("200"))
                        {
                            progressbar.setVisibility(View.GONE);
                            txtmsg.setVisibility(View.VISIBLE);
                            btn_update.setEnabled(true);
                            btn_update.setBackgroundResource(R.drawable.layout_rectangle);
                        }
                        else{
                            Toast.makeText(ctx, "Refer Code Is Already Used", Toast.LENGTH_SHORT).show();
                        }
                        progressbar.setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.FINDUSERBYCODE+"?_id="+session.getData(Session.KEY_id) + "&refer_code=" + refer_code, params, true);
    }


    private void call_api_updaterefercode(final String refer_code) {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("_id", session.getData(Session.KEY_id));
        params.put("refer_code", refer_code);

        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("success").equalsIgnoreCase("200"))
                        {
                            progressbar.setVisibility(View.GONE);
                            session.setData(Session.KEY_REFER_CODE,refer_code);
                            btn_update.setBackgroundResource(R.drawable.layout_rectangle_4);
                            btn_update.setEnabled(false);
                            txtmsg.setVisibility(View.VISIBLE);
                            txtmsg.setText("Refer Code Successfully Updated");
                        }
                        else{
                            Toast.makeText(ctx, "Refer Code Could Not Updated.", Toast.LENGTH_SHORT).show();
                        }
                        progressbar.setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.UPDATEREFERCODE, params, true);

    }


    private boolean checkcodeAlphaNumeric(String refer_code) {
        // Regex to check string is alphanumeric or not.
        String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (refer_code == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given string
        // and regular expression.
        Matcher m = p.matcher(refer_code);

        // Return if the string
        // matched the ReGex
        return m.matches();


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



}