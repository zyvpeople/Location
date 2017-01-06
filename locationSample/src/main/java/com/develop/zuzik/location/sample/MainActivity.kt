package com.develop.zuzik.location.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.develop.zuzik.location.manager.location_provider.LocationManagerLocationProvider
import com.develop.zuzik.locationmanager.R
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val locationManagerLocationProvider = LocationManagerLocationProvider(this, 10000, 10f)
        val googlePlayServiceLocationProvider = FakeLocationProvider()

        val locationProviderFactory = {
            if (locationProviderSwitch.isChecked) {
                googlePlayServiceLocationProvider
            } else {
                locationManagerLocationProvider
            }
        }

        RxView
                .clicks(getLocation)
                .subscribe {
                    locationProviderFactory()
                            .asObservable()
                            .first()
                            .doOnError { Log.i("MainActivity", it.toString()) }
                            .map { it.toString() }
                            .onErrorReturn { it.javaClass.simpleName }
                            .subscribe {
                                location.text = it
                            }
                }
    }
}