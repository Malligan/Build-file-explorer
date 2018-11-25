package com.alged.bfe.extensions

fun String.removeQuotes(): String = this.replace("'", "")

fun String.inverseStringCommenting(): String = when {
    this.contains("//") -> this.replace("//", "")
    else -> "//$this"
}