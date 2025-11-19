package com.gcgenome.lims.interpretable

import org.springframework.data.elasticsearch.core.document.Document

interface DocumentUtil: Unwrappable {
    fun Document.percentage(key: String): String? {
        val s = this.getStringUnwrap(key) ?: return null
        val number = s.toDouble() * 100
        return if (number < 0.0001) String.format("%f", number) else String.format("%.4f", number)
    }
}