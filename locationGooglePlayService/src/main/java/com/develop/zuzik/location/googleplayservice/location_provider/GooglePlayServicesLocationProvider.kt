package com.develop.zuzik.location.googleplayservice.location_provider

import android.content.Context
import android.location.Location
import com.develop.zuzik.location.core.availability.GpsAvailability
import com.develop.zuzik.location.core.availability.InternetAvailability
import com.develop.zuzik.location.core.exception.GetLocationException
import com.develop.zuzik.location.core.exception.GpsUnavailableException
import com.develop.zuzik.location.core.exception.InternetUnavailableException
import com.develop.zuzik.location.core.location_provider.LocationProvider
import com.develop.zuzik.location.googleplayservice.availability.GooglePlayServicesAvailability
import com.develop.zuzik.location.googleplayservice.exception.GooglePlayServicesUnavailableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import rx.Observable

/**
 * User: zuzik
 * Date: 4/26/16
 */
//TODO: it is incorrect to check internet and gps at the same time
class GooglePlayServicesLocationProvider(private val context: Context,
                                         private val locationRequest: LocationRequest) : LocationProvider {

    private val googlePlayServicesAvailability = GooglePlayServicesAvailability(context)
    private val internetAvailability = InternetAvailability(context)
    private val gpsAvailability = GpsAvailability(context)

    override fun asObservable(): Observable<Location> {
        return locationAvailable()
                .flatMap { googleApiClient() }
                .flatMap { connectedGoogleApiClient(it) }
                .flatMap { location(it).doOnUnsubscribe { disconnect(it) } }
    }

    private fun locationAvailable() = Observable.defer<Any> {
        if (!googlePlayServicesAvailability.available()) {
            Observable.error(GooglePlayServicesUnavailableException())
        } else if (!internetAvailability.available()) {
            Observable.error(InternetUnavailableException())
        } else if (!gpsAvailability.available()) {
            Observable.error(GpsUnavailableException())
        } else {
            Observable.just(Any())
        }
    }

    private fun googleApiClient() = Observable.defer {
        Observable.just(GoogleApiClient
                .Builder(context)
                .addApi(LocationServices.API)
                .build())
    }

    private fun connectedGoogleApiClient(it: GoogleApiClient) = Observable
            .create(GoogleApiClientConnectionObservable(it))
            .onErrorResumeNext { Observable.error(GetLocationException()) }

    private fun location(googleApiClient: GoogleApiClient) = Observable
            .create(GoogleApiClientLocationObservable(context,
                    googleApiClient,
                    LocationServices.FusedLocationApi,
                    locationRequest))

    private fun disconnect(googleApiClient: GoogleApiClient) {
        if (googleApiClient.isConnected || googleApiClient.isConnecting) {
            googleApiClient.disconnect()
        }
    }
}
