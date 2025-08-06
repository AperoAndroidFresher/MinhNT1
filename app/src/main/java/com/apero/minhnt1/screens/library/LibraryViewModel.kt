package com.apero.minhnt1.screens.library

import android.util.Log
import androidx.lifecycle.ViewModel
import com.apero.minhnt1.database.song.Song
import com.apero.minhnt1.network.ApiClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryViewModel : ViewModel() {
    private val _state = MutableStateFlow<LibraryMviState>(LibraryMviState())
    val state: StateFlow<LibraryMviState> = _state.asStateFlow()
    private val _event = MutableSharedFlow<LibraryMviEvents>()
    val event: SharedFlow<LibraryMviEvents> = _event.asSharedFlow()
    private val _intentFlow = MutableSharedFlow<LibraryMviIntents>()

    fun processIntent(intent: LibraryMviIntents) {
        //   viewModelScope.launch {
        //       _intentFlow.collect { intent ->
        when (intent) {
            is LibraryMviIntents.SwitchView -> switchView()
            is LibraryMviIntents.GetMusicFromRemote -> getMusicFromRemote()
        }
        //      }
        //  }
    }

    private fun switchView() {
        _state.value.isList.value = !_state.value.isList.value
    }

    fun getMusicFromRemote() {
        val call = ApiClient.build().getSongs()
        call.enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                when {
                    response.isSuccessful -> {
                        var jsonList = response.body()
                        jsonList?.forEach {
                            //if (!state.value.remoteSongLibrary.contains(it))
                            state.value.remoteSongLibrary.add(
                                Song(
                                    title = it.title,
                                    artist = it.artist,
                                    duration = it.duration.toLong(),
                                    path = it.path
                                )
                            )
                            Log.d("Retrofit response", it.title)
                        }
                    }

                    response.code() == 400 -> Log.e("LibraryViewModel", "Bad Request")
                    response.code() == 401 -> Log.e("LibraryViewModel", "Unauthorized")
                    response.code() == 403 -> Log.e("LibraryViewModel", "Forbidden")
                    response.code() == 404 -> Log.e("LibraryViewModel", "Not Found")
                    response.code() == 500 -> Log.e("LibraryViewModel", "Internal Server Error")
                    else -> Log.e("LibraryViewModel", "Unknown Error")
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Log.e("LibraryViewModel", "onFailure: ${t.message}")

            }
        })

    }


}