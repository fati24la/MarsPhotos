package com.example.marsphotos.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.marsphotos.network.MarsApi
import com.example.marsphotos.network.MarsPhoto
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "MarsViewModel"

class MarsViewModel : ViewModel() {
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    init {
        getMarsPhotos()
    }

    fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading
            marsUiState = try {
                Log.d(TAG, "Calling API...")
                val photos = MarsApi.retrofitService.getPhotos()
                // Correction ici - éviter d'utiliser .size
                Log.d(TAG, "Received photos successfully")
                MarsUiState.Success(photos)
            } catch (e: IOException) {
                Log.e(TAG, "IOException: ${e.message}")
                MarsUiState.Error("Erreur réseau: Vérifiez votre connexion internet.")
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException: ${e.code()}, ${e.message()}")
                MarsUiState.Error("Erreur HTTP ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e(TAG, "General exception: ${e.message}")
                e.printStackTrace()
                MarsUiState.Error("Erreur inattendue: ${e.message}")
            }
        }
    }
}

sealed interface MarsUiState {
    data class Success(val photos: List<MarsPhoto>) : MarsUiState
    data class Error(val message: String) : MarsUiState
    object Loading : MarsUiState
}