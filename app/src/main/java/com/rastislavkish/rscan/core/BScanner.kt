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

import android.os.Handler
import android.os.Looper

import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.ScanMode
import com.budiyev.android.codescanner.DecodeCallback

import com.google.zxing.BarcodeFormat
import com.google.zxing.Result

class BScanner(codeScanner: CodeScanner) {

    var flashlight: Boolean=false
    set(value)
        {
        codeScanner.setFlashEnabled(value)
        field=value
        }

    private val barcodeDetectedListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    private val codeScanner=codeScanner
    private val mainHandler=Handler(Looper.getMainLooper())

    init {
        codeScanner.autoFocusMode=AutoFocusMode.CONTINUOUS
        codeScanner.scanMode=ScanMode.CONTINUOUS
        codeScanner.decodeCallback=DecodeCallback(this::barcodeDetected)
        codeScanner.startPreview()
        }

    fun addBarcodeDetectedListener(f: (BarcodeInfo) -> Unit)
        {
        barcodeDetectedListeners.add(f)
        }

    private fun barcodeDetected(result: Result)
        {
        val barcode=when (result.barcodeFormat) {
            BarcodeFormat.EAN_13 -> BarcodeInfo(BarcodeInfo.TYPE_EAN_13, result.text)
            BarcodeFormat.EAN_8 -> BarcodeInfo(BarcodeInfo.TYPE_EAN_8, result.text)
            BarcodeFormat.UPC_A -> BarcodeInfo(BarcodeInfo.TYPE_UPC_A, result.text)
            BarcodeFormat.UPC_E -> BarcodeInfo(BarcodeInfo.TYPE_UPC_E, result.text)
            else -> return
            }

        mainHandler.post({
            for (f in barcodeDetectedListeners) {
                f(barcode)
                }
            })
        }

    }
