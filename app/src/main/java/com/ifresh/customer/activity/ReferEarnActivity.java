package com.ifresh.customer.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.ifresh.customer.R;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;

public class ReferEarnActivity extends AppCompatActivity {

    TextView txtrefercoin, txtcode, txtcopy, txtinvite,txtc,txtorderplace,txtmsg;
    Toolbar toolbar;
    Session session;
    String preText = "";
    String val;
    Integer order_count;
    StorePrefrence storeinfo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_frnd);

        session = new Session(ReferEarnActivity.this);
        storeinfo = new StorePrefrence(ReferEarnActivity.this);
        toolbar = findViewById(R.id.toolbar);
        txtrefercoin = findViewById(R.id.txtrefercoin);
        txtc = findViewById(R.id.txtc);
        txtorderplace = findViewById(R.id.txtorderplace);
        txtmsg = findViewById(R.id.txtmsg);


        if (Constant.REFER_EARN_METHOD.equals("rupees")) {
            /*preText = Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_BONUS  + " + " +
                      Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_BONUS;*/

             preText = Constant.SETTING_CURRENCY_SYMBOL + storeinfo.getString(Constant.FRIEND_ONE) + " + " +
                      Constant.SETTING_CURRENCY_SYMBOL + storeinfo.getString(Constant.FRIEND_ONE);


        } else {
            preText = Constant.REFER_EARN_BONUS + "% ";
        }
        //txtrefercoin.setText("Refer a friend & earn" + preText + " " +"also you can earn more on every successful order" + Constant.SETTING_CURRENCY_SYMBOL + Constant.REFER_EARN_ORDER_AMOUNT + ". which allows you to earn upto " + Constant.SETTING_CURRENCY_SYMBOL + Constant.MAX_EARN_AMOUNT + ".");

        val = storeinfo.getString(Constant.USER_REFER_AMT);
        txtrefercoin.setText("Refer a friend & earn " + preText + " " +"also you can earn more on every successful order" );
        txtcode = findViewById(R.id.txtcode);
        txtcopy = findViewById(R.id.txtcopy);
        txtinvite = findViewById(R.id.txtinvite);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.invite_frnd));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*  try{
            call_api_2(storeinfo.getString("mobile"), storeinfo.getString("user_id"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }*/

        txtorderplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtinvite.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(ReferEarnActivity.this, R.drawable.ic_share), null, null, null);
        txtcode.setText(session.getData(Session.KEY_REFER_CODE));
        txtcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txtcode.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ReferEarnActivity.this, R.string.refer_code_copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void OnInviteFrdClick(View view) {
        if (!txtcode.getText().toString().equals("code")) {
            try {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");

                /*shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.refer_share_msg_1)
                        + getResources().getString(R.string.app_name) + getString(R.string.refer_share_msg_2)
                        + "\n " + Constant.share_url + "refer/" + txtcode.getText().toString());*/

                /*shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.refer_share_msg_1) +" " + "*" + txtcode.getText().toString() + "*" +" "+
                                getString(R.string.refer_share_msg_2) + " " +  Constant.SETTING_CURRENCY_SYMBOL  + val +"/-."+ " " + getString(R.string.refer_share_msg_3) +"\n" + getString(R.string.applink)
                        );*/

                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, storeinfo.getString(Constant.MSG_1)+" " + "*" + txtcode.getText().toString() + "*" +" "+
                        storeinfo.getString(Constant.MSG_2) + " " +  Constant.SETTING_CURRENCY_SYMBOL  + val +"/-."+ " " + storeinfo.getString(Constant.MSG_3) +"\n" + storeinfo.getString(Constant.SHORT_LINK)
                );

                startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_frnd_title)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.refer_code_alert_msg), Toast.LENGTH_SHORT).show();
        }
    }

   /* private void call_api_2(String mobile, String user_id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile",mobile);
        params.put("id",user_id);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        //System.out.println("====res area " + response);

                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR))
                        {
                            storeinfo.setString(KEY_ORDER_COUNT,objectbject.getString("order_count"));
                            //storeinfo.setString(KEY_BALANCE,objectbject.getString("balance"));
                            order_count = Integer.parseInt(storeinfo.getString(KEY_ORDER_COUNT));
                            Log.d("ordercount", ""+ order_count);
                            if(order_count == 0)
                            {
                                txtc.setVisibility(View.GONE);
                                txtcode.setVisibility(View.GONE);
                                txtcopy.setVisibility(View.GONE);
                                txtc.setVisibility(View.GONE);
                                txtinvite.setVisibility(View.GONE);
                                txtorderplace.setVisibility(View.VISIBLE);
                                txtmsg.setVisibility(View.VISIBLE);

                            }
                            else if(order_count > 0)
                            {
                                txtc.setVisibility(View.VISIBLE);
                                txtcode.setVisibility(View.VISIBLE);
                                txtcopy.setVisibility(View.VISIBLE);
                                txtinvite.setVisibility(View.VISIBLE);
                                txtorderplace.setVisibility(View.GONE);
                                txtmsg.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ReferEarnActivity.this, Constant.GETUSERINFO, params, true);
    }*/



}
