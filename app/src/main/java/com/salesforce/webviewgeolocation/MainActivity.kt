package com.salesforce.webviewgeolocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {


    private val PERMISSIONS_REQUEST_CODE_LOCATION = 60
    private var mCallback: GeolocationPermissions.Callback? = null
    private var mOrigin: String? = null


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = layoutInflater.inflate(R.layout.activity_main, null)
        val webView: WebView = rootView.findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.setGeolocationEnabled(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                mCallback = callback
                mOrigin = origin
                if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                ) {
                    // Allow Javascript Geolocation API to access location
                    // Permission: ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
                    callback.invoke(origin, true, false)
                } else {
                    val permissionRequests = arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    requestPermissions(permissionRequests, PERMISSIONS_REQUEST_CODE_LOCATION)
                }
            }
        }
        //webView.loadUrl("https://storage.googleapis.com/webviewgeolcation/index.html")
        webView.loadUrl("file:///android_asset/www/index.html")
        setContentView(rootView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE_LOCATION) {
            // Checks to ensure that these permissions were granted
            if (permissions.size == permissions.size && grantResults.size == permissions.size && Manifest.permission.ACCESS_COARSE_LOCATION ==
                permissions[0] && Manifest.permission.ACCESS_FINE_LOCATION ==
                permissions[1] && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Allow Javascript Geolocation API to access location
                mCallback?.invoke(mOrigin, true, false)
            } else {
                // Denies Javascript Geolocation API location permission
                mCallback?.invoke(mOrigin, false, false)
            }

        }
    }

    fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result: Int = checkSelfPermission(permission)
            PackageManager.PERMISSION_GRANTED == result
        } else {
            true
        }
    }
}