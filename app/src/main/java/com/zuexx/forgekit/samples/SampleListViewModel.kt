package com.zuexx.forgekit.samples

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SampleListViewModel(
    private val provider: SampleResourceProvider = InMemorySampleResourceProvider(),
) : ViewModel() {

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Loaded(val resources: List<SampleResource>) : State
        data class Failed(val reason: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    /**
     * Suspends rather than launching into viewModelScope so that a unit test can await it
     * directly, with no main-dispatcher rule and nothing to synchronise.
     */
    suspend fun load() {
        _state.value = State.Loading
        _state.value = try {
            State.Loaded(provider.load())
        } catch (failure: SampleResourceLoadException) {
            State.Failed(failure.reason)
        } catch (error: Exception) {
            State.Failed(error.toString())
        }
    }
}
