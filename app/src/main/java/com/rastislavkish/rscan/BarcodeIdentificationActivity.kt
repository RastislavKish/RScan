package com.rastislavkish.rscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BarcodeIdentificationActivity: AppCompatActivity() {

    private lateinit var barcode: BarcodeInfo
    private var wordCount=0

    //Components

    private lateinit var barcodeInfoTextView: TextView

    private lateinit var barcodeDescriptionsRecyclerView: RecyclerView

    private lateinit var decreaseWordCountButton: Button
    private lateinit var wordCountTextView: TextView
    private lateinit var increaseWordCountButton: Button

    private lateinit var barcodeDescriptionEditText: EditText

    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?)
        {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_identification)

        val extras=intent.extras
        if (extras!=null) {
            barcode=BarcodeInfo.fromCsv(extras.getCharSequence("barcode").toString() ?: throw Exception("BarcodeIdentificationActivity did not receive a barcode in extras."))
            ?: throw Exception("Barcode received by BarcodeIdentificationActivity is invalid.")
            }
        else throw Exception("barcodeIdentificationActivity did not receive any extras.")

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
        }

    fun decreaseWordCountButton_click(view: View)
        {

        }
    fun increaseWordCountButton_click(view: View)
        {

        }

    fun saveButton_click(view: View)
        {

        }

    }
