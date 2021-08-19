package ru.sedavnyh.vkgroupscan.util

import android.util.Log
import java.util.*

class TextOperations {

    val titleRegex = Regex("[^a-zA-Z0-9- ]")

    fun cleanComment(comment: String?): String? {
        if (comment != null) {

            val preComment = comment.lowercase(Locale.getDefault())

            // Remove text in brackets
            var cleanedComment = removeTextInBrackets(preComment)

            // Strip non-special characters
            cleanedComment = cleanedComment.replace(titleRegex, " ")

            // Strip splitters and consecutive spaces
            val consecutiveSpacesRegex = Regex(" +")
            cleanedComment = cleanedComment.trim().replace(" - ", " ").replace(consecutiveSpacesRegex, " ").trim()
            cleanedComment = cleanedComment.replace("-", " ").trim()

            return cleanedComment
        }
        return null
    }

    fun cleanDescription(description: String) : MutableList<String> {

        var result : MutableList<String> = mutableListOf()

        var cleanedDescr = description.lowercase(Locale.getDefault())
        cleanedDescr = removeTextInBrackets(cleanedDescr)
        cleanedDescr = cleanedDescr.replace(titleRegex, " ")

        val regexDecimals = Regex("(\\d{6})")
        var matches = regexDecimals.findAll(cleanedDescr)

        val decimals = matches.map {
            it.groupValues[1] }.joinToString("")
        result.add(decimals)

        val regexStr = Regex("([^a-zA-Z ])")
        val letters = cleanedDescr.replace(regexStr, " ").trim()

        result.add(letters)

        return result
    }

    private fun removeTextInBrackets(text: String): String {
        val bracketPairs = listOf(
            '[' to ']'
        )
        var openingBracketPairs = bracketPairs.mapIndexed { index, (opening, _) ->
            opening to index
        }.toMap()
        var closingBracketPairs = bracketPairs.mapIndexed { index, (_, closing) ->
            closing to index
        }.toMap()

        val depthPairs = bracketPairs.map { 0 }.toMutableList()

        val result = StringBuilder()
        for (c in text ) {
            val openingBracketDepthIndex = openingBracketPairs[c]
            if (openingBracketDepthIndex != null) {
                depthPairs[openingBracketDepthIndex]++
            } else {
                val closingBracketDepthIndex = closingBracketPairs[c]
                if (closingBracketDepthIndex != null) {
                    depthPairs[closingBracketDepthIndex]--
                } else {
                    @Suppress("ControlFlowWithEmptyBody")
                    if (depthPairs.all { it <= 0 }) {
                        result.append(c)
                    } else {
                        // In brackets, do not append to result
                    }
                }
            }
        }
        return result.toString()
    }
}