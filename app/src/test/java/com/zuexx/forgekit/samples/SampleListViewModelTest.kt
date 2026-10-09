package com.zuexx.forgekit.samples

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private class StubProvider(
    private val result: Result<List<SampleResource>>,
) : SampleResourceProvider {
    override suspend fun load(): List<SampleResource> = result.getOrThrow()
}

class SampleListViewModelTest {

    @Test
    fun `load publishes the resources it was given`() = runTest {
        val resources = listOf(SampleResource("42", "Answer"))
        val viewModel = SampleListViewModel(StubProvider(Result.success(resources)))

        viewModel.load()

        // Assert the value, not merely that nothing failed: a state check alone would still
        // pass if load() published an empty list, which is the case worth catching.
        assertEquals(SampleListViewModel.State.Loaded(resources), viewModel.state.value)
    }

    @Test
    fun `load surfaces the failure reason`() = runTest {
        val viewModel = SampleListViewModel(
            StubProvider(Result.failure(SampleResourceLoadException("no network"))),
        )

        viewModel.load()

        assertEquals(SampleListViewModel.State.Failed("no network"), viewModel.state.value)
    }

    @Test
    fun `load replaces an unclassified exception's message with a fixed generic reason`() =
        runTest {
            // Anything that is not a SampleResourceLoadException is unclassified and must never
            // have its raw message (which could carry internal URLs, timeout details, etc. once
            // this provider talks to a real backend) surfaced to the user.
            val viewModel = SampleListViewModel(
                StubProvider(Result.failure(IllegalStateException("http://internal/host timed out"))),
            )

            viewModel.load()

            assertEquals(
                SampleListViewModel.State.Failed(SampleListViewModel.UNCLASSIFIED_FAILURE_REASON),
                viewModel.state.value,
            )
        }
}
