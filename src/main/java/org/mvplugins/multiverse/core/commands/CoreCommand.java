package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.MultiverseCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.utils.FoliaScheduler;

/**
 * Represents a command that is part of the Multiverse-Core plugin.
 */
@Contract
@CommandAlias("mv")
public abstract class CoreCommand extends MultiverseCommand {
    protected final void runOnGlobal(Runnable action) {
        FoliaScheduler.runGlobal(plugin(), action);
    }

    protected final void runOnIssuer(MVCommandIssuer issuer, Runnable action) {
        Plugin plugin = plugin();
        if (FoliaScheduler.isFoliaServer() && issuer.getIssuer() instanceof Entity entity) {
            FoliaScheduler.runEntity(plugin, entity, action);
        } else {
            action.run();
        }
    }

    private Plugin plugin() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (plugin == null) {
            throw new IllegalStateException("Multiverse-Core plugin is not loaded");
        }
        return plugin;
    }
}
