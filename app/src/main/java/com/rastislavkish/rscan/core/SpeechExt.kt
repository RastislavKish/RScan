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

package com.rastislavkish.rscan.core

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener

class SpeechExt(context: Context): UtteranceProgressListener() {

    private val tts=TextToSpeech(context, null)

    init {
        tts.setOnUtteranceProgressListener(this)
        }

    var speechFinishListener: ((String) -> Unit)?=null

    fun speak(text: String, interrupt: Boolean=true, utteranceId: String?=null) {
        if (utteranceId==null) {
            lastUtteranceId+=1
            }

        tts.speak(text, if (interrupt) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, utteranceId ?: lastUtteranceId.toString())
        }

    override fun onStart(utteranceId: String) {

        }

    override fun onError(utteranceId: String) {

        }

    override fun onStop(utteranceId: String, interrupted: Boolean) {
        speechFinishListener?.invoke(utteranceId)
        }

    override fun onDone(utteranceId: String) {
        speechFinishListener?.invoke(utteranceId)
        }

    private var lastUtteranceId=0
    }
