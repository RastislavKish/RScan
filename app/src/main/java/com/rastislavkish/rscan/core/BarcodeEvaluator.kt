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

import kotlin.text.Regex
import kotlin.text.RegexOption

class DuckDuckGoBarcodeEvaluator {

    val lookupper=DuckDuckGoBarcodeLookupper()

    //Should match words, but also measures like 1000ml, 1000dm3, but not too long numbers such as the barcode itself if present in the result.
    val wordRegex=Regex(
        """^([a-zľščťžýáíéúňôäüöěř]{3,})|([0-9,.]{1,6}([a-z]{1,3}\d?)?)$""",
        RegexOption.IGNORE_CASE)

    //Supposed to match known measures, so results containing them can be prioritized
    val knownMeasureRegex=Regex(
        """[^\d][0-9]{1,6}([,.]\d{1,5})? ?(g|kg|mg|l|ml|dl|%)( |$)""",
        RegexOption.IGNORE_CASE)

    //Supposed to match measures, so results containing them can be prioritized
    val measureRegex=Regex(
        """[^\d][0-9]{1,6}([,.]\d{1,5})? ?[a-z0-9%]{1,3}( |$)""",
        RegexOption.IGNORE_CASE)

    fun evaluateBarcode(barcode: BarcodeInfo): String {
        val lookup=lookupper.lookupBarcode(barcode)

        var result: String?=null
        var maxWordCount=0
        var measurePresenceThreshold=0.0

        for (description in lookup) {
            var wordCount=0
            var measurePresenceCertainty=if (knownMeasureRegex.containsMatchIn(description))
            1.0
            else if (measureRegex.containsMatchIn(description))
            0.5
            else
            0.0

            for (word in description.split(" ")) {
                if (wordRegex.matches(word))
                wordCount+=1

                }

            if (measurePresenceCertainty<measurePresenceThreshold)
            continue

            if (measurePresenceCertainty>measurePresenceThreshold) {
                result=description
                maxWordCount=wordCount
                measurePresenceThreshold=measurePresenceCertainty
                continue
                }

            if (wordCount>maxWordCount) {
                result=description
                maxWordCount=wordCount
                }
            }

        return result ?: ""
        }
    }
