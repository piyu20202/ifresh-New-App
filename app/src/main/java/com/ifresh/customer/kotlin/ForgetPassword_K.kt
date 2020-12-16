package com.ifresh.customer.kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customer.R
import com.ifresh.customer.helper.*
import kotlinx.android.synthetic.main.activity_view_forget.*

import org.json.JSONObject
import java.util.HashMap
import java.util.regex.Pattern


class ForgetPassword_K : AppCompatActivity()
{
    private val mContext: Context = this@ForgetPassword_K
    private val activity = this
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_forget)
        session = Session(mContext)

        btnrecover.setOnClickListener(){
            val phoneNo = edtforgotmobile.text.toString()
            if(edtforgotmobile.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_phone), "RETRY", activity)
            }
            else{
                if(isValidMobile(edtforgotmobile.text.toString()))
                {
                    call_forgotpass(activity, phoneNo)
                }
                else{
                    ApiConfig.setSnackBar(getString(R.string.invalid_phone), "RETRY", activity)
                }
            }
        }
    }


    private fun call_forgotpass(activity: ForgetPassword_K, phone_no: String) {
        val params: MutableMap<String, String> = HashMap()
        params["phone"] = phone_no
        params["reqForm"] = "forgot"
        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {
                        //val otp = jsonObject.getString("data")
                        val mainIntent = Intent(mContext, OtpActivity_K::class.java)
                        //mainIntent.putExtra("otp", otp)
                        mainIntent.putExtra("reqForm", "forget")
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent);
                        finish()
                    }
                    else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.FORGETPASSWORD, params, true)
    }


    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length in 7..13
        } else false
    }






}