package com.rastislavkish.rscan.core

import android.content.SharedPreferences

class Settings(preferences: SharedPreferences) {

    var useFlashlight=false

    private val preferences=preferences

    fun load()
        {
        useFlashlight=preferences.getBoolean("useFlashlight", false)
        }
    fun save()
        {
        preferences.edit()
        .putBoolean("useFlashlight", useFlashlight)
        .commit()
        }
    }
