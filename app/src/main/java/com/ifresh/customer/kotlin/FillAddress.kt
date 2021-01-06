package com.ifresh.customer.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ifresh.customer.R
import com.ifresh.customer.activity.MainActivity
import com.ifresh.customer.activity.MapsActivity
import com.ifresh.customer.activity.SetDefaultAddress_2
import com.ifresh.customer.adapter.*
import com.ifresh.customer.helper.*
import com.ifresh.customer.helper.Constant.*
import com.ifresh.customer.model.*
import kotlinx.android.synthetic.main.activity_location_selection.spin_area
import kotlinx.android.synthetic.main.activity_location_selection.spin_area_sub
import kotlinx.android.synthetic.main.activity_location_selection.spin_city
import kotlinx.android.synthetic.main.activity_location_selection.spin_state
import kotlinx.android.synthetic.main.activity_view_setaddress.btnsave
import kotlinx.android.synthetic.main.activity_view_setaddress.chHome
import kotlinx.android.synthetic.main.activity_view_setaddress.chOther
import kotlinx.android.synthetic.main.activity_view_setaddress.chWork
import kotlinx.android.synthetic.main.activity_view_setaddress.edthno
import kotlinx.android.synthetic.main.activity_view_setaddress.edtlandmark
import kotlinx.android.synthetic.main.activity_view_setaddress.edtpincode
import kotlinx.android.synthetic.main.activity_view_setaddress_3.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FillAddress : AppCompatActivity(), OnMapReadyCallback
{
    private var activity = this
    private val  mContext: Context = this@FillAddress
    private val arrayListAreaType = arrayListOf<AddressType>()
    private var areaTypeAdapter: AreaTypeAdapter?=null
    private lateinit var session:Session
    private lateinit var storeinfo: StorePrefrence


    var addresstype_id:String="0"
    var mapFragment: SupportMapFragment? = null
    var toolbar: Toolbar? = null
    var userId:String=""

    private val arrayListCity = arrayListOf<CityName>()
    private val arrayListArea = arrayListOf<Area>()
    private val arrayListSubArea = arrayListOf<SubArea>()
    private val arrayListState = arrayListOf<State>()

    private var areaAdapter: AreaAdapter?=null
    private var subareaAdapter: SubAreaAdapter?=null
    private var cityAdapter: CityAdapter?=null

    private var stateAdapter: StateAdapter?=null
    private var countryid:String=""
    private var stateid:String=""
    private var cityid:String=""
    private var areaid:String=""
    private var subareaid:String="111"
    private var str_state = ""
    private var str_city = ""
    private var str_area = ""
    private var str_subarea = ""

    lateinit var databaseHelper: DatabaseHelper
    var isbackto_home:Boolean = false
    private lateinit var gps: GPSTracker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_setaddress_3)
        session = Session(mContext)
        storeinfo = StorePrefrence(mContext)
        databaseHelper =  DatabaseHelper(mContext);
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = mContext.getString(R.string.address_save)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        // spinner country
        /* spin_addresstype.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
             override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                 if(pos > 0)
                 {
                     val addressType: AddressType = arrayListAreaType[pos]
                     Log.d("id==>", "" + addressType.adress_id)
                     addresstype_id = addressType.adress_id.toString()
                 }
             }
             override fun onNothingSelected(p0: AdapterView<*>?) {
                 addresstype_id="-1"
             }
         }*/


        // spinner state
        spin_state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val state: State = arrayListState[pos]
                //Log.d("id==>", "" + state.state_id)
                str_state = state.state_name.toString()
                stateid = state.state_id.toString()

                //session.setData(STATE_ID,str_state)
                //session.setData(STATE_N, stateid)
                //arrayListCity.clear()
                //callApi_city(activity, stateid)
                /*if(pos > 0)
                {
                    str_state = state.state_name.toString()
                    stateid = state.state_id.toString()
                    callApi_city(activity, stateid)
                }*/
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                stateid = "-1"
                str_state = ""
            }
        }

        //spinner city
        spin_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                //Log.d("postion", pos.toString());
                if(pos > 0)
                {
                    val city: CityName = arrayListCity[pos]
                    cityid = city.city_id.toString()
                    str_city = city.city_name.toString()
                    callApi_area(activity, cityid)

                    last_city.setText("")
                    last_area.setText("")
                    last_subarea.setText("")

                }
                else{
                    val city: CityName = arrayListCity[pos]
                    cityid = city.city_id.toString()
                    str_city = city.city_name.toString()
                    callApi_area(activity, cityid)

                    last_city.setText("")
                    //last_area.setText("")
                    //last_subarea.setText("")
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                cityid= "-1"
                str_city=""
            }

        }

        //spinner area
        spin_area.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long)
            {
                if(pos > 0)
                {
                    val area: Area = arrayListArea[pos]
                    areaid = area.area_id.toString()
                    str_area = area.area_name.toString()

                    //callApi_subarea(activity, areaid, true)

                    last_area.setText("")
                    last_subarea.setText("")


                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                areaid = "-1"
                str_area=""
            }
        }

        //spinner sub area
        /*spin_area_sub.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val subArea: SubArea = arrayListSubArea[pos]
                    subareaid = subArea.subarea_id.toString()
                    str_subarea = subArea.subarea_name.toString()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                subareaid = "-1"
                str_subarea=""
            }

        }*/

        chHome.setOnClickListener {
            chHome.isChecked = true
            chWork.isChecked = false
            chOther.isChecked = false
            addresstype_id = "1"
        }
        chWork.setOnClickListener {
            chWork.isChecked = true
            chHome.isChecked = false
            chOther.isChecked = false
            addresstype_id = "2"
        }
        chOther.setOnClickListener {
            chOther.isChecked = true
            chHome.isChecked = false
            chWork.isChecked = false
            addresstype_id = "3"
        }

        btnsave.setOnClickListener(View.OnClickListener {
            when {
                edthno.text.isEmpty() -> {
                    ApiConfig.setSnackBar(getString(R.string.empty_hno), "RETRY", activity)
                }
                edtlandmark.text.isEmpty() -> {
                    ApiConfig.setSnackBar(getString(R.string.empty_landmark), "RETRY", activity)
                }
                addresstype_id == "0" -> {
                    ApiConfig.setSnackBar(getString(R.string.empty_addtype), "RETRY", activity)
                }
                cityid.isEmpty() || cityid == "-1" -> {
                    ApiConfig.setSnackBar("Please Select City", "RETRY", activity)
                }
                areaid.isEmpty() || areaid == "-1" -> {
                    ApiConfig.setSnackBar("Please Select Area", "RETRY", activity)
                }
                /*subareaid.isEmpty() || subareaid == "-1" -> {
                    ApiConfig.setSnackBar("Please Select Sub Area", "RETRY", activity)
                }*/
                else -> {
                    call_saveaddress(activity)
                }
            }

        })

        init_state()
        init_city()
        init_area()
        //init_subarea()
        // callApi_state(activity, countryid)

    }

    private fun call_saveaddress(activity: FillAddress) {
        pdialog.visibility=View.VISIBLE
        val params: MutableMap<String, String> = HashMap()
        params["address1"] = edthno.text.toString()
        params["address2"] = edtlandmark.text.toString()
        params["pincode"] = edtpincode.text.toString()
        session.setData("pincode",edtpincode.text.toString())
        params["phone_no"] = session.getData(Session.KEY_mobile)
        params["areaId"] = areaid
        params["cityId"] = cityid
        params["stateId"] = storeinfo.getString("state_id")
        params["countryId"] = session.getData(COUNTRY_ID)
        params["lat"] = gps.latitude.toString()
        params["long"] = gps.longitude.toString()
        params["address_type"] = addresstype_id
        params["userId"] = session.getData(Session.KEY_id)
        Log.d("address1", edthno.text.toString())
        Log.d("address2", edtlandmark.text.toString())
        Log.d("pincode", edtpincode.text.toString())
        Log.d("phone_no", session.getData(Session.KEY_mobile))
        Log.d("areaId", areaid)
        Log.d("cityId", cityid)
        Log.d("stateId", storeinfo.getString("state_id"))
        Log.d("countryId", session.getData(COUNTRY_ID))
        Log.d("lat", gps.latitude.toString())
        Log.d("long", gps.longitude.toString())
        Log.d("address_type", addresstype_id)
        Log.d("userId", session.getData(Session.KEY_id))


        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(SUCESS) == 200)
                    {
                        Toast.makeText(mContext, ADDRESS_SAVEMSG, Toast.LENGTH_SHORT).show()

                        edthno.setText("");
                        edtlandmark.setText("");
                        edtpincode.setText("");

                        addresstype_id = "0";
                        chOther.isChecked = false
                        chHome.isChecked = false
                        chWork.isChecked = false
                        pdialog.visibility=View.GONE


                        onBackPressed()
                    }
                    else {
                        pdialog.visibility=View.GONE
                        Toast.makeText(mContext, jsonObject.getString("Error Occur Please Try Again"), Toast.LENGTH_SHORT)
                                .show()
                    }


                } catch (e: java.lang.Exception) {
                    pdialog.visibility=View.GONE
                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.SAVE_DELIVERYADDRESS, params, true)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        gps = GPSTracker(this@FillAddress)
        if( session.getCoordinates(Session.KEY_LATITUDE)=="0.0" || session.getCoordinates(Session.KEY_LONGITUDE) == "0.0")
            tvCurrent.text = getString(R.string.location_1) + ApiConfig.getAddress(gps.latitude,gps.longitude,activity)
        else
            tvCurrent.text = getString(R.string.location_1) + ApiConfig.getAddress(session.getCoordinates(Session.KEY_LATITUDE).toDouble(),session.getCoordinates(Session.KEY_LONGITUDE).toDouble(),activity)




        Handler().postDelayed({ mapFragment!!.getMapAsync(this@FillAddress) }, 1000)
        userId = intent.getStringExtra("userId").toString();

        if(userId.isNotEmpty())
        {
            //user id is not empty
            callApidefaultAdd(BASEPATH + GET_USERDEFULTADD, userId)
        }


    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        if (ContextCompat.checkSelfPermission(this@FillAddress, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
                            ActivityCompat.requestPermissions(this@FillAddress, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    0)
                        }
                        .create()
                        .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0)
            }
        }

        val mMap: GoogleMap = googleMap
        val saveLatitude:Double
        val saveLongitude:Double

        if( session.getCoordinates(Session.KEY_LATITUDE)=="0.0" || session.getCoordinates(Session.KEY_LONGITUDE) == "0.0"){
            saveLatitude = gps.latitude.toDouble()
            saveLongitude = gps.longitude.toDouble()
        }
        else{
            saveLatitude = session.getCoordinates(Session.KEY_LATITUDE).toDouble()
            saveLongitude = session.getCoordinates(Session.KEY_LONGITUDE).toDouble()
        }
        mMap.clear()
        val latLng = LatLng(saveLatitude, saveLongitude)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.location)
        val marker = MarkerOptions().position(LatLng(gps.latitude, gps.longitude)).draggable(true).title(getString(R.string.current_location))
        marker.icon(icon)
        mMap.addMarker(marker)
        val cameraPosition = CameraPosition.Builder()
                .target(latLng).zoom(15f).tilt(60f).build()
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition))
    }

    fun UpdateLocation_pro(view: View?)
    {
        if (ApiConfig.isGPSEnable(this@FillAddress)) //startActivity(new Intent(ProfileActivity.this, MapActivity.class));
            startActivity(Intent(this@FillAddress, MapsActivity::class.java)) else ApiConfig.displayLocationSettingsRequest(this@FillAddress)
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

    private fun callApidefaultAdd(url: String, userId:String) {
        pdialog.visibility=View.VISIBLE
        val params: MutableMap<String, String> = HashMap()
        params["userId"] = userId
        //Log.d("userId", session.getData(Session.KEY_id))

        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    //println("====res area=>$response")
                    val jsonObject = JSONObject(response)
                    if(jsonObject.has(SUCESS))
                    {
                        if (jsonObject.getInt(Constant.SUCESS) == 200)
                        {
                            //fill address
                            val objectData = jsonObject.getJSONObject("data")
                            if (objectData.length() > 0)
                            {
                                val jsonObject_address = objectData.getJSONObject("address")
                                edthno.setText(jsonObject_address.getString("address1"))
                                edtlandmark.setText(jsonObject_address.getString("address2"))
                                edtpincode.setText(jsonObject_address.getString("pincode"))

                                if (jsonObject_address.getString("address_type") == "1")
                                {
                                    chHome.isChecked = true;
                                    chWork.isChecked = false;
                                    chOther.isChecked = false;
                                    addresstype_id = "1";
                                } else if (jsonObject_address.getString("address_type") == "2") {
                                    chWork.isChecked = true;
                                    chHome.isChecked = false;
                                    chOther.isChecked = false;
                                    addresstype_id = "2";
                                } else if (jsonObject_address.getString("address_type") == "3") {
                                    chOther.isChecked = true;
                                    chHome.isChecked = false;
                                    chWork.isChecked = false;
                                    addresstype_id = "3";
                                }
                            }
                        } else {
                            if(userId.isNotEmpty())
                            {
                                Toast.makeText(mContext, "No Default Address", Toast.LENGTH_SHORT).show()
                            }


                        }
                    }
                    else{
                        if(userId.isNotEmpty()) {
                            Toast.makeText(mContext, "No Default Address", Toast.LENGTH_SHORT).show()
                        }
                    }
                    pdialog.visibility=View.GONE
                    init_state();

                } catch (e: JSONException) {
                    pdialog.visibility=View.GONE
                    e.printStackTrace()
                }
            }
        }, activity, url, params, false)
    }

    private fun init_state()
    {
        val state = State()
        state.state_id = "5fa125aa8f5fa179a5daafde"
        state.state_name = "Rajasthan"

        /*state.state_id = storeinfo.getString("state_id")
        state.state_name = storeinfo.getString("state_name")*/

        arrayListState.add(state)
        Log.d("state", arrayListState.toString())

        stateAdapter = StateAdapter(mContext, arrayListState)
        spin_state.adapter = stateAdapter
        spin_state.isEnabled=false
        spin_state.isClickable=false

        if(arrayListCity.size == 0)
        {
            callApi_city(activity, storeinfo.getString("state_id"))
        }
    }

    private fun init_city() {
        val city = CityName()
        /*city.city_id = "-1"
        city.city_name = "Select City"

        cityid = city.city_id.toString()
        str_city = city.city_name.toString()*/

        cityid = "5fa125c68f5fa179a5daafdf"
        str_city = "Jodhpur"

        city.city_id=cityid
        city.city_name=str_city



        arrayListCity.add(city)

        cityAdapter = CityAdapter(mContext, arrayListCity)
        spin_city.adapter = cityAdapter


        last_city.visibility=View.VISIBLE
        last_city.setText(session.getData(CITY_N))

        spin_city.isClickable=false
        spin_city.isEnabled=false





    }

    private fun init_area() {
        val area = Area()
        area.area_id = "-1"
        area.area_name = "Select Area"

        areaid = area.area_id.toString()
        str_area = area.area_name.toString()

        arrayListArea.add(area)
        areaAdapter = AreaAdapter(mContext, arrayListArea)
        spin_area.adapter = areaAdapter

        last_area.visibility=View.VISIBLE
        last_area.setText(session.getData(AREA_N))


    }

    /*private fun init_subarea()
    {
        val subArea = SubArea()
        subArea.subarea_id = "-1"
        subArea.subarea_name = "Select Sub Area"

        subareaid = subArea.subarea_id.toString()
        str_subarea = subArea.subarea_name.toString()


        arrayListSubArea.add(subArea)
        subareaAdapter = SubAreaAdapter(mContext, arrayListSubArea)
        spin_area_sub.adapter = subareaAdapter


        last_subarea.visibility=View.VISIBLE
        last_subarea.setText(session.getData(SUBAREA_N))
    }*/


    private fun callApi_state(activity: Activity, country_id: String) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")

                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val state = State()
                            if(session.getData(Constant.STATE_ID) == jsonObject.getString("_id"))
                            {
                                //do not add
                            }
                            else{
                                state.state_id = jsonObject.getString("_id")
                                state.state_name = jsonObject.getString("title")
                                arrayListState.add(state)
                            }
                        }
                        stateAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_STATE + country_id, params, true)
    }


    private fun callApi_city(activity: Activity, state_id: String)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {

                        val jsonArray = jsonObject.optJSONArray("data")

                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val city = CityName()

                            city.city_id = jsonObject.getString("_id")
                            city.city_name = jsonObject.getString("title")
                            arrayListCity.add(city)

                            /*if(session.getData(CITY_ID) == jsonObject.getString("_id"))
                            {
                                //do not add
                            }
                            else{
                                city.city_id = jsonObject.getString("_id")
                                city.city_name = jsonObject.getString("title")
                                arrayListCity.add(city)
                            }*/
                        }
                        cityAdapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_CITY + state_id, params, true)
    }


    private fun callApi_area(activity: Activity, cityId: String)
    {
        pdialog.visibility=View.VISIBLE
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)

                    if (jsonObject.getInt(SUCESS) == 200)
                    {

                        val jsonArray = jsonObject.optJSONArray("data")
                        arrayListArea.clear()
                        val area = Area()
                        area.area_id = "-1"
                        area.area_name = "Select Area"
                        arrayListArea.add(area)

                        arrayListSubArea.clear()
                        val subArea = SubArea()
                        subArea.subarea_id = "-1"
                        subArea.subarea_name = "Select Sub Area"
                        arrayListSubArea.add(subArea)

                        areaAdapter?.notifyDataSetChanged()
                        subareaAdapter?.notifyDataSetChanged()
                        spin_area.setSelection(0)
                        spin_area_sub.setSelection(0)

                        areaid="-1"
                        subareaid="-1"





                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val area = Area()
                            area.area_id = jsonObject.getString("_id")
                            area.area_name = jsonObject.getString("title")
                            arrayListArea.add(area)

                        }

                        pdialog.visibility=View.GONE
                        areaAdapter?.notifyDataSetChanged()



                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + GET_AREA + cityId, params, true)
    }


    /* private fun callApi_subarea(activity: Activity, areaId: String, isspinclick:Boolean)
     {
         pdialog.visibility=View.VISIBLE
         val params: MutableMap<String, String> = HashMap()
         ApiConfig.RequestToVolley_GET({ result, response ->
             if (result) {
                 try {
                     println("===n response $response")
                     val jsonObject = JSONObject(response)
                     if (jsonObject.getInt(SUCESS) == 200)
                     {
                         arrayListSubArea.clear()
                         val subArea = SubArea()
                         subArea.subarea_id = "-1"
                         subArea.subarea_name = "Select Sub Area"
                         arrayListSubArea.add(subArea)

                         subareaAdapter?.notifyDataSetChanged()
                         spin_area_sub.setSelection(0)

                         val jsonArray = jsonObject.optJSONArray("data")
                         for (i in 0 until jsonArray.length())
                         {
                             val jsonObject = jsonArray.getJSONObject(i)
                             val subarea = SubArea()
                             subarea.subarea_id = jsonObject.getString("_id")
                             subarea.subarea_name = jsonObject.getString("title")
                             arrayListSubArea.add(subarea)
                         }

                         pdialog.visibility=View.GONE
                         subareaAdapter?.notifyDataSetChanged()
                     }

                     else {
                         Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                 .show()
                     }
                 } catch (e: java.lang.Exception) {
                     pdialog.visibility=View.GONE
                     e.printStackTrace()
                 }
             }
         }, activity, BASEPATH + GET_SUBAREA + areaId, params, true)
     }*/


    private fun showAlertView(pos: Int)
    {
        val alertDialog = AlertDialog.Builder(mContext)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.msg_view_6, null)
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        val dialog = alertDialog.create()
        Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvyes: TextView
        val tvclose: TextView
        tvyes = dialogView.findViewById(R.id.tvyes)
        tvclose = dialogView.findViewById(R.id.tvclose)

        tvyes.setOnClickListener {
            dialog.dismiss()
            //delete your cart and call change area api
            databaseHelper?.DeleteAllOrderData()
            isbackto_home=true

            //fill data by api
            /*val area: Area = arrayListArea[pos]
            Log.d("AREAID", ""+area.area_id)

            session.setData(AREA_ID,area.area_id)
            session.setData(AREA_N, area.area_name)

            subareaAdapter?.notifyDataSetChanged()

            spin_area_sub.isEnabled = true
            spin_area_sub.isClickable=true

            callApi_subarea(activity, areaid,true)*/

        }
        tvclose.setOnClickListener {
            dialog.dismiss()
            /*arrayListArea.clear()
            val area = Area()
            area.area_id = session.getData(AREA_ID)
            area.area_name = session.getData(AREA_N)
            arrayListArea.add(area)
            areaAdapter = AreaAdapter(mContext, arrayListArea)
            spin_area.adapter = areaAdapter

            spin_area_sub.isEnabled = false;
            spin_area_sub.isClickable=false


            callApi_area(activity, cityid)*/


            //onBackPressed();
        }
        dialog.show()
    }


    override fun onBackPressed()
    {
        super.onBackPressed()
        if(isbackto_home)
        {
            val mainIntent = Intent(mContext, MainActivity::class.java)
            //mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            mainIntent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent);
            finish()
        }
        else{
            val mainIntent = Intent(mContext, SetDefaultAddress_2::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            mainIntent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mainIntent);
            finish()
        }
    }

}
