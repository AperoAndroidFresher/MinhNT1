package com.apero.minhnt1.network

import com.apero.minhnt1.database.song.Song
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("Remote_audio.json")
    fun getSongs(): Call<List<Song>>
}