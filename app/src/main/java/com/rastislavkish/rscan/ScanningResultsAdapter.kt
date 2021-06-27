package com.rastislavkish.rscan

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScanningResultsAdapter: RecyclerView.Adapter<ScanningResultsAdapter.ScanningResultViewHolder>() {

    private val scanningResultsList=mutableListOf<BarcodeInfo>()
    private val scanningResultSelectedListeners=mutableListOf<(BarcodeInfo) -> Unit>()

    class ScanningResultViewHolder(view: View, scanningResultSelectedListeners: MutableList<(BarcodeInfo) -> Unit>): RecyclerView.ViewHolder(view) {

        private val itemTextView: TextView=view.findViewById(R.id.itemTextView)

        init {
            itemTextView.setOnClickListener(this::itemTextView_click)
            }

        private val scanningResultSelectedListeners=scanningResultSelectedListeners

        private var scanningResult: BarcodeInfo=BarcodeInfo(0, "")

        fun bind(scanningResult: BarcodeInfo)
            {
            this.scanningResult=scanningResult

            itemTextView.text=scanningResult.description
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
        if (scanningResult in scanningResultsList)
        return false

        scanningResultsList.add(0, scanningResult)

        notifyItemInserted(0)

        return true
        }
    fun clear()
        {
        val itemCount=scanningResultsList.size

        scanningResultsList.clear()

        notifyItemRangeRemoved(0, itemCount)
        }

    }
