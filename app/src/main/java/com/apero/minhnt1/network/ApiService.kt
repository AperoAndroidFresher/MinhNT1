package com.apero.minhnt1.network

import com.apero.minhnt1.database.song.Song
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    @GET("Remote_audio.json")
    fun getListOfSongs(): Call<List<Song>>

    @Streaming
    @GET()
    fun getSong(@Url songUrl: String): Call<ResponseBody>
}