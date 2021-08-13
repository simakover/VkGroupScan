package ru.sedavnyh.vkgroupscan.util

import android.util.Log
import java.util.*

class TextOperations {

    fun cleanComment(comment: String?): String? {
        if (comment != null) {

            val preComment = comment.lowercase(Locale.getDefault())

            // Remove text in brackets
            var cleanedComment = removeTextInBrackets(preComment)

            // Strip non-special characters
            val titleRegex = Regex("[^a-zA-Z0-9- ]")
            cleanedComment = cleanedComment.replace(titleRegex, " ")

            // Strip splitters and consecutive spaces
            val consecutiveSpacesRegex = Regex(" +")
            cleanedComment = cleanedComment.trim().replace(" - ", " ").replace(consecutiveSpacesRegex, " ").trim()

            return cleanedComment
        }
        return null
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