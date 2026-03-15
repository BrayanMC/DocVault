package com.docvault.domain.model

enum class DocumentType {
    PDF,
    IMAGE,
    ;

    companion object {
        fun fromString(value: String): DocumentType = entries.firstOrNull { it.name == value } ?: IMAGE
    }
}
