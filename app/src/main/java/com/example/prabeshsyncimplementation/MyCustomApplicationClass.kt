package com.example.prabeshsyncimplementation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkRequest

class MyCustomApplicationClass : Application() {
    var networkMonitor = NetworkMonitor()
    override fun onCreate() {
        super.onCreate()
        //IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        //Object connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE);

        //String builder = NetworkRequest.Builder()

        //apps should use the more versatile requestNetwork,
        // registerNetworkCallback or registerDefaultNetworkCallback functions
        // instead for faster and more detailed updates about the network changes they care about.

        var connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;

        var builder = NetworkRequest.Builder()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback (){

        })



    }

    fun registerNetworkCallback(request: NetworkRequest?, networkCallback: NetworkCallback?) {}
}