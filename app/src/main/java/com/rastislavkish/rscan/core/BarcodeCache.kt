package com.rastislavkish.rscan.core

import android.content.SharedPreferences

class BarcodeCache(preferences: SharedPreferences) {

    private var cache=mutableListOf<BarcodeInfo>()
    private val preferences=preferences

    fun load()
        {
        val csvCache=preferences.getString("Cache", null)

        if (csvCache!=null) {
            val localCache=mutableListOf<BarcodeInfo>()

            for (line in csvCache.split("\n")) {
                val barcode=BarcodeInfo.fromCsv(line)

                if (barcode!=null) {
                    localCache.add(barcode)
                    }
                }

            cache=localCache
            }
        }
    fun save()
        {
        val csvCache=cache.map({ it.csv() }).joinToString("\n")

        preferences.edit()
        .putString("Cache", csvCache)
        .apply()
        }

    fun importCsv(csv: String?): Boolean
        {
        if (csv!=null) {
            val localCache=mutableListOf<BarcodeInfo>()

            for (line in csv.split("\n")) {
                val barcode=BarcodeInfo.fromCsv(line)

                if (barcode!=null) {
                    localCache.add(barcode)
                    }
                }

            if (localCache.size>=1) {
                cache=localCache
                save()
                return true
                }
            }

        return false
        }
    fun exportCsv(): String
        {
        return cache.map({ it.csv() }).joinToString("\n")
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
    }
