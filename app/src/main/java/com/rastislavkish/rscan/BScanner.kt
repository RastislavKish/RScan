package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import android.util.Size

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider

import java.util.concurrent.Executors

import com.google.mlkit.vision.common.InputImage

import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions

class BarcodeInfo(type: Int, value: String, description: String="") {

    val type=type
    val value=value
    val description=when (description) {
        "" -> "${typeToString(type)}: $value"
        else -> description
        }

    fun csv(): String="${typeToString(type)},$value,$description"

    override fun equals(other: Any?): Boolean
        {
        if (other is BarcodeInfo) {
            return type==other.type && value==other.value
            }

        return false
        }

    companion object {

        const val TYPE_EAN_13=1
        const val TYPE_EAN_8=2
        const val TYPE_UPC_A=4
        const val TYPE_UPC_E=8

        fun fromCsv(csv: String): BarcodeInfo?
            {
            val fields=csv.split(",")

            if (fields.size!=3)
            return null

            val type=when (fields[0].lowercase().replace("-", "").replace("_", "").replace(" ", "").trim()) {
                "ean13" -> TYPE_EAN_13
                "ean8" -> TYPE_EAN_8
                "upca" -> TYPE_UPC_A
                "upce" -> TYPE_UPC_E
                else -> return null
                }
            val value=fields[1]

            if (value=="")
            return null

            val description=fields[2]

            return BarcodeInfo(type, value, description)
            }

        fun typeToString(type: Int): String
            {
            return when (type) {
                TYPE_EAN_13 -> "EAN-13"
                TYPE_EAN_8 -> "EAN-8"
                TYPE_UPC_A -> "UPC-A"
                TYPE_UPC_E -> "UPC-E"
                else -> "Unknown"
                };
            }
        }
    }
class BScanner(activity: AppCompatActivity) {

    private val activity=activity

    private val cameraExecutor=Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider?=null

    private val scanner: BarcodeScanner
    private val imageAnalysis: ImageAnalysis

    private var barcodeDetectedListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    init {

        val options=BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        .build()

        scanner=BarcodeScanning.getClient(options)

        imageAnalysis=ImageAnalysis.Builder()
        .setTargetResolution(Size(1920, 1080))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

        imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer(this::analyzeImage))

        val cameraProviderFuture=ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider=cameraProviderFuture.get()

            val camera=cameraProvider?.bindToLifecycle(activity, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis)
            if (camera!=null) {
                if (camera.cameraInfo.hasFlashUnit()) {
                    camera.cameraControl.enableTorch(true)
                    }
                }
            }, ContextCompat.getMainExecutor(activity))
        }

    fun addBarcodeDetectedListener(f: (BarcodeInfo) -> Unit)
        {
        barcodeDetectedListeners.add(f)
        }

    fun deinitialize()
        {
        cameraExecutor.shutdown()
        }

    private fun analyzeImage(imageProxy: ImageProxy)
        {
        val mediaImage=imageProxy.image
        if (mediaImage!=null) {
            val image=InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
            .addOnCompleteListener({ _ -> imageProxy.close() })
            .addOnSuccessListener(this::barcodesDetected)
            }
        }
    private fun barcodesDetected(barcodes: List<Barcode>)
        {
        for (barcode in barcodes) {
            if (barcode.rawValue!=null) {
                val type=when (barcode.format) {
                    Barcode.FORMAT_EAN_13 -> BarcodeInfo.TYPE_EAN_13
                    else -> 0
                    }

                if (type!=0) {
                    val barcodeInfo=BarcodeInfo(type, barcode.rawValue ?: "")

                    for (f in barcodeDetectedListeners) {
                        f(barcodeInfo)
                        }
                    }
                }
            }
        }

    }
