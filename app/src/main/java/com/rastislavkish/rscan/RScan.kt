package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity

class RScan(activity: AppCompatActivity) {

    private val bScanner=BScanner(activity)
    private val newScanningResultListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    private val barcodeCache=BarcodeCache(activity.getSharedPreferences("BarcodeCache", AppCompatActivity.MODE_PRIVATE))

    init {
        barcodeCache.load()

        bScanner.addBarcodeDetectedListener(this::barcodeDetected)
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
