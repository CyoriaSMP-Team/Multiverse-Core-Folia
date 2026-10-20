package org.mvplugins.multiverse.core.utils

import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.Test
import kotlin.test.assertTrue

class FoliaSchedulerTest : TestWithMockBukkit() {

    @Test
    fun `runs global action inline on non-Folia servers`() {
        var ran = false

        FoliaScheduler.runGlobal(multiverseCore) { ran = true }

        assertTrue(ran)
    }
}
