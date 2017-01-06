package com.develop.zuzik.location.core.location_provider

import android.location.Location

import rx.Observable

/**
 * User: zuzik
 * Date: 4/26/16
 */
interface LocationProvider {
    fun asObservable(): Observable<Location>
}
