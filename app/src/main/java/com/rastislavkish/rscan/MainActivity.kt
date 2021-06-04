package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.rastislavkish.rtk.Sound
import com.rastislavkish.rtk.Speech

class MainActivity : AppCompatActivity() {

    private lateinit var speech: Speech

    private val barcodeScannerBeep=Sound()
    private lateinit var scanner: BScanner
    private lateinit var permissionsRequester: PermissionsRequester

    private var lastDetectedBarcode=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barcodeScannerBeep.load("BarcodeScannerBeep.opus", this)

        permissionsRequester=PermissionsRequester(this)
        if (!permissionsRequester.permissionsGranted) {
            permissionsRequester.requestPermissions(this)
            }

        speech=Speech(this)

        scanner=BScanner(this)
        scanner.addBarcodeDetectedListener(this::barcodeDetected)
        }

    fun barcodeDetected(value: String)
        {
        if (value!=lastDetectedBarcode) {
            lastDetectedBarcode=value

            barcodeScannerBeep.play()
            speech.speak(value)

            }
        }
    }
