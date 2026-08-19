package com.zuexx.forgekit.samples

/**
 * The view model depends on this, never on a concrete implementation. That is what lets it be
 * tested without a network, an emulator, or the Android framework.
 */
interface SampleResourceProvider {
    suspend fun load(): List<SampleResource>
}

class SampleResourceLoadException(val reason: String) : Exception(reason)

/**
 * The implementation the app runs with. Swap it for one that talks to a real backend; the view
 * model and the screen do not change.
 */
class InMemorySampleResourceProvider(
    private val resources: List<SampleResource> = listOf(
        SampleResource("1", "First resource"),
        SampleResource("2", "Second resource"),
        SampleResource("3", "Third resource"),
    ),
) : SampleResourceProvider {
    override suspend fun load(): List<SampleResource> = resources
}
