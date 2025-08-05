package com.apero.minhnt1.utility

import android.icu.text.SimpleDateFormat
import java.util.Date

fun millisToDuration(duration: Long): String {
    val format = SimpleDateFormat("mm:ss")
    return format.format(Date(duration))

}