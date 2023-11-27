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

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
class BarcodeInfo(
    val type: Type,
    val value: String,
    val description: String?=null,
    ) {

    val label: String
    get() = description ?: "${typeToString(type)}: $value"

    override fun equals(other: Any?): Boolean
        {
        if (other is BarcodeInfo) {
            return type==other.type && value==other.value
            }

        return false
        }
    override fun hashCode(): Int {
        return type.hashCode() xor value.hashCode()
        }

    @Serializable
    enum class Type {
        EAN_13,
        EAN_8,
        UPC_A,
        UPC_E,
        }

    companion object {

        fun fromCsv(csv: String): BarcodeInfo?
            {
            //This method is kept only for backward compatibility reasons.

            val fields=csv.split(",")

            if (fields.size!=3)
            return null

            val type=when (fields[0].lowercase().replace("-", "").replace("_", "").replace(" ", "").trim()) {
                "ean13" -> Type.EAN_13
                "ean8" -> Type.EAN_8
                "upca" -> Type.UPC_A
                "upce" -> Type.UPC_E
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
                    val barcodeJson=(extras.getCharSequence(key)
                    ?: throw Exception("$caller did not receive a barcode in received intent on key \"$key\"."))
                    .toString()

                    try {
                        return Json.decodeFromString<BarcodeInfo>(barcodeJson)
                        }
                    catch (e: Exception) {
                        throw Exception("Barcode received by $caller is invalid.")
                        }
                    }
                else throw Exception("$caller did not receive any extras in the received intent.")
                }
            else throw Exception("$caller did not receive any intent.")
            }

        fun typeToString(type: Type): String
            {
            return when (type) {
                Type.EAN_13 -> "EAN-13"
                Type.EAN_8 -> "EAN-8"
                Type.UPC_A -> "UPC-A"
                Type.UPC_E -> "UPC-E"
                };
            }
        }
    }
