/*
* Copyright (C) 2023 Rastislav Kish
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

package com.rastislavkish.rscan.opticalidentificationactivity

import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent

import androidx.appcompat.app.AppCompatActivity

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

import com.rastislavkish.rscan.R
import com.rastislavkish.rscan.core.BarcodeInfo
import com.rastislavkish.rscan.core.SpeechExt

class OpticalIdentificationActivity: AppCompatActivity(), RecognitionListener {

    private lateinit var barcode: BarcodeInfo
    private var description=""

    private lateinit var speech: SpeechExt
    private lateinit var speechRecognizer: SpeechRecognizer
    private var ocrAppDisplayed=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_optical_identification)

        barcode=BarcodeInfo.fromIntent(intent, "barcode", "OpticalIdentificationActivity")

        speech=SpeechExt(this)
        speech.speechFinishListener=this::speechFinishHandler

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(this)

        val lookoutIntent=packageManager.getLaunchIntentForPackage("com.google.android.apps.accessibility.reveal")

        if (lookoutIntent!=null) {
            startActivity(lookoutIntent)
            }
        }

    override fun onPause() {
        super.onPause()

        if (!ocrAppDisplayed)
        ocrAppDisplayed=true

        speechRecognizer.cancel()
        }
    override fun onResume() {
        super.onResume()

        if (ocrAppDisplayed) {
            ocrAppDisplayed=false

            val recognizerIntent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

            speechRecognizer.startListening(recognizerIntent)
            }
        }

    fun speechFinishHandler(utteranceId: String) {
        val recognizerIntent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        runOnUiThread({ -> speechRecognizer.startListening(recognizerIntent) })
        }

    override fun onReadyForSpeech(params: Bundle) {

        }
    override fun onResults(params: Bundle) {
        val results: List<String> = params.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.toList() ?: listOf()

        if (!results.isEmpty()) {
            val result=results[0]

            if (result=="cancel") {
                finish()
                return
                }

            if ((result=="yes" || result=="áno" || result=="ano") && description!="") {
                val resultIntent=Intent()
                resultIntent.putExtra("result", Json.encodeToString(BarcodeInfo(barcode.type, barcode.value, description)))
                setResult(RESULT_OK, resultIntent)

                finish()
                return
                }

            description=result
            }

        speech.speak(description)
        }
    override fun onError(error: Int) {
        if (error==SpeechRecognizer.ERROR_NO_MATCH)
        speech.speak(description, false)
        }

    override fun onBeginningOfSpeech() {}
    override fun onBufferReceived(buffer: ByteArray) {}
    override fun onEndOfSpeech() {}
    override fun onEvent(eventType: Int, params: Bundle) {}
    override fun onPartialResults(partialResults: Bundle) {}
    override fun onRmsChanged(rmsdB: Float) {}

    }
