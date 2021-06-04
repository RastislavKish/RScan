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

class BScanner(activity: AppCompatActivity) {

    private val activity=activity

    private val cameraExecutor=Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider?=null

    private val scanner: BarcodeScanner
    private val imageAnalysis: ImageAnalysis

    private var barcodeDetectedListeners=mutableListOf<(String) -> Unit>()

    init {

        val options=BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
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

    fun addBarcodeDetectedListener(f: (String) -> Unit)
        {
        barcodeDetectedListeners.add(f)
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
            for (f in barcodeDetectedListeners) {
                if (barcode.rawValue!=null) {
                    f(barcode.rawValue ?: "")
                    }
                }
            }
        }

    }
