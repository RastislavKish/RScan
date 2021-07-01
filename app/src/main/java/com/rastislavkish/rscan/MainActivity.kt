package com.rastislavkish.rscan

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

import android.view.View

import android.widget.Button
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView

import com.rastislavkish.rtk.Sound
import com.rastislavkish.rtk.Speech

class MainActivity : AppCompatActivity() {

    private lateinit var speech: Speech

    private val barcodeScannerBeep=Sound()
    private lateinit var permissionsRequester: PermissionsRequester

    private lateinit var settings: Settings
    private lateinit var rScan: RScan
    private val scanningResultsAdapter=ScanningResultsAdapter()
    private lateinit var barcodeIdentificationActivityLauncher: ActivityResultLauncher<Intent>

    //Components

    private lateinit var scanningResultsRecyclerView: RecyclerView

    private lateinit var flashlightToggleButton: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?)
        {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings=Settings(getSharedPreferences("RScanSettings", MODE_PRIVATE))
        settings.load()

        barcodeScannerBeep.load("BarcodeScannerBeep.opus", this)

        permissionsRequester=PermissionsRequester(this)
        if (!permissionsRequester.permissionsGranted) {
            permissionsRequester.requestPermissions(this)
            }

        barcodeIdentificationActivityLauncher=registerForActivityResult(StartActivityForResult(), this::barcodeIdentificationActivityResult)

        speech=Speech(this)

        rScan=RScan(this)
        rScan.addNewScanningResultListener(this::newScanningResult)
        Handler(Looper.getMainLooper()).postDelayed({
            rScan.setFlashlightState(settings.useFlashlight)
            }, 1000)

        scanningResultsAdapter.addScanningResultSelectedListener(this::scanningResultSelected)

        //Load the interface

        flashlightToggleButton=findViewById(R.id.flashlightToggleButton)
        flashlightToggleButton.setChecked(settings.useFlashlight)

        scanningResultsRecyclerView=findViewById(R.id.scanningResultsRecyclerView)
        scanningResultsRecyclerView.adapter=scanningResultsAdapter
        }
    override fun onDestroy()
        {
        rScan.deinitialize()

        super.onDestroy()
        }

    private fun newScanningResult(scanningResult: BarcodeInfo)
        {
        if (!scanningResultsAdapter.addScanningResult(scanningResult))
        return

        barcodeScannerBeep.play()
        if (scanningResult.known)
        speech.speak(scanningResult.description)
        else
        speech.speak(BarcodeInfo.typeToString(scanningResult.type), false)
        }
    private fun scanningResultSelected(scanningResult: BarcodeInfo)
        {
        startBarcodeIdentificationActivity(scanningResult)
        }
    private fun barcodeIdentificationActivityResult(result: ActivityResult)
        {
        if (result.resultCode==RESULT_OK) {
            val barcode=BarcodeInfo.fromIntent(result.data, "result", "MainActivity")

            if (scanningResultsAdapter.removeScanningResult(barcode))
            rScan.cacheBarcode(barcode)
            }
        }

    //Interface components events

    fun clearListButton_click(view: View)
        {
        scanningResultsAdapter.clear()
        }
    fun flashlightToggleButton_click(view: View)
        {
        settings.useFlashlight=flashlightToggleButton.isChecked()
        settings.save()
        rScan.setFlashlightState(settings.useFlashlight)
        }

    private fun startBarcodeIdentificationActivity(barcode: BarcodeInfo)
        {
        val intent=Intent(this, BarcodeIdentificationActivity::class.java)
        intent.putExtra("barcode", barcode.csv())
        barcodeIdentificationActivityLauncher.launch(intent)
        }
    }
