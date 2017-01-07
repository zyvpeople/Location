package com.develop.zuzik.location.googleplayservice.location_provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import com.develop.zuzik.location.core.exception.LocationPermissionException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

/**
 * User: zuzik
 * Date: 4/27/16
 */
internal class GoogleApiClientLocationObservable(private val context: Context,
                                                 private val googleAPIClient: GoogleApiClient,
                                                 private val locationProviderAPI: FusedLocationProviderApi,
                                                 private val locationRequest: LocationRequest) : Observable.OnSubscribe<Location> {

    override fun call(subscriber: Subscriber<in Location>) {
        val locationListener = LocationListener { location ->
            if (!subscriber.isUnsubscribed) {
                subscriber.onNext(location)
            }
        }
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!subscriber.isUnsubscribed) {
                subscriber.onError(LocationPermissionException())
            }
        } else {
            subscriber.add(Subscriptions
                    .create { locationProviderAPI.removeLocationUpdates(googleAPIClient, locationListener) })

            locationProviderAPI
                    .requestLocationUpdates(
                            googleAPIClient,
                            locationRequest,
                            locationListener)
        }
    }
}