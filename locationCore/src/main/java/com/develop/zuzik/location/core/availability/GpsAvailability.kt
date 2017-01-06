package com.develop.zuzik.location.core.availability

import android.content.Context
import android.location.LocationManager

/**
 * User: zuzik
 * Date: 1/6/17
 */
class GpsAvailability(private val context: Context) : Availability {

    override fun available() = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
            ?.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ?: false
}