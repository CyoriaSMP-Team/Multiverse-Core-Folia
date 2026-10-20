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

    /** Check the running implementation without reading entity/world state from another region. */
    public static boolean isFoliaServer() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer", false,
                    Bukkit.getServer().getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /** Runs server-wide work on the global region scheduler on Paper and Folia. */
    public static void runGlobalLater(@NotNull Plugin plugin, @NotNull Runnable action, long delayTicks) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> action.run(), delayTicks);
    }

    /** Runs server-wide configuration changes on Folia's global region. */
    public static void runGlobal(@NotNull Plugin plugin, @NotNull Runnable action) {
        if (isFoliaServer()) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, action);
        } else {
            action.run();
        }
    }
}
