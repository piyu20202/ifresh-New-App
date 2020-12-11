package com.ifresh.customer.kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customer.R

import com.ifresh.customer.helper.ApiConfig
import com.ifresh.customer.helper.Constant
import com.ifresh.customer.helper.Session
import kotlinx.android.synthetic.main.activity_view_changepassword.*

import org.json.JSONObject
import java.util.HashMap

class ChangePassword : AppCompatActivity()
{
    private val mContext: Context = this@ChangePassword
    private val activity = this
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_changepassword)
        session = Session(mContext)
        Log.d("mobile", session.getData(Session.KEY_mobile))

        edtoldpsw.setText(session.getData(Session.KEY_mobile))


        btnchangepsw.setOnClickListener(){
          if(edtoldpsw.text.isEmpty())
          {
              ApiConfig.setSnackBar(getString(R.string.enter_old_pass), "RETRY", activity)
          }
          else if(edtnewpsw.text.isEmpty()){
              ApiConfig.setSnackBar(getString(R.string.enter_new_pass), "RETRY", activity)
          }
          else if(edtcnfpsw.text.isEmpty()){
              ApiConfig.setSnackBar(getString(R.string.enter_confirm_pass), "RETRY", activity)
          }
          else if( !edtnewpsw.text.toString().equals(edtcnfpsw.text.toString(),false) )
          {
              ApiConfig.setSnackBar(getString(R.string.pass_not_match), "RETRY", activity)
          }
          else{
              callapi_changepassword(activity, edtoldpsw.text.toString(), edtnewpsw.text.toString(), edtcnfpsw.text.toString())
          }


        }
    }

    private fun callapi_changepassword(activity: ChangePassword, oldpsw: String, newpsw: String, cnfpsw:String) {
        val params: MutableMap<String, String> = HashMap()
        params["oldpsw"] = oldpsw
        params["newpsw"] = newpsw
        params["confirmPassword"]=cnfpsw
        params["phone_no"] = session.getData(Session.KEY_mobile)
        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        Toast.makeText(mContext, Constant.PASSWORD_CHANGE_MSG , Toast.LENGTH_SHORT).show()
                        val mainIntent = Intent(mContext, SignInActivity_K::class.java)
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
        }, activity, Constant.BASEPATH + Constant.CHANGEPASSWORD, params, true)
    }


}