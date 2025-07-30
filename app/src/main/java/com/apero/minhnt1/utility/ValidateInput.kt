package com.apero.minhnt1.utility

import androidx.core.text.isDigitsOnly

fun validateInput(text: String, source: String): Boolean {
    when (source) {
        "PHONE NUMBER" -> {
            return text.isDigitsOnly()
        }

        "NAME", "UNIVERSITY NAME" -> {
            return text.matches(Regex("^[A-z\\s]*$"))
        }

        "PASSWORD", "USERNAME" -> {
            return text.matches(Regex("^[A-z0-9]*$"))
        }

        "EMAIL" -> {
            return text.matches(Regex("^[A-z0-9._-]+@(apero.vn)"))
        }
    }
    return false
}