/*
* Copyright (C) 2021 Rastislav Kish
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, version 3.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package com.rastislavkish.rscan.core

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
