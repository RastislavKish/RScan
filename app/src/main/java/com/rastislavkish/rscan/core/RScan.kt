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

import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity

class RScan(activity: AppCompatActivity) {

    private val context=activity

    private val bScanner=BScanner(activity)
    private val newScanningResultListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    private val barcodeCache=BarcodeCache(activity.getSharedPreferences("BarcodeCache", AppCompatActivity.MODE_PRIVATE))

    init {
        barcodeCache.load()

        bScanner.addBarcodeDetectedListener(this::barcodeDetected)
        }

    fun importFromClipboard(): Boolean
        {
        val clipboard=context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)==true) {
            val text=clipboard.primaryClip?.getItemAt(0)?.text

            if (text!=null) {
                return barcodeCache.importCache(text.toString())
                }
            }

        return false
        }
    fun exportToClipboard()
        {
        val clipboard=context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip=ClipData.newPlainText("BarcodeCache", barcodeCache.exportCache())

        clipboard.setPrimaryClip(clip)
        }

    fun cacheBarcode(barcode: BarcodeInfo)
        {
        barcodeCache.addBarcode(barcode)
        barcodeCache.save()
        }

    fun addNewScanningResultListener(f: (BarcodeInfo) -> Unit)
        {
        newScanningResultListeners.add(f)
        }

    fun setFlashlightState(state: Boolean)
        {
        bScanner.flashlight=state
        }

    fun deinitialize()
        {
        bScanner.deinitialize()
        }

    private fun barcodeDetected(barcode: BarcodeInfo)
        {
        //Check if we know the barcode

        val cachedBarcode=barcodeCache.getBarcode(barcode)
        if (cachedBarcode!=null)
        raiseNewScanningResultEvent(cachedBarcode)

        //Else, forward the event

        raiseNewScanningResultEvent(barcode)
        }
    private fun raiseNewScanningResultEvent(barcode: BarcodeInfo)
        {
        for (f in newScanningResultListeners) {
            f(barcode)
            }
        }

    }
