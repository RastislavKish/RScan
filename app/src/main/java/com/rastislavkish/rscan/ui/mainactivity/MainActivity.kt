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

package com.rastislavkish.rscan.ui.mainactivity

import android.content.res.Configuration
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

import android.view.View
import android.view.OrientationEventListener

import android.widget.Button
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

import com.rastislavkish.rtk.Sound
import com.rastislavkish.rtk.Speech

import com.rastislavkish.rscan.R
import com.rastislavkish.rscan.core.BarcodeInfo
import com.rastislavkish.rscan.core.PermissionsRequester
import com.rastislavkish.rscan.core.RScan
import com.rastislavkish.rscan.core.Settings
import com.rastislavkish.rscan.ui.barcodeidentificationactivity.BarcodeIdentificationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var speech: Speech
    private lateinit var orientationEventListener: OrientationEventListener

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
        orientationEventListener=object : OrientationEventListener(this) {

            override fun onOrientationChanged(orientation: Int) {
                onOrientationChange(orientation)
                }
            }

        rScan=RScan(this)
        rScan.setFlashlightState(settings.useFlashlight)
        rScan.addNewScanningResultListener(this::newScanningResult)

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

    override fun onPause()
        {
        orientationEventListener.disable()
        super.onPause()
        }
    override fun onResume()
        {
        orientationEventListener.enable()
        super.onResume()
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

    var lastRotationValue=-1
    fun onOrientationChange(orientation: Int)
        {
        if (orientation==OrientationEventListener.ORIENTATION_UNKNOWN) {
            return
            }

        val rotation=androidx.camera.core.UseCase.snapToSurfaceRotation(orientation)

        if (rotation!=lastRotationValue) {
            rScan.updateDeviceRotation(rotation)

            lastRotationValue=rotation
            }
        }

    private fun startBarcodeIdentificationActivity(barcode: BarcodeInfo)
        {
        val intent=Intent(this, BarcodeIdentificationActivity::class.java)
        intent.putExtra("barcode", Json.encodeToString(barcode))
        barcodeIdentificationActivityLauncher.launch(intent)
        }
    }
