package com.develop.zuzik.location.googleplayservice.availability

import android.content.Context
import com.develop.zuzik.location.core.availability.Availability
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * User: zuzik
 * Date: 1/6/17
 */
class GooglePlayServicesAvailability(private val context: Context) : Availability {

    override fun available() = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;

}