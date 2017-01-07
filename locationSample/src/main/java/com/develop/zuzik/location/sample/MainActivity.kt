package com.develop.zuzik.location.sample

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.develop.zuzik.location.core.location_provider.LocationProvider
import com.develop.zuzik.location.googleplayservice.location_provider.GooglePlayServicesLocationProvider
import com.develop.zuzik.location.manager.location_provider.LocationManagerLocationProvider
import com.develop.zuzik.locationmanager.R
import com.google.android.gms.location.LocationRequest
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askLocation()
                .withLatestFrom(locationProvider()) { askLocation, locationProvider -> locationProvider }
                .flatMap {
                    it.asObservable()
                            .compose(toTryMonad())
                            .take(1)
                }
                .map(monadToString())
                .subscribe(RxTextView.text(location))
    }

    private fun createGooglePlayServicesLocationProvider() = GooglePlayServicesLocationProvider(this, createLocationRequest())

    private fun createLocationRequest() =
            LocationRequest()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    private fun createLocationManagerLocationProvider() = LocationManagerLocationProvider(this, 10000, 10f)

    private fun askLocation() = RxView.clicks(getLocation)

    private fun locationProvider(): Observable<LocationProvider>? {
        val googlePlayServiceLocationProvider = createGooglePlayServicesLocationProvider()
        val locationManagerLocationProvider = createLocationManagerLocationProvider()
        return RxCompoundButton
                .checkedChanges(locationProviderSwitch)
                .defaultIfEmpty(false)
                .map { checked ->
                    if (checked) {
                        googlePlayServiceLocationProvider
                    } else {
                        locationManagerLocationProvider
                    }
                }
    }

    private fun toTryMonad(): (Observable<Location>) -> Observable<TryMonad<Location>> = {
        it
                .map { TryMonad.value(it) }
                .onErrorReturn { TryMonad.error(it) }
    }

    private fun monadToString(): (TryMonad<Location>) -> String = {
        if (it.value != null) {
            it.value.toString()
        } else if (it.error != null) {
            it.error.javaClass.simpleName
        } else {
            ""
        }
    }
}