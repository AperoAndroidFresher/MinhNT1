package com.apero.minhnt1.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log

fun convertBitmapToImage(contentUri: Uri?, context: Context): Bitmap? {
    var retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, contentUri)
        var data = retriever.embeddedPicture
        if (data != null) return BitmapFactory.decodeByteArray(data, 0, data.size)
    } catch (e: Exception) {
        Log.e("LibraryScreen", "Unable to convert $contentUri to image")
    } finally {
        retriever.release()
    }
    return null
}

fun getImageAsByteArray(contentUri: Uri?, context: Context): ByteArray? {
    var retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, contentUri)
        var data = retriever.embeddedPicture
        if (data != null) return data
    } catch (e: Exception) {
        Log.e("LibraryScreen", "Unable to convert $contentUri to image")
    } finally {
        retriever.release()
    }
    return null
}