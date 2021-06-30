package com.rastislavkish.rscan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.rastislavkish.rtk.Speech

class BarcodeIdentificationActivity: AppCompatActivity() {

    private lateinit var barcode: BarcodeInfo
    private var wordCount=0

    private lateinit var speech: Speech

    //Components

    private lateinit var barcodeInfoTextView: TextView

    private lateinit var barcodeDescriptionsRecyclerView: RecyclerView

    private lateinit var decreaseWordCountButton: Button
    private lateinit var wordCountTextView: TextView
    private lateinit var increaseWordCountButton: Button

    private lateinit var barcodeDescriptionEditText: EditText

    private lateinit var saveButton: Button

    //Components for components

    private lateinit var barcodeDescriptionsAdapter: BarcodeDescriptionsAdapter

    override fun onCreate(savedInstanceState: Bundle?)
        {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_identification)

        barcode=BarcodeInfo.fromIntent(intent, "barcode", "BarcodeIdentificationActivity")

        //Do some preinitialization

        speech=Speech(this)

        //Load activity components

        barcodeInfoTextView=findViewById(R.id.barcodeInfoTextView)

        barcodeDescriptionsRecyclerView=findViewById(R.id.barcodeDescriptionsRecyclerView)

        decreaseWordCountButton=findViewById(R.id.decreaseWordCountButton)
        wordCountTextView=findViewById(R.id.wordCountTextView)
        increaseWordCountButton=findViewById(R.id.increaseWordCountButton)

        barcodeDescriptionEditText=findViewById(R.id.barcodeDescriptionEditText)

        saveButton=findViewById(R.id.saveButton)

        //Set what's necessary

        barcodeInfoTextView.text=barcode.description
        wordCountTextView.text=wordCount.toString()

        barcodeDescriptionsAdapter=BarcodeDescriptionsAdapter(DuckDuckGoBarcodeLookupper().lookupBarcode(barcode))
        barcodeDescriptionsAdapter.addDescriptionSelectedListener(this::barcodeDescriptionsRecyclerView_descriptionSelected)
        barcodeDescriptionsRecyclerView.adapter=barcodeDescriptionsAdapter
        }

    fun barcodeDescriptionsRecyclerView_descriptionSelected(description: String)
        {
        barcodeDescriptionEditText.setText(description)
        wordCount=0
        wordCountTextView.text="0"
        }

    fun decreaseWordCountButton_click(view: View)
        {
        if (wordCount>0) {
            wordCount-=1
            wordCountTextView.text=wordCount.toString()

            speech.speak(getIthWord(barcodeDescriptionEditText.text.toString(), wordCount-1))
            }
        }
    fun increaseWordCountButton_click(view: View)
        {
        wordCount+=1
        wordCountTextView.text=wordCount.toString()

        speech.speak(getIthWord(barcodeDescriptionEditText.text.toString(), wordCount-1))
        }

    fun saveButton_click(view: View)
        {
        val resultIntent=Intent()
        resultIntent.putExtra("result", BarcodeInfo(barcode.type, barcode.value, getWords(barcodeDescriptionEditText.text.toString(), wordCount)).csv())
        setResult(RESULT_OK, resultIntent)

        finish()
        }

    private val spacesRegex=Regex("\\s{2,}")
    private fun clearString(input: String): String
        {
        return spacesRegex.replace(input, "").trim()
        }
    private fun getIthWord(input: String, wordIndex: Int): String
        {
        if (wordIndex<0) return ""

        val words=clearString(input).split(" ")

        if (wordIndex>=words.size) return ""

        return words[wordIndex]
        }
    private fun getWords(input: String, wordCount: Int): String
        {
        if (wordCount<0) return ""

        if (wordCount==0) return input

        val words=clearString(input).split(" ")

        if (wordCount>words.size) return input

        return words.slice(0 until wordCount).joinToString(" ")
        }
    }