package com.ifresh.customerr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.ifresh.customerr.R;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.kotlin.SetAddress_K;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customerr.helper.Constant.ADDRESS_DEFAULT_CHANGE_MSG;
import static com.ifresh.customerr.helper.Constant.ADDRESS_DELETE_MSG;
import static com.ifresh.customerr.helper.Constant.SELECT_DEFAULT_ADD;

public class SetDefaultAddress extends AppCompatActivity {
    Context mContext = SetDefaultAddress.this;
    Activity activity = SetDefaultAddress.this;
    Session session;
    Toolbar toolbar;
    EditText edtaddress_1, edtaddress_2, edtaddress_3;
    ImageView imgdelete_1, imgdelete_2, imgdelete_3;
    LinearLayout linear_other, linear_office, linear_home;
    CheckBox chHome, chWork, chOther;
    Button btnsubmit;
    String default_add_home, default_add_2_home, default_add_work,default_add_2_work,default_add_other, default_add_2_other;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresstype);
        session=new Session(mContext);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.defaultaddress));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtaddress_1 = findViewById(R.id.edtaddress);
        edtaddress_2 = findViewById(R.id.edtaddress_2);
        edtaddress_3 = findViewById(R.id.edtaddress_3);
        linear_home = findViewById(R.id.linear_home);
        linear_office = findViewById(R.id.linear_office);
        linear_other = findViewById(R.id.linear_other);

        imgdelete_1 = findViewById(R.id.imgdelete_1);
        imgdelete_2 = findViewById(R.id.imgdelete_2);
        imgdelete_3 = findViewById(R.id.imgdelete_3);

        btnsubmit = findViewById(R.id.btnsubmit);
        chHome = findViewById(R.id.chHome);
        chWork = findViewById(R.id.chWork);
        chOther = findViewById(R.id.chOther);

        if(chHome.isChecked())
        {
            chWork.setChecked(false);
            chOther.setChecked(false);
        }
        else if(chWork.isChecked())
        {
            chHome.setChecked(false);
            chOther.setChecked(false);
        }
        else if(chOther.isChecked())
        {
            chHome.setChecked(false);
            chWork.setChecked(false);
        }

       chHome.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              chWork.setChecked(false);
              chOther.setChecked(false);
              chHome.setChecked(true);
              callApi_updatedefultAdd(session.getData(Constant.HOME_ADD_ID), default_add_home, default_add_2_home, "1");
           }
       });

        chWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chHome.setChecked(false);
                chOther.setChecked(false);
                chWork.setChecked(true);
                callApi_updatedefultAdd(session.getData(Constant.OFFICE_ADD_ID), default_add_work, default_add_2_work,"2");
            }
        });

        chOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chHome.setChecked(false);
                chWork.setChecked(false);
                chOther.setChecked(true);
                callApi_updatedefultAdd(session.getData(Constant.OTHER_ADD_ID), default_add_other,default_add_2_other,"3");
            }
        });

       /* session.setData(Constant.HOME_ADD_ID, "");
        session.setData(Constant.HOME_ADD,"");*/
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtaddress_1.getText().length() == 0 || edtaddress_2.getText().length() == 0 || edtaddress_3.getText().length() == 0)
                {
                    Intent intent = new Intent(SetDefaultAddress.this, SetAddress_K.class);
                    intent.putExtra("pas_address","");
                    intent.putExtra("from","");
                    intent.putExtra("addresstype_id","");
                    startActivity(intent);
                }
            }
        });

        imgdelete_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConformationView(session.getData(Constant.HOME_ADD_ID), Constant.HOME_ADD_ID);
            }
        });
        imgdelete_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConformationView(session.getData(Constant.OFFICE_ADD_ID),Constant.OFFICE_ADD_ID);
            }
        });
        imgdelete_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConformationView(session.getData(Constant.OTHER_ADD_ID),Constant.OTHER_ADD_ID);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        String url = makeurl_filldefultAdd();
        callApi_fillAdd(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ConformationView(final String address_id, final String session_label)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_4, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvremove,tvclose,txt_msg;

        tvremove = dialogView.findViewById(R.id.tvcancel);
        tvclose = dialogView.findViewById(R.id.tvclose);
        txt_msg = dialogView.findViewById(R.id.txt_msg);

        tvclose.setText("CANCEL");
        tvremove.setText("REMOVE");
        txt_msg.setText(activity.getResources().getString(R.string.deleteproductmsg));




        tvremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callApi_deleteadd(address_id,session_label);
            }
        });

        tvclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
     }

    private void callApi_deleteadd(final String address_id, final String session_label) {
        Map<String, String> params = new HashMap<String, String>();
        Log.d("val1", address_id);
        Log.d("val3", session.getData(Session.KEY_mobile));
        params.put("address_id", address_id);
        params.put("user_id", session.getData(session.getData(Session.KEY_id)));

        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getInt(Constant.SUCESS) == 200)
                        {
                            Toast.makeText(mContext, ADDRESS_DELETE_MSG, Toast.LENGTH_SHORT).show();
                            session.setData(session_label,"");
                            String url = makeurl_filldefultAdd();
                            callApi_fillAdd(url);
                        }
                        else{
                            Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.SET_DELETEADD, params, false);
    }

    private String makeurl_filldefultAdd() {
        String url = Constant.BASEPATH + Constant.GET_DEFULTADD + session.getData(Session.KEY_id);
        Log.d("url", url);
        return url;
    }

    private void callApi_fillAdd(String url) {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt(Constant.SUCESS) == 200)
                        {
                            edtaddress_1.setText("");
                            edtaddress_2.setText("");
                            edtaddress_3.setText("");
                            default_add_home="";
                            default_add_2_home="";
                            default_add_work="";
                            default_add_2_work="";
                            default_add_other="";
                            default_add_2_other="";
                            session.setData(Session.KEY_ADDRESS, "");
                            session.setData("pas_address", "");
                            session.setData("addresstype_id", "0");
                            chHome.setChecked(false);
                            chWork.setChecked(false);
                            chOther.setChecked(false);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for(int i = 0; i<jsonArray.length(); i++)
                            {
                                    JSONObject mjsonobj = jsonArray.getJSONObject(i);

                                        if(mjsonobj.getString("address_type").equalsIgnoreCase("1"))
                                        {
                                            default_add_home = mjsonobj.getString("address1") + " "+
                                                    mjsonobj.getString("address2") + " "+"\n"+
                                                    "PinCode:"+" "+mjsonobj.getString("pincode");
                                            edtaddress_1.setText(default_add_home);

                                            session.setData(Constant.HOME_ADD_ID, mjsonobj.getString("_id"));

                                            default_add_2_home = mjsonobj.getString("address1") + "$"+
                                                    mjsonobj.getString("address2") + "$"+ "PinCode"+"$"+mjsonobj.getString("pincode");

                                            if(mjsonobj.getBoolean("default_address") )
                                            {
                                                chHome.setChecked(true);
                                                chWork.setChecked(false);
                                                chOther.setChecked(false);
                                                session.setData(Session.KEY_ADDRESS, default_add_home);
                                                session.setData("pas_address", default_add_2_home);
                                                session.setData("addresstype_id", "1");

                                            }

                                        }

                                        if(mjsonobj.getString("address_type").equalsIgnoreCase("2"))
                                        {
                                            default_add_work = mjsonobj.getString("address1") + " "+
                                                    mjsonobj.getString("address2") + " "+"\n"+
                                                    "PinCode:"+" "+mjsonobj.getString("pincode");
                                            edtaddress_2.setText(default_add_work);

                                            session.setData(Constant.OFFICE_ADD_ID, mjsonobj.getString("_id"));
                                            default_add_2_work = mjsonobj.getString("address1") + "$" +
                                                    mjsonobj.getString("address2") + "$" + "PinCode" + "$" + mjsonobj.getString("pincode");
                                            if(mjsonobj.getBoolean("default_address") )
                                            {
                                                chWork.setChecked(true);
                                                chHome.setChecked(false);
                                                chOther.setChecked(false);

                                                session.setData(Session.KEY_ADDRESS, default_add_work);
                                                session.setData("pas_address", default_add_2_work);
                                                session.setData("addresstype_id", "2");
                                            }

                                        }

                                        if(mjsonobj.getString("address_type").equalsIgnoreCase("3"))
                                        {
                                            default_add_other = mjsonobj.getString("address1") + " "+
                                                    mjsonobj.getString("address2") + " "+"\n"+
                                                    "PinCode:"+" "+mjsonobj.getString("pincode");
                                            edtaddress_3.setText(default_add_other);

                                            session.setData(Constant.OTHER_ADD_ID, mjsonobj.getString("_id"));
                                            default_add_2_other = mjsonobj.getString("address1") + "$"+
                                                    mjsonobj.getString("address2") + "$"+ "PinCode"+"$"+mjsonobj.getString("pincode");

                                            if(mjsonobj.getBoolean("default_address") )
                                            {
                                                chOther.setChecked(true);
                                                chWork.setChecked(false);
                                                chHome.setChecked(false);

                                                session.setData(Session.KEY_ADDRESS, default_add_other);
                                                session.setData("pas_address", default_add_2_other);
                                                session.setData("addresstype_id", "3");
                                            }

                                        }
                            }
                               setVisbility_CheckBox();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, false);

    }

    private void setVisbility_CheckBox()
    {
        if(edtaddress_1.getText().length() == 0)
        {
            linear_home.setVisibility(View.GONE);
        }
        else{
            linear_home.setVisibility(View.VISIBLE);
        }


        if(edtaddress_2.getText().length()==0)
        {
            linear_office.setVisibility(View.GONE);
        }
        else{
            linear_office.setVisibility(View.VISIBLE);
        }

        if(edtaddress_3.getText().length()==0)
        {
            linear_other.setVisibility(View.GONE);
        }
        else{
            linear_other.setVisibility(View.VISIBLE);
        }
    }

    private void callApi_updatedefultAdd(String address_id, final String default_address, final String default_add_2, final String address_type)
     {
         Log.d("val", default_address);
         Map<String, String> params = new HashMap<String, String>();
         Log.d("val1", address_id);
         Log.d("val3", session.getData(Session.KEY_id));

         params.put("address_id", address_id);
         params.put("default_address", "true");
         params.put("user_id", session.getData(Session.KEY_id));


         ApiConfig.RequestToVolley_POST(new VolleyCallback() {
             @Override
             public void onSuccess(boolean result, String response) {
                 if (result) {
                     try {
                         System.out.println("====res area " + response);
                         JSONObject jsonObject = new JSONObject(response);
                         if(jsonObject.getInt(Constant.SUCESS) == 200)
                         {
                             Toast.makeText(mContext, ADDRESS_DEFAULT_CHANGE_MSG, Toast.LENGTH_SHORT).show();
                             //session.setData(Constant.DEFAULT_ADD, default_address);
                             Log.d("Add=>:", default_address);
                             Log.d("Add=>", default_add_2);


                             session.setData(Session.KEY_ADDRESS, default_address);
                             session.setData("pas_address", default_add_2);
                             session.setData("addresstype_id", address_type);



                         }

                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }, activity, Constant.BASEPATH+Constant.SET_DEFULTADD, params, false);
     }








}
