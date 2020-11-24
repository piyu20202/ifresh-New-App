package com.ifresh.customerr.kotlin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ifresh.customerr.R
import com.ifresh.customerr.activity.MainActivity
import com.ifresh.customerr.helper.*
import com.ifresh.customerr.helper.Constant.*
import kotlinx.android.synthetic.main.activity_view_otp.*
import org.json.JSONObject
import java.util.HashMap

class OtpActivity_K : AppCompatActivity() {
    private val mContext:Context=this@OtpActivity_K
    private val activity = this
    private lateinit var session: Session
    lateinit var reqForm : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = Session(mContext)
        setContentView(R.layout.activity_view_otp)

        val otp: String? = intent.getStringExtra("otp")
        val phone: String? = intent.getStringExtra("phone")
        reqForm = intent.getStringExtra("reqForm").toString()
        edtotp.setText(otp)

        btnotpverify.setOnClickListener()
        {
            val etvOtp:String=edtotp.text.toString()
            if(etvOtp.length != 4)
            {
                ApiConfig.setSnackBar(getString(R.string.invalid_otp),"RETRY", activity)
            }
            else{
                if (phone != null) {
                    callotp(activity,etvOtp,phone)
                }
            }
        }
    }

    private fun callotp(activity: OtpActivity_K, etvOtp: String, phone:String) {
        val params: MutableMap<String, String> = HashMap()
        params["otp"] = etvOtp
        params["phone"] = phone

        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(SUCESS) == 200)
                    {
                        if(jsonObject.getBoolean("varified"))
                        {
                           if(reqForm == "signin" || reqForm == "signup")
                           {
                               SaveUserData(jsonObject.getJSONObject("data"))
                           }
                           else if(reqForm == "changeno")
                           {
                               val mainIntent = Intent(mContext, SignUpActivity_K::class.java)
                               mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                               startActivity(mainIntent);
                               finish()
                           }
                           else if(reqForm == "forget"){
                               val mainIntent = Intent(mContext, ChangePassword::class.java)
                               mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                               startActivity(mainIntent);
                               finish()
                           }
                        }
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH+Constant.VARIFYOTP, params, true)
    }


    private fun SaveUserData(dataObject: JSONObject)
    {
        /*Log.d("COUNTRY_N====>",""+session.getData(COUNTRY_N));
        Log.d("STATE_N====>",""+session.getData(STATE_N));
        Log.d("CITY_N====>",""+session.getData(CITY_N));
        Log.d("AREA_N====>",""+session.getData(AREA_N));
        Log.d("SUBAREA_N====>",""+session.getData(SUBAREA_N));*/

        session.setData(AUTHTOKEN, dataObject.getString("authtoken"))
        session.setData("role", dataObject.getJSONObject("user").getString("role_type"))
        Log.d("refer_code",""+dataObject.getJSONObject("user").getString("refer_code"));

        session.createUserLoginSession_new(
                dataObject.getJSONObject("user").getString("_id"),
                dataObject.getJSONObject("user").getString("fname"),
                dataObject.getJSONObject("user").getString("lname"),
                dataObject.getJSONObject("user").getString("email"),
                dataObject.getJSONObject("user").getString("phone_no"),
                session.getCoordinates(Session.KEY_LATITUDE),
                session.getCoordinates(Session.KEY_LONGITUDE),
                session.getData(COUNTRY_N),
                session.getData(STATE_N),
                session.getData(CITY_N),
                session.getData(AREA_N),
                session.getData(SUBAREA_N),
                session.getData(COUNTRY_ID),
                session.getData(STATE_ID),
                session.getData(CITY_ID),
                session.getData(AREA_ID),
                session.getData(SUBAREA_ID),
                dataObject.getString(AUTHTOKEN),
                dataObject.getJSONObject("user").getString("refer_code"),
                dataObject.getJSONObject("user").getString("device_token")


        )

        val mainIntent = Intent(mContext, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainIntent);
        finish()
    }


}
