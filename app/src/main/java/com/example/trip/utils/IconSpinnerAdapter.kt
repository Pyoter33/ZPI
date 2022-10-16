package com.example.trip.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.trip.R
import com.skydoves.powermenu.MenuBaseAdapter


class IconSpinnerAdapter : MenuBaseAdapter<Int>() {
    override fun getView(index: Int, view: View?, viewGroup: ViewGroup): View {
        var itemView = view
        val context = viewGroup.context
        if (view == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.item_icon, viewGroup, false)
        }
        val item = getItem(index) as Int
        val image = itemView?.findViewById<ImageView>(R.id.image_item_icon)
        image?.setImageResource(item)

        return super.getView(index, itemView, viewGroup)
    }
}