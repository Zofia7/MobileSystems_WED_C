package com.example.ludapp.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PamiecLokalna(context: Context) {
    private val prefs = context.getSharedPreferences("ludapp_prefs", Context.MODE_PRIVATE)

    var zapisane by mutableStateOf(odczytaj())
        private set

    fun przelaczZapis(id: String) {
        zapisane = if (id in zapisane) zapisane - id else zapisane + id
        prefs.edit().putStringSet("zapisane_wydarzenia", zapisane).apply()
    }

    private fun odczytaj(): Set<String> =
        prefs.getStringSet("zapisane_wydarzenia", emptySet())?.toSet() ?: emptySet()
}