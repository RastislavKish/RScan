package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity

class RScan(activity: AppCompatActivity) {

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

        if (barcode in barcodesCache) {
            val scanningResult=barcodesCache[barcodesCache.indexOf(barcode)]
            raiseNewScanningResultEvent(scanningResult)
            return
            }

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
