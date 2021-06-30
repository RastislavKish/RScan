package com.rastislavkish.rscan

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsRequester(context: Context) {

    val cameraPermission: Boolean
    get() = ContextCompat.checkSelfPermission(context, "android.permission.CAMERA")==PackageManager.PERMISSION_GRANTED

    val permissionsGranted: Boolean
    get() = cameraPermission

    private val context=context

    fun requestPermissions(activity: Activity)
        {
        ActivityCompat.requestPermissions(activity, arrayOf("android.permission.CAMERA"), 1)
        }
    }
