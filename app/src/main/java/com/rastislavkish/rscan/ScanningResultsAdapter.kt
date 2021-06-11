package com.rastislavkish.rscan

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScanningResultsAdapter: RecyclerView.Adapter<ScanningResultsAdapter.ScanningResultViewHolder>() {

    private val scanningResultsList=mutableListOf<BarcodeInfo>()

    class ScanningResultViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val itemTextView: TextView=view.findViewById(R.id.itemTextView)

        private var scanningResult: BarcodeInfo=BarcodeInfo(0, "")

        fun bind(scanningResult: BarcodeInfo)
            {
            this.scanningResult=scanningResult

            itemTextView.text=scanningResult.description
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanningResultViewHolder
        {
        val view=LayoutInflater.from(parent.context)
        .inflate(R.layout.scanning_result_item, parent, false)

        return ScanningResultViewHolder(view)
        }
    override fun onBindViewHolder(viewHolder: ScanningResultViewHolder, position: Int)
        {
        viewHolder.bind(scanningResultsList[position])
        }
    override fun getItemCount() = scanningResultsList.size

    fun addScanningResult(scanningResult: BarcodeInfo): Boolean
        {
        if (scanningResult in scanningResultsList)
        return false

        scanningResultsList.add(scanningResult)

        notifyItemInserted(scanningResultsList.size-1)

        return true
        }
    fun clear()
        {
        val itemCount=scanningResultsList.size

        scanningResultsList.clear()

        notifyItemRangeRemoved(0, itemCount)
        }

    }
