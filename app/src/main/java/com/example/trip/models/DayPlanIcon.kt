package com.example.trip.models

import androidx.annotation.DrawableRes
import com.example.trip.R

enum class DayPlanIcon(val code: Int, @DrawableRes val resId: Int) {
    MONUMENT(0, R.drawable.ic_baseline_church_24),
    WALK(1, R.drawable.ic_baseline_directions_walk_24),
    CITY(2, R.drawable.ic_baseline_location_city_24),
    MOUNTAIN(3, R.drawable.ic_baseline_landscape_24),
    RESTAURANT(4, R.drawable.ic_baseline_restaurant_24),
    CASTLE(5, R.drawable.ic_baseline_castle_24),
    BOAT(6, R.drawable.ic_baseline_sailing_24),
    WATER(7, R.drawable.ic_baseline_water_24),
    SKI(8, R.drawable.ic_baseline_downhill_skiing_24)
}