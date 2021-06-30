package com.rastislavkish.rscan

import android.content.Intent

class BarcodeInfo(type: Int, value: String, description: String="") {

    val type=type
    val value=value
    val description=when (description) {
        "" -> "${typeToString(type)}: $value"
        else -> description
        }
    val known=description!=""

    fun csv(): String="${typeToString(type)},$value,$description"

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

            val description=fields[2]

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
