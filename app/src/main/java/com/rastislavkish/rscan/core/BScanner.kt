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

package com.rastislavkish.rscan.core

import android.util.Size

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

import androidx.camera.core.Camera
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

    var flashlight: Boolean=false
    set(value)
        {
        synchronized (flashlightLock) {
            if (camera!=null) {
                if (camera?.cameraInfo?.hasFlashUnit()==true) {
                    camera?.cameraControl?.enableTorch(value)
                    }
                }
            field=value
            }
        }

    private var flashlightLock=Any()

    private val activity=activity

    private var camera: Camera?=null
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
            synchronized (flashlightLock) {
                cameraProvider=cameraProviderFuture.get()

                camera=cameraProvider?.bindToLifecycle(activity, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis)

                if (camera?.cameraInfo?.hasFlashUnit()==true) {
                    camera?.cameraControl?.enableTorch(flashlight)
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
                    Barcode.FORMAT_EAN_13 -> BarcodeInfo.Type.EAN_13
                    Barcode.FORMAT_EAN_8 -> BarcodeInfo.Type.EAN_8
                    Barcode.FORMAT_UPC_A -> BarcodeInfo.Type.UPC_A
                    Barcode.FORMAT_UPC_E -> BarcodeInfo.Type.UPC_E
                    else -> continue
                    }

                val barcodeInfo=BarcodeInfo(type, barcode.rawValue ?: "")

                for (f in barcodeDetectedListeners) {
                    f(barcodeInfo)
                    }
                }
            }
        }

    }
