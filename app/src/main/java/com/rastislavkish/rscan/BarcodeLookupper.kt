package com.rastislavkish.rscan

import android.util.Log

import java.net.URL
import java.util.Scanner
import javax.net.ssl.HttpsURLConnection

import kotlin.concurrent.thread

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
