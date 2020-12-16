package com.ifresh.customer.kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customer.R
import com.ifresh.customer.helper.ApiConfig
import com.ifresh.customer.helper.Constant
import com.ifresh.customer.helper.Session
import kotlinx.android.synthetic.main.activity_view_phonechange.*
import org.json.JSONObject
import java.util.HashMap
import java.util.regex.Pattern

class PhoneUpdate_K : AppCompatActivity(){

    private val mContext: Context = this@PhoneUpdate_K
    private val activity = this
    //private lateinit var storePrefrence: StorePrefrence
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_phonechange)
        //storePrefrence = StorePrefrence(mContext)
        session = Session(mContext)


        btnchange_phone.setOnClickListener(View.OnClickListener {

            if(edtold_mobile.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_phone), "RETRY", activity)
            }
            else {
                if(isValidMobile(edtold_mobile.text.toString()))
                {
                       if(edtnew_mobile.text.isEmpty())
                       {
                           ApiConfig.setSnackBar(getString(R.string.empty_phone), "RETRY", activity)
                       }
                    else{
                           if(isValidMobile(edtnew_mobile.text.toString()))
                           {
                               //call api
                               callaChangeMobile(activity,edtold_mobile.text.toString(), edtnew_mobile.text.toString())
                           }
                           else{
                               ApiConfig.setSnackBar(getString(R.string.invalid_phone), "RETRY", activity)
                           }
                       }
                }
                else{
                    ApiConfig.setSnackBar(getString(R.string.invalid_phone), "RETRY", activity)
                }

            }
        })

    }

    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length > 6 && phone.length <= 13
        } else false
    }

    private fun callaChangeMobile(activity: PhoneUpdate_K, phoneNo_old: String, phoneNo_new: String ) {
        val params: MutableMap<String, String> = HashMap()
        params["phone_old"] = phoneNo_old
        params["phone_new"] = phoneNo_new
        params["reqFrom"] = "changeno"
        params["device_id"]= ApiConfig.getDeviceId(mContext)
        params["fcm_id"]= "123"

        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        //val otp = jsonObject.getString("data")
                        val mainIntent = Intent(mContext, OtpActivity_K::class.java)
                        //mainIntent.putExtra("otp", otp)
                        mainIntent.putExtra("reqForm", "changeno")

                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent);
                        finish()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.CHANGENO, params, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}