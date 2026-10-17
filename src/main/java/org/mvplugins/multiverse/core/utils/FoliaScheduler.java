package org.mvplugins.multiverse.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** Small ownership-aware scheduler bridge shared by Paper and Folia. */
public final class FoliaScheduler {

    private FoliaScheduler() { }

    /** Runs work on the entity's owning region, or silently drops it if the entity retired. */
    public static void runEntity(@NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable action) {
        entity.getScheduler().run(plugin, task -> action.run(), () -> { });
    }

    /** Runs delayed work on the entity's owning region, or silently drops it if it retired. */
    public static void runEntityLater(
            @NotNull Plugin plugin, @NotNull Entity entity, @NotNull Runnable action, long delayTicks) {
        entity.getScheduler().runDelayed(plugin, task -> action.run(), () -> { }, delayTicks);
    }

    /** Runs server-wide work on the global region scheduler on Paper and Folia. */
    public static void runGlobalLater(@NotNull Plugin plugin, @NotNull Runnable action, long delayTicks) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> action.run(), delayTicks);
    }
}
