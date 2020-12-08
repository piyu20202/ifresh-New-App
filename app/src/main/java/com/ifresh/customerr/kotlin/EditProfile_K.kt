package com.ifresh.customerr.kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customerr.R
import com.ifresh.customerr.helper.ApiConfig
import com.ifresh.customerr.helper.Constant
import com.ifresh.customerr.helper.Constant.AREA_N
import com.ifresh.customerr.helper.Constant.CITY_N
import com.ifresh.customerr.helper.Session
import kotlinx.android.synthetic.main.activity_view_editprofile.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class EditProfile_K : AppCompatActivity()
{
    private val mContext: Context = this@EditProfile_K
    private val activity = this
    //private lateinit var storePrefrence: StorePrefrence
    private lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = Session(mContext)
        setContentView(R.layout.activity_view_editprofile)
        setSupportActionBar(toolbar)
        supportActionBar?.title = mContext.getString(R.string.profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fillData()
        callApidefaultAdd(Constant.BASEPATH + Constant.GET_USERDEFULTADD)

        txtchangepassword.setOnClickListener(View.OnClickListener {
            val mainIntent = Intent(mContext, ChangePassword::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mainIntent)
        })

        btnsubmit.setOnClickListener(View.OnClickListener {
            when {
                edtname.text.isEmpty() -> {
                    ApiConfig.setSnackBar(getString(R.string.empty_name), "RETRY", activity)
                }
                edtemail.text.isEmpty() -> {
                    ApiConfig.setSnackBar(getString(R.string.empty_email), "RETRY", activity)
                }
                else -> {
                    // edit profile
                    callApi_editprfile(activity)
                }
            }
        })
    }

    private fun fillData() {
        edtname.setText(session.getData(Session.KEY_FIRSTNAME).toString())
        edtlname.setText(session.getData(Session.KEY_LASTNAME).toString())
        edtemail.setText(session.getData(Session.KEY_email).toString())
        edtMobile.setText(session.getData(Session.KEY_mobile).toString())
        txt_city.text = session.getData(CITY_N).toString()
        txt_area.text = session.getData(AREA_N).toString()

    }

    private fun callApi_editprfile(activity: EditProfile_K) {
        val params: MutableMap<String, String> = HashMap()
        //params["_id"] = storePrefrence.getString("ID")
        params["_id"] = session.getData(Session.KEY_id)
        params["phone_no"] = session.getData(Session.KEY_mobile)
        params["fname"] = edtname.text.toString()
        params["lname"] = edtlname.text.toString()
        params["email"] = edtemail.text.toString()
        params["device_id"]= ApiConfig.getDeviceId(mContext)
       ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        Toast.makeText(mContext, "Profile Update Successfully", Toast.LENGTH_SHORT)
                                .show()
                        session.setData(Session.KEY_FIRSTNAME, edtname.text.toString())
                        session.setData(Session.KEY_LASTNAME, edtlname.text.toString())
                        session.setData(Session.KEY_email, edtemail.text.toString())

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.EDITPROFILE, params, true)
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


    private fun callApidefaultAdd(url: String)
    {
        val params: MutableMap<String, String> = HashMap()
        Log.d("userId", session.getData(Session.KEY_id));
        params["userId"] = session.getData(Session.KEY_id)
        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("====res area=>$response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has(Constant.SUCESS)) {
                        if (jsonObject.getInt(Constant.SUCESS) == 200) {
                            //fill address
                            val jsonObject_data = jsonObject.getJSONObject("data")
                            if (jsonObject_data.length() > 0)
                            {
                                edtaddress.setText(jsonObject_data.getString("complete_address"))
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, url, params, false)
    }
}