package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.rastislavkish.rtk.Sound
import com.rastislavkish.rtk.Speech

import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.ScanMode
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback

import com.google.zxing.Result

class MainActivity : AppCompatActivity() {

    private lateinit var speech: Speech

    private val barcodeScannerBeep=Sound()
    private lateinit var scanner: BScanner
    private lateinit var codeScanner: CodeScanner
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

        val scannerView=findViewById<CodeScannerView>(R.id.codeScannerView)
        codeScanner=CodeScanner(this, scannerView)
        codeScanner.autoFocusMode=AutoFocusMode.CONTINUOUS
        //codeScanner.scanMode=ScanMode.CONTINUOUS
        //codeScanner.decodeCallback=DecodeCallback(this::decodeCallback)

        scanner=BScanner(this)
        scanner.addBarcodeDetectedListener(this::barcodeDetected)
        }
    override fun onPause()
        {
        super.onPause()

        //codeScanner.releaseResources()
        }
    override fun onResume()
        {
        super.onResume()

        //codeScanner.startPreview()
        }

    fun decodeCallback(result: Result)
        {
        if (result.text!=lastDetectedBarcode) {
            lastDetectedBarcode=result.text

            barcodeScannerBeep.play()
            speech.speak(result.text)

            }
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
