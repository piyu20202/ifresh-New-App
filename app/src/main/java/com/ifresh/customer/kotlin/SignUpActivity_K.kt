package com.ifresh.customer.kotlin


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customer.R
import com.ifresh.customer.helper.*
import com.ifresh.customer.helper.Constant.*

import kotlinx.android.synthetic.main.activity_view_signup.*
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern


class SignUpActivity_K : AppCompatActivity()
{
    private val mContext:Context = this@SignUpActivity_K
    private val activity = this
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_signup)
        session = Session(mContext)

        btnsignup.setOnClickListener(){
            val phoneNo = edtsignupMobile.text.toString()
            if(edtLoginMobile.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_phone), "RETRY", activity)
            }
            else{
                if(isValidMobile(edtsignupMobile.text.toString()))
                {
                    if(edtfirstname.text.isEmpty())
                    {
                      ApiConfig.setSnackBar(getString(R.string.invalid_firstname), "RETRY", activity)
                    }
                    else{
                        /*if(edtlastname.text.isEmpty())
                        {
                            ApiConfig.setSnackBar(getString(R.string.invalid_lastname), "RETRY", activity)
                        }
                        else{
                            // all field have data
                            callaSighup(activity, phoneNo)
                        }*/
                        // all field have data
                        callaSighup(activity, phoneNo)
                    }

                }
                else{
                    ApiConfig.setSnackBar(getString(R.string.invalid_phone), "RETRY", activity)
                }
            }
        }

        tvlogin.setOnClickListener(){
            val mainIntent = Intent(mContext, SignInActivity_K::class.java)
            startActivity(mainIntent);
        }

    }

    private fun callaSighup(activity: SignUpActivity_K, phone_no: String) {
        val params: MutableMap<String, String> = HashMap()
        if(edtlastname.text.isEmpty())
            params["lname"] = ""
        else
            params["lname"] = edtlastname.text.toString()
     
        params["phone"] = phone_no
        params["fname"] = edtfirstname.text.toString()
        params["reqForm"] = "signup"
        params["device_id"]= ApiConfig.getDeviceId(mContext)
        params["token"]= session.getData("token")
        params[FRIEND_CODE]= edtRefer.text.toString().trim()
        //params[REFERRAL_CODE]= randomAlphaNumeric(8)

        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {

                        val mainIntent = Intent(mContext, OtpActivity_K::class.java)

                        mainIntent.putExtra("reqForm", "signup")
                        mainIntent.putExtra("phone", phone_no)
                        //session.setData("refer_code",jsonObject.getString("referCode"))
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent);
                        finish()

                    } else if (jsonObject.getInt(SUCESS) == 400) {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                        val mainIntent = Intent(mContext, SignInActivity_K::class.java)
                        startActivity(mainIntent);

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + LOGIN, params, true)
    }


    private fun isValidMobile(phone: String): Boolean
    {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length > 6 && phone.length == 10;
        }
        return false;
    }





}
