package com.example.myroom.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myroom.repositori.RepositoriSiswa
import com.example.myroom.room.Siswa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriSiswa: RepositoriSiswa
) : ViewModel() {

    private val siswaId: Int = checkNotNull(savedStateHandle["siswaId"]) // Assuming the navigation argument is named "siswaId"

    val uiState: StateFlow<DetailUiState> =
        repositoriSiswa.getSiswaStream(siswaId)
            .filterNotNull()
            .map {
                DetailUiState(detailSiswa = it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DetailUiState()
            )

    suspend fun deleteSiswa() {
        repositoriSiswa.deleteSiswa(uiState.value.detailSiswa)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class DetailUiState(
    val detailSiswa: Siswa = Siswa(0, "", "", "")
)
