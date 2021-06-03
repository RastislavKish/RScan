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

            cameraProvider?.bindToLifecycle(activity, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis)
            }, ContextCompat.getMainExecutor(activity))

        }

    fun analyzeImage(imageProxy: ImageProxy)
        {

        }

    }
