package com.rastislavkish.rscan

import android.content.Intent
import android.os.Bundle
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

        barcodeScannerBeep.load("BarcodeScannerBeep.opus", this)

        permissionsRequester=PermissionsRequester(this)
        if (!permissionsRequester.permissionsGranted) {
            permissionsRequester.requestPermissions(this)
            }

        barcodeIdentificationActivityLauncher=registerForActivityResult(StartActivityForResult(), this::barcodeIdentificationActivityResult)

        speech=Speech(this)

        rScan=RScan(this)
        rScan.addNewScanningResultListener(this::newScanningResult)

        scanningResultsAdapter.addScanningResultSelectedListener(this::scanningResultSelected)

        //Load the interface

        flashlightToggleButton=findViewById(R.id.flashlightToggleButton)
        flashlightToggleButton.setChecked(true)

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
        rScan.setFlashlightState(flashlightToggleButton.isChecked())
        }

    private fun startBarcodeIdentificationActivity(barcode: BarcodeInfo)
        {
        val intent=Intent(this, BarcodeIdentificationActivity::class.java)
        intent.putExtra("barcode", barcode.csv())
        barcodeIdentificationActivityLauncher.launch(intent)
        }
    }
