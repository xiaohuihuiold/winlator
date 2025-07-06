package com.xhhold.winlator.ui.screens.home

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> {
    error("HomeViewModel not provided")
}

class HomeViewModel : ViewModel() {}