package com.develop.zuzik.location.googleplayservice.location_provider

import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

/**
 * User: zuzik
 * Date: 4/26/16
 */
internal class GoogleApiClientConnectionObservable(private val googleAPIClient: GoogleApiClient) : Observable.OnSubscribe<GoogleApiClient> {

    override fun call(subscriber: Subscriber<in GoogleApiClient>) {
        val connectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onNext(googleAPIClient)
                }
                if (!subscriber.isUnsubscribed) {
                    subscriber.onCompleted()
                }
            }

            override fun onConnectionSuspended(i: Int) {
                googleAPIClient.connect()
            }
        }
        val onConnectionFailedListener = GoogleApiClient.OnConnectionFailedListener {
            if (!subscriber.isUnsubscribed) {
                subscriber.onError(GoogleApiClientConnectionException())
            }
        }

        googleAPIClient.registerConnectionCallbacks(connectionCallbacks)
        googleAPIClient.registerConnectionFailedListener(onConnectionFailedListener)

        subscriber
                .add(Subscriptions
                        .create {
                            googleAPIClient.unregisterConnectionCallbacks(connectionCallbacks)
                            googleAPIClient.unregisterConnectionFailedListener(onConnectionFailedListener)
                        })

        googleAPIClient.connect()
    }
}
