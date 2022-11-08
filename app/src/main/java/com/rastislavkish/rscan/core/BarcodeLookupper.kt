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

    private val cache=mutableMapOf<BarcodeInfo, List<String>>()

    override fun lookupBarcode(barcode: BarcodeInfo): List<String>
        {
        if (barcode in cache)
        return cache[barcode] ?: throw Exception("DuckDuckGo lookup cache returned a null result")

        var results: MutableList<String>?=null

        val t=thread {
            try {
                val doc=Jsoup.connect("https://duckduckgo.com/html/?q=${barcode.value}").get()
                val headers=doc.getElementsByTag("h2")

                results=mutableListOf<String>()

                for (header in headers) {
                    if (header.text()!="") {
                        results?.add(header.text())
                        }
                    }
                }
            catch (e: Exception) {

                }
            }
        t.join()

        val result=results?.toList() ?: listOf<String>()

        if (results!=null)
        cache[barcode]=result

        return result
        }
    }
