package com.ifresh.customerr.kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customerr.R
import com.ifresh.customerr.activity.CartActivity_2
import com.ifresh.customerr.activity.MainActivity
import com.ifresh.customerr.adapter.*
import com.ifresh.customerr.helper.ApiConfig
import com.ifresh.customerr.helper.Constant
import com.ifresh.customerr.helper.Constant.*
import com.ifresh.customerr.helper.GPSTracker
import com.ifresh.customerr.helper.Session
import com.ifresh.customerr.model.*
import kotlinx.android.synthetic.main.activity_location_selection.*
import org.json.JSONObject
import java.util.*

class LocationSelection_K : AppCompatActivity()
{
    private var activity = this
    private val  mContext: Context = this@LocationSelection_K
    //private lateinit var storePrefrence: StorePrefrence
    private lateinit var session: Session
    private lateinit var  gps: GPSTracker

    private val arrayListCity = arrayListOf<CityName>()
    private val arrayListArea = arrayListOf<Area>()
    private val arrayListSubArea = arrayListOf<SubArea>()
    private val arrayListCountry = arrayListOf<Country>()
    private val arrayListState = arrayListOf<State>()

    private var areaAdapter: AreaAdapter?=null
    private var subareaAdapter:SubAreaAdapter?=null
    private var cityAdapter: CityAdapter?=null
    private var countryAdapter: CountryAdapter?=null
    private var stateAdapter: StateAdapter?=null
    private var countryid:String=""
    private var stateid:String=""
    private var cityid:String=""
    private var areaid:String=""
    private var subareaid:String=""
    private var str_country=""
    private var str_state=""
    private var str_city=""
    private var str_area=""
    private var str_subarea=""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_selection)
        //storePrefrence = StorePrefrence(mContext)
        session = Session(mContext)

        activity = this@LocationSelection_K
        gps = GPSTracker(this@LocationSelection_K)

        ApiConfig.displayLocationSettingsRequest(this@LocationSelection_K)
        ApiConfig.getLocation(this@LocationSelection_K)

        val saveLatitude = session.getCoordinates(Session.KEY_LATITUDE).toDouble()
        val saveLongitude = session.getCoordinates(Session.KEY_LONGITUDE).toDouble()

        if(saveLatitude.equals(0.0) || saveLongitude.equals(0.0) )
        {
            SaveLocation(gps.latitude.toString(), gps.longitude.toString())
        }
        else{
            SaveLocation(session.getCoordinates(Session.KEY_LATITUDE), session.getCoordinates(Session.KEY_LONGITUDE))
        }

        init_country()

        btnsubmit.setOnClickListener(View.OnClickListener {
            when {
                countryid.isEmpty() -> {
                    Toast.makeText(mContext, getString(R.string.selectcountry), Toast.LENGTH_SHORT).show()
                }
                stateid.isEmpty() -> {
                    Toast.makeText(mContext, getString(R.string.selectstate), Toast.LENGTH_SHORT).show()
                }
                cityid.isEmpty() -> {
                    Toast.makeText(mContext, getString(R.string.selectcitym), Toast.LENGTH_SHORT).show()
                }
                areaid.isEmpty() -> {
                    Toast.makeText(mContext, getString(R.string.selectaream), Toast.LENGTH_SHORT).show()
                }
                subareaid.isEmpty() -> {
                    Toast.makeText(mContext, getString(R.string.selectaream), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    session.setData(COUNTRY_ID,countryid)
                    session.setData(CITY_N,str_city)

                    session.setData(STATE_ID,stateid)
                    session.setData(STATE_N,str_state)

                    session.setData(CITY_ID,cityid)
                    session.setData(CITY_N,str_city)

                    session.setData(AREA_ID,areaid)
                    session.setData(AREA_N,str_area)


                    session.setData(SUBAREA_ID,subareaid)
                    session.setData(SUBAREA_N,str_subarea)



                    if(session.isUserLoggedIn)
                    {
                        //user already login
                        val mainIntent = Intent(mContext, MainActivity::class.java)
                        startActivity(mainIntent);
                        finish()
                    }
                    else{
                        // user is type of guest
                        callGuestUserApi();
                    }

                }
            }
        })

        // spinner country
        spin_country.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val country: Country = arrayListCountry[pos]
                    Log.d("id==>", "" + country.country_id)

                    //session.setData(Constant.COUNTRY_N, country.country_name.toString())
                    str_country=country.country_name.toString()
                    countryid = country.country_id.toString()
                    callApi_state(activity, country.country_id.toString())

                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                countryid=""
                str_country=""
            }
        }

        // spinner state
        spin_state.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val state: State = arrayListState[pos]
                    Log.d("id==>", "" + state.state_id)

                    //storePrefrence.setString("state_name", state.state_name)
                    str_state =  state.state_name.toString()
                    stateid=state.state_id.toString()

                    //session.setData(STATE_N,state.state_name.toString())
                    callApi_city(activity, stateid)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                stateid=""
                str_state=""
            }
        }

        //spinner city
        spin_city.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val city: CityName = arrayListCity[pos]
                    Log.d("id==>", "" + city.city_id)

                    cityid = city.city_id.toString()
                    str_city = city.city_name.toString()

                    //storePrefrence.setString("city", )
                    //Log.d("citname", city.city_name.toString())
                    //session.setData(CITY_N, city.city_name.toString())
                    //Log.d("cityname",""+session.getData(Constant.CITY_N))

                    callApi_area(activity, cityid)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                cityid=""
                str_city=""
            }

        }

        //spinner area
        spin_area.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val area: Area = arrayListArea[pos]
                    //Log.d("id==>", "" + area.area_id)
                    areaid = area.area_id.toString()
                    str_area =  area.area_name.toString()
                    //Log.d("area_id", storePrefrence.getString("area_id"))
                    //storePrefrence.setString("area", area.area_name)
                    //session.setData(AREA_N, area.area_name.toString())
                    //Log.d("AREA=>", session.getData(AREA_N))
                    callApi_subarea(activity, areaid)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                areaid=""
                str_area=""
            }
        }

        spin_area_sub.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val subArea: SubArea = arrayListSubArea[pos]
                    Log.d("id==>", "" + subArea.subarea_id)
                    subareaid = subArea.subarea_id.toString()
                    str_subarea = subArea.subarea_name.toString()

                    //session.setData(SUBAREA_ID, subareaid)
                    //session.setData(SUBAREA_N, subArea.subarea_name)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                subareaid=""
                str_subarea=""
            }

        }


    }

    private fun callGuestUserApi()
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_POST_GUEST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    val data_jsonobj = jsonObject.getJSONObject("data");

                    session.setData(AUTHTOKEN, data_jsonobj.getString("authtoken"))
                    session.setData("role", data_jsonobj.getJSONObject("user").getString("role_type"))

                    val mainIntent = Intent(mContext, MainActivity::class.java)
                    startActivity(mainIntent);
                    finish()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + GUEST, params, false)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init_country() {
        val country = Country()
        //country.country_id = "-1"
        //country.country_name = "Select Country"

        country.country_id = "5f587a916a254867fcd29515"
        country.country_name = "India"

        countryid = country.country_id.toString()
        str_country = country.country_name.toString()

        arrayListCountry.add(country)
        countryAdapter = CountryAdapter(mContext, arrayListCountry)
        spin_country.adapter = countryAdapter

        session.setData(COUNTRY_N, country.country_name.toString())

        spin_country.isEnabled=false
        spin_country.isClickable=false

        init_state()


        //callApi_country(activity)
    }

    private fun init_state() {
        val state = State()
        //state.state_id = "-1"
        //state.state_name = "Select State"

        state.state_id = "5f587d443eb6fb4cbf561e2f"
        state.state_name = "Rajasthan"

        stateid = state.state_id.toString()
        str_state = state.state_name.toString()

        arrayListState.add(state)
        stateAdapter = StateAdapter(mContext, arrayListState)
        spin_state.adapter = stateAdapter

        session.setData(STATE_N,state.state_name.toString())

        spin_state.isEnabled=false
        spin_state.isClickable=false

        init_city()
        init_area()
        init_subarea()

    }

    private fun init_city() {
        val city = CityName()
        //city.city_id = "-1"
        //city.city_name = "Select City"

        city.city_id = "5f561df2f20f7e484332259b"
        city.city_name = "Jodhpur"

        cityid = city.city_id.toString()
        str_city = city.city_name.toString()

        arrayListCity.add(city)
        cityAdapter = CityAdapter(mContext, arrayListCity)
        spin_city.adapter = cityAdapter

        session.setData(CITY_N,"Jodhpur")

        spin_city.isEnabled=false
        spin_city.isClickable=false

        callApi_area(activity, cityid)

    }

    private fun init_area() {
        val area = Area()
        area.area_id = "-1"
        area.area_name = "Select Area"
        arrayListArea.add(area)
        areaAdapter = AreaAdapter(mContext, arrayListArea)
        spin_area.adapter = areaAdapter
    }

    private fun init_subarea()
    {
        val subArea=SubArea()
        subArea.subarea_id="-1"
        subArea.subarea_name="Select Sub Area"
        arrayListSubArea.add(subArea)
        subareaAdapter = SubAreaAdapter(mContext, arrayListSubArea)
        spin_area_sub.adapter=subareaAdapter

    }


    private fun callApi_area(activity: Activity, cityId: String)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val area = Area()
                            area.area_id = jsonObject.getString("_id")
                            area.area_name = jsonObject.getString("title")
                            arrayListArea.add(area)
                        }
                        areaAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }


                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_AREA + cityId, params, true)
    }

    private fun callApi_subarea(activity: Activity, areaId: String)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val subarea = SubArea()
                            subarea.subarea_id = jsonObject.getString("_id")
                            subarea.subarea_name = jsonObject.getString("title")
                            arrayListSubArea.add(subarea)
                        }
                        subareaAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_SUBAREA + areaId, params, true)
    }


    private fun callApi_country(activity: Activity)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val country = Country()
                            country.country_id = jsonObject.getString("_id")
                            country.country_name = jsonObject.getString("title")
                            arrayListCountry.add(country)
                        }
                        countryAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_COUNTRY, params, true)
    }

    private fun callApi_state(activity: Activity, country_id: String)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val state = State()
                            state.state_id = jsonObject.getString("_id")
                            state.state_name = jsonObject.getString("title")
                            arrayListState.add(state)
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
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val city = CityName()
                            city.city_id = jsonObject.getString("_id")
                            city.city_name = jsonObject.getString("title")
                            arrayListCity.add(city)
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

    fun SaveLocation(latitude: String?, longitude: String?) {
        Log.d("lat", "" + latitude)
        Log.d("long", "" + longitude)
        session.setData(Session.KEY_LATITUDE, latitude.toString())
        session.setData(Session.KEY_LONGITUDE, longitude.toString())
    }

}





