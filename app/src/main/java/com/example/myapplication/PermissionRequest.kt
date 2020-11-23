package com.example.myapplication

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat

class PermissionRequest {

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    fun checkNetworkConnected(context: Context) {
        if (!isNetworkConnected(context)) {
            showToastNoConnection(context)
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }

    private fun showToastNoConnection(context: Context) {
        Toast.makeText(context.applicationContext, "No Internet Connection", Toast.LENGTH_SHORT)
            .show()
    }
}