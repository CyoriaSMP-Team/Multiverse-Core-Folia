package org.mvplugins.multiverse.core.utils;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

/**
 * Defers action that cannot be done during world tick.
 */
@Service
public final class WorldTickDeferrer {

    private final MultiverseCore plugin;

    @Inject
    WorldTickDeferrer(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Defer action that cannot be done during a world tick.
     * 
     * @param action The action to defer
     */
    public void deferWorldTick(Runnable action) {
        Logging.fine("Deferring world tick...");
        FoliaScheduler.runGlobalLater(this.plugin, action, 1L);
    }
}
