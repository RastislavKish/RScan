package com.rastislavkish.rscan.core

import kotlin.concurrent.thread

import org.jsoup.Jsoup

interface BarcodeLookupper {

    val name: String
    val checked: Boolean

    fun lookupBarcode(barcode: BarcodeInfo): List<String>
    }
class DuckDuckGoBarcodeLookupper: BarcodeLookupper {

    override val name="DuckDuckGo"
    override val checked=false

    override fun lookupBarcode(barcode: BarcodeInfo): List<String>
        {
        val results=mutableListOf<String>()

        val t=thread {
            val doc=Jsoup.connect("https://duckduckgo.com/html/?q=${barcode.value}").get()
            val headers=doc.getElementsByTag("h2")

            for (header in headers) {
                if (header.text()!="") {
                    results.add(header.text())
                    }
                }
            }
        t.join()

        return results.toList()
        }
    }
