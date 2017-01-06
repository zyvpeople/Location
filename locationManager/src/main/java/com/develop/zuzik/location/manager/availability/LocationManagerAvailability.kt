package com.develop.zuzik.location.manager.availability

import android.content.Context
import com.develop.zuzik.location.core.availability.Availability

/**
 * User: zuzik
 * Date: 1/6/17
 */
class LocationManagerAvailability(private val context: Context) : Availability {

    override fun available() = this.context.getSystemService(Context.LOCATION_SERVICE) != null
}