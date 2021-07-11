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

package com.rastislavkish.rscan.barcodeidentificationactivity

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.rastislavkish.rscan.R

class BarcodeDescriptionsAdapter(barcodeDescriptionsList: List<String>): RecyclerView.Adapter<BarcodeDescriptionsAdapter.BarcodeDescriptionViewHolder>() {

    private val barcodeDescriptionsList=barcodeDescriptionsList
    private val descriptionSelectedListeners=mutableListOf<(String) -> Unit>()

    class BarcodeDescriptionViewHolder(view: View, descriptionSelectedListeners: MutableList<(String) -> Unit>): RecyclerView.ViewHolder(view) {

        private val itemTextView: TextView=view.findViewById(R.id.itemTextView)
        private val descriptionSelectedListeners=descriptionSelectedListeners

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
