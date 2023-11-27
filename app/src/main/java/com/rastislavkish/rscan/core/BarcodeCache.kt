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

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class BarcodeCache(preferences: SharedPreferences) {

    private var cache=mutableListOf<BarcodeInfo>()
    private val preferences=preferences

    fun load()
        {
        val serializedCache=preferences.getString("Cache", null)

        if (serializedCache!=null) {
            if (!isJson(serializedCache)) {
                //The cache is stored in CSV format, used by the first versions of RScan

                val localCache=mutableListOf<BarcodeInfo>()

                for (line in serializedCache.split("\n")) {
                    val barcode=BarcodeInfo.fromCsv(line)

                    if (barcode!=null) {
                        localCache.add(barcode)
                        }
                    }

                cache=localCache
                save()

                return
                }

            cache=Json.decodeFromString<MutableList<BarcodeInfo>>(serializedCache)
            }
        }
    fun save()
        {
        val serializedCache=Json.encodeToString(cache)

        preferences.edit()
        .putString("Cache", serializedCache)
        .apply()
        }

    fun importCache(serializedCache: String?): Boolean {
        if (serializedCache!=null) {
            if (!isJson(serializedCache)) {
                //The cache is most likely stored in CSV format, used by the first versions of RScan

                val localCache=mutableListOf<BarcodeInfo>()

                for (line in serializedCache.split("\n")) {
                    val barcode=BarcodeInfo.fromCsv(line)

                    if (barcode!=null) {
                        localCache.add(barcode)
                        }
                    }

                if (localCache.size>1) {
                    cache=localCache
                    save()
                    return true
                    }

                return false
                }

            val localCache: MutableList<BarcodeInfo>

            try {
                localCache=Json.decodeFromString<MutableList<BarcodeInfo>>(serializedCache)
                }
            catch (e: Exception) {
                return false
                }

            if (localCache.size>1) {
                cache=localCache
                save()
                return true
                }

            return false
            }

        return false
        }
    fun exportCache(): String {
        return Json.encodeToString(cache)
        }

    fun addBarcode(barcode: BarcodeInfo)
        {
        if (barcode in cache)
        cache[cache.indexOf(barcode)]=barcode
        else
        cache.add(barcode)
        }

    fun getBarcode(barcode: BarcodeInfo): BarcodeInfo?
        {
        if (barcode in cache) {
            return cache[cache.indexOf(barcode)]
            }

        return null
        }

    private fun isJson(data: String): Boolean {
        //a very simple method to distinguish Json from CSV. It's purpose is not real json verification

        return ((data.startsWith("{") && data.endsWith("}")) || (data.startsWith("[") && data.endsWith("]")))
        }
    }
