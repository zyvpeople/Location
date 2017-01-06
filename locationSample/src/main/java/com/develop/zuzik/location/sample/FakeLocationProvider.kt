package com.develop.zuzik.location.sample

import android.location.Location
import com.develop.zuzik.location.core.location_provider.LocationProvider
import rx.Observable

/**
 * User: zuzik
 * Date: 1/6/17
 */
class FakeLocationProvider : LocationProvider {

    override fun asObservable(): Observable<Location> = Observable.error(NotImplementedProviderException())
}