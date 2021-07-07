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

import android.content.Intent
import android.util.Base64

import java.nio.charset.StandardCharsets

class BarcodeInfo(type: Int, value: String, description: String="") {

    val type=type
    val value=value
    val description=when (description) {
        "" -> "${typeToString(type)}: $value"
        else -> description
        }
    val known=description!=""

    fun csv(): String="${typeToString(type)},$value,${String(Base64.encode(description.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT), StandardCharsets.UTF_8)}"

    override fun equals(other: Any?): Boolean
        {
        if (other is BarcodeInfo) {
            return type==other.type && value==other.value
            }

        return false
        }

    companion object {

        const val TYPE_EAN_13=1
        const val TYPE_EAN_8=2
        const val TYPE_UPC_A=4
        const val TYPE_UPC_E=8

        fun fromCsv(csv: String): BarcodeInfo?
            {
            val fields=csv.split(",")

            if (fields.size!=3)
            return null

            val type=when (fields[0].lowercase().replace("-", "").replace("_", "").replace(" ", "").trim()) {
                "ean13" -> TYPE_EAN_13
                "ean8" -> TYPE_EAN_8
                "upca" -> TYPE_UPC_A
                "upce" -> TYPE_UPC_E
                else -> return null
                }
            val value=fields[1]

            if (value=="")
            return null

            val description=String(Base64.decode(fields[2], Base64.DEFAULT), StandardCharsets.UTF_8)

            return BarcodeInfo(type, value, description)
            }
        fun fromIntent(intent: Intent?, key: String, caller: String=""): BarcodeInfo
            {
            if (intent!=null) {
                val extras=intent.extras
                if (extras!=null) {
                    val barcodeCsv=(extras.getCharSequence(key)
                    ?: throw Exception("$caller did not receive a barcode in received intent on key \"$key\"."))
                    .toString()

                    return BarcodeInfo.fromCsv(barcodeCsv)
                    ?: throw Exception("Barcode received by $caller is invalid.")
                    }
                else throw Exception("$caller did not receive any extras in the received intent.")
                }
            else throw Exception("$caller did not receive any intent.")
            }

        fun typeToString(type: Int): String
            {
            return when (type) {
                TYPE_EAN_13 -> "EAN-13"
                TYPE_EAN_8 -> "EAN-8"
                TYPE_UPC_A -> "UPC-A"
                TYPE_UPC_E -> "UPC-E"
                else -> "Unknown"
                };
            }
        }
    }
