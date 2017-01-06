package com.develop.zuzik.location.manager.location_provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.develop.zuzik.location.core.exception.LocationPermissionException
import com.develop.zuzik.location.manager.exception.LocationManagerUnavailableException
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

/**
 * User: zuzik
 * Date: 4/30/16
 */
internal class LocationObservable private constructor(private val context: Context,
                                                      private val minUpdateIntervalInMilliseconds: Long,
                                                      private val minUpdateDistanceInMeters: Float,
                                                      private val provider: String) : Observable.OnSubscribe<Location> {

    companion object Factory {

        fun createNetworkLocationObservable(context: Context,
                                            minUpdateIntervalInMilliseconds: Long,
                                            minUpdateDistanceInMeters: Float): LocationObservable {
            return LocationObservable(context,
                    minUpdateIntervalInMilliseconds,
                    minUpdateDistanceInMeters,
                    LocationManager.NETWORK_PROVIDER)
        }

        fun createGpsLocationObservable(context: Context,
                                        minUpdateIntervalInMilliseconds: Long,
                                        minUpdateDistanceInMeters: Float): LocationObservable {
            return LocationObservable(context,
                    minUpdateIntervalInMilliseconds,
                    minUpdateDistanceInMeters,
                    LocationManager.GPS_PROVIDER)
        }
    }

    override fun call(subscriber: Subscriber<in Location>) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            if (!subscriber.isUnsubscribed) {
                subscriber.onError(LocationManagerUnavailableException())
            }
            return
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!subscriber.isUnsubscribed) {
                        subscriber.onNext(location)
                    }
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}
            }

            locationManager.requestLocationUpdates(
                    provider,
                    minUpdateIntervalInMilliseconds,
                    minUpdateDistanceInMeters,
                    locationListener)

            val lastKnownLocation = locationManager.getLastKnownLocation(provider)
            if (lastKnownLocation != null) {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onNext(lastKnownLocation)
                }
            }

            subscriber
                    .add(Subscriptions
                            .create {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    locationManager.removeUpdates(locationListener)
                                }
                            })
        } else {
            if (!subscriber.isUnsubscribed) {
                subscriber.onError(LocationPermissionException())
            }
        }
    }
}
