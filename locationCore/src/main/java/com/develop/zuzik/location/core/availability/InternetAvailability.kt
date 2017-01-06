package com.develop.zuzik.location.core.availability

import android.content.Context
import android.net.ConnectivityManager

/**
 * User: zuzik
 * Date: 1/6/17
 */
class InternetAvailability(private val context: Context) : Availability {

    override fun available() = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
            ?.activeNetworkInfo
            ?.isConnected
            ?: false
}