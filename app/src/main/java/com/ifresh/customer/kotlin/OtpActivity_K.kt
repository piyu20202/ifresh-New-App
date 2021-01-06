package com.ifresh.customer.kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.Constants.MessagePayloadKeys.FROM
import com.ifresh.customer.R
import com.ifresh.customer.activity.MainActivity
import com.ifresh.customer.helper.ApiConfig
import com.ifresh.customer.helper.Constant
import com.ifresh.customer.helper.Constant.*
import com.ifresh.customer.helper.GPSTracker
import com.ifresh.customer.helper.Session
import kotlinx.android.synthetic.main.activity_view_otp.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class OtpActivity_K : AppCompatActivity() {
    private val mContext:Context=this@OtpActivity_K
    private val activity = this
    private lateinit var session: Session
    lateinit var reqForm : String
    private lateinit var gps: GPSTracker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_otp)
        session = Session(mContext)
        gps = GPSTracker(this@OtpActivity_K)


        //val otp: String? = intent.getStringExtra("otp")
        val phone: String? = intent.getStringExtra("phone")
        reqForm = intent.getStringExtra("reqForm").toString()
        //edtotp.setText(otp)


        btnotpverify.setOnClickListener()
        {
            val etvOtp:String=edtotp.text.toString()
            if(etvOtp.length != 6)
            {
                ApiConfig.setSnackBar(getString(R.string.invalid_otp),"RETRY", activity)
            }
            else{
                if (phone != null) {
                    callotp(activity,etvOtp,phone)
                }
            }
        }


        tvResend.setOnClickListener(View.OnClickListener {
            if (phone != null) {
                reset_timer()
                call_resendotp(phone)
            };
        })

        reset_timer()

    }

    private fun reset_timer() {
        val timer = object: CountDownTimer(100000, 1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                val hms = java.lang.String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)  ))
                tvTime.text=hms //set text
            }
            override fun onFinish() {
                Log.d("hi", "finish")
                tvTime.text = getString(R.string.otp_receive_alert)
                tvResend.visibility = VISIBLE
                //this.start()
            }
        }
        timer.start()
    }

    private fun call_resendotp(phone:String) {
        val params: MutableMap<String, String> = HashMap()
        params["phone"] = phone
        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(SUCESS) == 200)
                    {
                        //Log.d("response", response)
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH+Constant.RESEND_OTP, params, true)
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

        var refer_code = ""

        if( dataObject.getJSONObject("user").has("refer_code") )
        {
            refer_code = dataObject.getJSONObject("user").getString("refer_code")
            //Log.d("refer_code",""+dataObject.getJSONObject("user").getString("refer_code"));
        }
        else{
            refer_code=""
        }
        session.createUserLoginSession_new(
                dataObject.getJSONObject("user").getString("_id"),
                dataObject.getJSONObject("user").getString("fname"),
                dataObject.getJSONObject("user").getString("lname"),
                dataObject.getJSONObject("user").getString("email"),
                dataObject.getJSONObject("user").getString("phone_no"),
                gps.latitude.toString(),
                gps.longitude.toString(),
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
                refer_code,
                dataObject.getJSONObject("user").getString("device_token")
        )
        val mainIntent = Intent(mContext, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainIntent);
        finish()
    }




}
