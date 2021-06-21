package com.rastislavkish.rscan

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class BarcodeDescriptionsAdapter(barcodeDescriptionsList: List<String>): RecyclerView.Adapter<BarcodeDescriptionsAdapter.BarcodeDescriptionViewHolder>() {

    private val barcodeDescriptionsList=barcodeDescriptionsList
    private val descriptionSelectedListeners=mutableListOf<(String) -> Unit>()

    class BarcodeDescriptionViewHolder(view: View, descriptionSelectedListeners: MutableList<(String) -> Unit>): RecyclerView.ViewHolder(view) {

        val itemTextView: TextView=view.findViewById(R.id.itemTextView)
        val descriptionSelectedListeners=descriptionSelectedListeners

        init {
            itemTextView.setOnClickListener(this::itemTextView_click)
            }

        private var barcodeDescription=""

        fun bind(barcodeDescription: String)
            {
            this.barcodeDescription=barcodeDescription

            itemTextView.text=barcodeDescription
            }

        fun itemTextView_click(view: View)
            {
            for (listener in descriptionSelectedListeners) {
                listener(barcodeDescription)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeDescriptionViewHolder
        {
        val view=LayoutInflater.from(parent.context)
        .inflate(R.layout.barcode_description_item, parent, false)

        return BarcodeDescriptionViewHolder(view, descriptionSelectedListeners)
        }
    override fun onBindViewHolder(viewHolder: BarcodeDescriptionViewHolder, position: Int)
        {
        viewHolder.bind(barcodeDescriptionsList[position])
        }
    override fun getItemCount() = barcodeDescriptionsList.size

    fun addDescriptionSelectedListener(listener: (String) -> Unit)
        {
        descriptionSelectedListeners.add(listener)
        }

    }
