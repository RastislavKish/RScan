package com.rastislavkish.rscan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

import android.view.View

import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.rastislavkish.rtk.Sound
import com.rastislavkish.rtk.Speech

class ScanningMode(name: String, code: Int) {
    //A helper class for linking scanner modes with their text representations

    val name=name
    val code=code

    companion object {

        val scanningModes=listOf(
            ScanningMode("Discovery", -1), //Will match everything
            ScanningMode("EAN-13", BarcodeInfo.TYPE_EAN_13),
            ScanningMode("EAN-8", BarcodeInfo.TYPE_EAN_8),
            ScanningMode("UPC-A", BarcodeInfo.TYPE_UPC_A),
            ScanningMode("UPC-E", BarcodeInfo.TYPE_UPC_E),
            )
        }
    }
class MainActivity : AppCompatActivity() {

    private lateinit var speech: Speech

    private val barcodeScannerBeep=Sound()
    private var selectedScanningModeIndex=1
    private val selectedScanningMode get() = ScanningMode.scanningModes[selectedScanningModeIndex]
    private lateinit var permissionsRequester: PermissionsRequester

    private lateinit var rScan: RScan
    private val scanningResultsAdapter=ScanningResultsAdapter()
    private lateinit var barcodeIdentificationActivityLauncher: ActivityResultLauncher<Intent>

    //Components

    private lateinit var scanningResultTextView: TextView
    private lateinit var scanningModeSelectionTextView: TextView
    private lateinit var scanningResultsRecyclerView: RecyclerView

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

        scanningResultTextView=findViewById(R.id.scanningResultTextView)
        scanningModeSelectionTextView=findViewById(R.id.scanningModeSelectionTextView)
        updateScanningModeSelectionTextView()

        scanningResultsRecyclerView=findViewById(R.id.scanningResultsRecyclerView)
        scanningResultsRecyclerView.adapter=scanningResultsAdapter
        }
    override fun onDestroy()
        {
        rScan.deinitialize()

        super.onDestroy()
        }

    private fun newScanningResult(barcode: BarcodeInfo)
        {
        if (selectedScanningModeIndex==0 && !scanningResultsAdapter.addScanningResult(barcode))
        return

        barcodeScannerBeep.play()
        scanningResultTextView.text=barcode.description
        speech.speak(barcode.description)
        }
    private fun scanningResultSelected(scanningResult: BarcodeInfo)
        {
        startBarcodeIdentificationActivity(scanningResult)
        }
    private fun barcodeIdentificationActivityResult(result: ActivityResult)
        {
        if (result.resultCode==RESULT_OK) {
            val barcode=BarcodeInfo.fromIntent(result.data, "result", "MainActivity")


            }
        }

    //Interface components events

    fun previousScanningModeButton_click(view: View)
        {
        if (selectedScanningModeIndex==0)
        scanningResultsAdapter.clear()

        selectedScanningModeIndex-=1
        if (selectedScanningModeIndex<0) selectedScanningModeIndex=ScanningMode.scanningModes.size-1

        rScan.scanningMode=selectedScanningMode.code

        updateScanningModeSelectionTextView()
        }
    fun nextScanningModeButton_click(view: View)
        {
        if (selectedScanningModeIndex==0)
        scanningResultsAdapter.clear()

        selectedScanningModeIndex+=1
        selectedScanningModeIndex%=ScanningMode.scanningModes.size

        rScan.scanningMode=selectedScanningMode.code

        updateScanningModeSelectionTextView()
        }

    fun identifyButton_click(view: View)
        {
        if (rScan.scanningResult.value!="") {
            startBarcodeIdentificationActivity(rScan.scanningResult)
            }
        }

    private fun startBarcodeIdentificationActivity(barcode: BarcodeInfo)
        {
        val intent=Intent(this, BarcodeIdentificationActivity::class.java)
        intent.putExtra("barcode", rScan.scanningResult.csv())
        barcodeIdentificationActivityLauncher.launch(intent)
        }
    private fun updateScanningModeSelectionTextView()
        {
        scanningModeSelectionTextView.text=selectedScanningMode.name
        }
    }
