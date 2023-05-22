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

package com.rastislavkish.rscan.mainactivity

import android.content.res.Configuration
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.SharedPreferences
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

import android.view.View

import android.widget.Button
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

import com.squareup.seismic.ShakeDetector

import com.rastislavkish.rtk.Sound
import com.rastislavkish.rtk.Speech

import com.rastislavkish.rscan.R
import com.rastislavkish.rscan.core.BarcodeInfo
import com.rastislavkish.rscan.core.PermissionsRequester
import com.rastislavkish.rscan.core.RScan
import com.rastislavkish.rscan.core.Settings
import com.rastislavkish.rscan.barcodeidentificationactivity.BarcodeIdentificationActivity
import com.rastislavkish.rscan.opticalidentificationactivity.OpticalIdentificationActivity

class MainActivity : AppCompatActivity(), ShakeDetector.Listener {

    private lateinit var speech: Speech

    private val barcodeScannerBeep=Sound()
    private lateinit var permissionsRequester: PermissionsRequester

    private lateinit var settings: Settings
    private lateinit var rScan: RScan
    private val scanningResultsAdapter=ScanningResultsAdapter()
    private lateinit var barcodeIdentificationActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var opticalIdentificationActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector

    //Components

    private lateinit var scanningResultsRecyclerView: RecyclerView

    private lateinit var flashlightToggleButton: ToggleButton

    private val activityOrientationOk: Boolean
    get() = resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE

    override fun onCreate(savedInstanceState: Bundle?)
        {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        settings=Settings(getSharedPreferences("RScanSettings", MODE_PRIVATE))
        settings.load()

        barcodeScannerBeep.load("BarcodeScannerBeep.opus", this)

        permissionsRequester=PermissionsRequester(this)
        if (!permissionsRequester.permissionsGranted) {
            permissionsRequester.requestPermissions(this)
            }

        barcodeIdentificationActivityLauncher=registerForActivityResult(StartActivityForResult(), this::barcodeIdentificationActivityResult)
        opticalIdentificationActivityLauncher=registerForActivityResult(StartActivityForResult(), this::opticalIdentificationActivityResult)

        speech=Speech(this)
        sensorManager=getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector=ShakeDetector(this)

        if (activityOrientationOk) {
            rScan=RScan(this)
            rScan.addNewScanningResultListener(this::newScanningResult)
            }

        scanningResultsAdapter.addScanningResultSelectedListener(this::scanningResultSelected)

        //Load the interface

        flashlightToggleButton=findViewById(R.id.flashlightToggleButton)
        flashlightToggleButton.setChecked(settings.useFlashlight)

        scanningResultsRecyclerView=findViewById(R.id.scanningResultsRecyclerView)
        scanningResultsRecyclerView.adapter=scanningResultsAdapter
        }
    override fun onDestroy()
        {
        if (!activityOrientationOk) {
            super.onDestroy()
            return
            }

        rScan.deinitialize()

        super.onDestroy()
        }

    override fun onResume()
        {
        super.onResume()

        if (!this::rScan.isInitialized)
        return

        rScan.setFlashlightState(settings.useFlashlight)
        shakeDetector.start(sensorManager)
        }
    override fun onPause()
        {
        super.onPause()

        if (!this::rScan.isInitialized)
        return

        shakeDetector.stop()
        }

    override fun hearShake() {
        val lastScanningResult=scanningResultsAdapter.lastScanningResult

        if (lastScanningResult!=null) {
            startOpticalIdentificationActivity(lastScanningResult)
            }
        }

    private fun newScanningResult(scanningResult: BarcodeInfo)
        {
        if (!scanningResultsAdapter.addScanningResult(scanningResult))
        return

        barcodeScannerBeep.play()
        if (scanningResult.description!=null)
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

            if (scanningResultsAdapter.removeScanningResult(barcode)) {
                rScan.cacheBarcode(barcode)
                }
            }
        }
    private fun opticalIdentificationActivityResult(result: ActivityResult)
        {
        if (result.resultCode==RESULT_OK) {
            val barcode=BarcodeInfo.fromIntent(result.data, "result", "MainActivity")

            if (scanningResultsAdapter.removeScanningResult(barcode)) {
                rScan.cacheBarcode(barcode)
                }
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
    fun importFromClipboardButton_click(view: View)
        {
        if (rScan.importFromClipboard())
        speech.speak("Import successful")
        else
        speech.speak("Import failed.")
        }
    fun exportToClipboardButton_click(view: View)
        {
        rScan.exportToClipboard()
        speech.speak("Copied")
        }

    private fun startBarcodeIdentificationActivity(barcode: BarcodeInfo)
        {
        val intent=Intent(this, BarcodeIdentificationActivity::class.java)
        intent.putExtra("barcode", Json.encodeToString(barcode))
        barcodeIdentificationActivityLauncher.launch(intent)
        }
    private fun startOpticalIdentificationActivity(barcode: BarcodeInfo)
        {
        val intent=Intent(this, OpticalIdentificationActivity::class.java)
        intent.putExtra("barcode", Json.encodeToString(barcode))
        opticalIdentificationActivityLauncher.launch(intent)
        }
    }
