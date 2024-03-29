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

package com.rastislavkish.rscan.ui.mainactivity

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.rastislavkish.rscan.R
import com.rastislavkish.rscan.core.BarcodeInfo

class ScanningResultsAdapter: RecyclerView.Adapter<ScanningResultsAdapter.ScanningResultViewHolder>() {

    private val scanningResultsList=mutableListOf<BarcodeInfo>()
    private val scanningResultSelectedListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    class ScanningResultViewHolder(view: View, scanningResultSelectedListeners: MutableList<(BarcodeInfo) -> Unit>): RecyclerView.ViewHolder(view) {

        private val itemTextView: TextView=view.findViewById(R.id.itemTextView)

        init {
            itemTextView.setOnClickListener(this::itemTextView_click)
            }

        private val scanningResultSelectedListeners=scanningResultSelectedListeners

        private var scanningResult: BarcodeInfo=BarcodeInfo(BarcodeInfo.Type.EAN_13, "")

        fun bind(scanningResult: BarcodeInfo)
            {
            this.scanningResult=scanningResult

            itemTextView.text=scanningResult.label
            }

        fun itemTextView_click(view: View)
            {
            for (listener in scanningResultSelectedListeners) {
                listener(scanningResult)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanningResultViewHolder
        {
        val view=LayoutInflater.from(parent.context)
        .inflate(R.layout.scanning_result_item, parent, false)

        return ScanningResultViewHolder(view, scanningResultSelectedListeners)
        }
    override fun onBindViewHolder(viewHolder: ScanningResultViewHolder, position: Int)
        {
        viewHolder.bind(scanningResultsList[position])
        }
    override fun getItemCount() = scanningResultsList.size

    fun addScanningResultSelectedListener(listener: (BarcodeInfo) -> Unit)
        {
        scanningResultSelectedListeners.add(listener)
        }

    fun addScanningResult(scanningResult: BarcodeInfo): Boolean
        {
        val listIndex=scanningResultsList.indexOf(scanningResult)
        if (listIndex>-1) {
            if (scanningResultsList[listIndex].description==null)
            return false

            scanningResultsList.removeAt(listIndex)
            scanningResultsList.add(0, scanningResult)

            notifyItemMoved(listIndex, 0)

            return true
            }

        scanningResultsList.add(0, scanningResult)

        notifyItemInserted(0)

        return true
        }
    fun removeScanningResult(scanningResult: BarcodeInfo): Boolean
        {
        if (scanningResult !in scanningResultsList)
        return false

        val listIndex=scanningResultsList.indexOf(scanningResult)
        if (listIndex>-1) {
            scanningResultsList.removeAt(listIndex)

            notifyItemRemoved(listIndex)

            return true
            }

        return false
        }
    fun clear()
        {
        val itemCount=scanningResultsList.size

        scanningResultsList.clear()

        notifyItemRangeRemoved(0, itemCount)
        }

    }
