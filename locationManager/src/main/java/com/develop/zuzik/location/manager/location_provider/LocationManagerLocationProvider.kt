package com.develop.zuzik.location.manager.location_provider

import android.content.Context
import android.location.Location
import com.develop.zuzik.location.core.availability.GpsAvailability
import com.develop.zuzik.location.core.availability.InternetAvailability
import com.develop.zuzik.location.core.exception.GpsUnavailableException
import com.develop.zuzik.location.core.exception.InternetUnavailableException
import com.develop.zuzik.location.core.location_provider.LocationProvider
import com.develop.zuzik.location.manager.availability.LocationManagerAvailability
import com.develop.zuzik.location.manager.exception.LocationManagerUnavailableException
import rx.Observable

/**
 * User: zuzik
 * Date: 4/30/16
 */
//TODO: it is incorrect to check internet and gps at the same time
class LocationManagerLocationProvider(private val context: Context,
                                      private val minUpdateIntervalInMilliseconds: Long,
                                      private val minUpdateDistanceInMeters: Float) : LocationProvider {

    private val locationManagerAvailability = LocationManagerAvailability(context)
    private val internetAvailability = InternetAvailability(context)
    private val gpsAvailability = GpsAvailability(context)

    override fun asObservable(): Observable<Location> = Observable
            .defer<Void> {
                if (!locationManagerAvailability.available()) {
                    Observable.error(LocationManagerUnavailableException())
                } else if (!internetAvailability.available()) {
                    Observable.error(InternetUnavailableException())
                } else if (!gpsAvailability.available()) {
                    Observable.error(GpsUnavailableException())
                } else {
                    Observable.just<Void>(null)
                }
            }
            .flatMap({
                val locationNetworkObservable = Observable.create(LocationObservable.createNetworkLocationObservable(context, minUpdateIntervalInMilliseconds, minUpdateDistanceInMeters))
                val locationGpsObservable = Observable.create(LocationObservable.createGpsLocationObservable(context, minUpdateIntervalInMilliseconds, minUpdateDistanceInMeters))
                Observable.merge<Location>(locationNetworkObservable, locationGpsObservable)
            })
}
