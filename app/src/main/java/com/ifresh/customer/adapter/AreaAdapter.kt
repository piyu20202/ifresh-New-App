package com.ifresh.customer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ifresh.customer.R
import com.ifresh.customer.model.Area
import com.ifresh.customer.helper.TextDrawable
import java.util.ArrayList

class AreaAdapter(var mContext: Context, var areaArrList: ArrayList<Area>) : BaseAdapter()
{
    private val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return areaArrList.size
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val view: View
        val holder: ViewHolder
        if (convertView == null)
        {
            view = inflater.inflate(R.layout.simple_spinner_item, parent, false)
            holder = ViewHolder(view)
            view?.tag = holder
        }
        else {
            view = convertView
            holder = view.tag as ViewHolder
        }


        holder.areaname.text = areaArrList[position].area_name
        holder.areaname.tag = areaArrList[position].area_id

        val d = TextDrawable(mContext)


       try {
            // some code
            d.text = areaArrList[position].area_name?.substring(0, 1)
            Log.d("val1",""+areaArrList[position].area_name?.substring(0, 1))
        }
        catch (ex: Exception) {
            // handler
            ex.printStackTrace()

        }

        d.textAlign = Layout.Alignment.ALIGN_CENTER
        d.colorFilter = PorterDuffColorFilter(R.color.colorPrimary, PorterDuff.Mode.SRC_IN)
        holder.base.setImageDrawable(d)
        return view
    }

    private class ViewHolder(row: View?) {
        val areaname: TextView = row?.findViewById(R.id.text_itemlist_spinner) as TextView
        val base: ImageView = row?.findViewById(R.id.imageView4) as ImageView
    }




}