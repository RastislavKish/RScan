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
