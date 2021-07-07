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
