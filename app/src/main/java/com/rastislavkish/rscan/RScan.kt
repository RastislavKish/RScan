package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity

class RScan(activity: AppCompatActivity) {

    var scanningResult: BarcodeInfo=BarcodeInfo(BarcodeInfo.TYPE_EAN_13, "")
    private set

    var scanningMode: Int=BarcodeInfo.TYPE_EAN_13

    private val bScanner=BScanner(activity)
    private val newScanningResultListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    private val barcodesCache=mutableListOf<BarcodeInfo>()

    init {
        bScanner.addBarcodeDetectedListener(this::barcodeDetected)
        }

    fun addNewScanningResultListener(f: (BarcodeInfo) -> Unit)
        {
        newScanningResultListeners.add(f)
        }

    fun deinitialize()
        {
        bScanner.deinitialize()
        }

    private fun barcodeDetected(barcode: BarcodeInfo)
        {
        //First of all, make sure the detected barcode is new

        if (barcode==scanningResult) return

        //Check if we know the barcode

        if (barcode in barcodesCache) {
            scanningResult=barcodesCache[barcodesCache.indexOf(barcode)]
            raiseNewScanningResultEvent(scanningResult)
            return
            }

        //If not, check the scanning mode, whether we should accept it

        if ((scanningMode and barcode.type)==barcode.type) {
            scanningResult=barcode
            raiseNewScanningResultEvent(barcode)
            }
        }
    private fun raiseNewScanningResultEvent(barcode: BarcodeInfo)
        {
        for (f in newScanningResultListeners) {
            f(barcode)
            }
        }

    }
