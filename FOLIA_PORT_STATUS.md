# Folia 1.21.11 port status

**Status: experimental live testing.** The fork enables Folia metadata for the requested iterative test cycle. This is not a claim that every ownership path is complete or safe.

## Verified source changes

- Added FoliaScheduler for entity-owned work and server-wide deferred work; it uses Paper API schedulers and retains Paper compatibility.
- Routed passenger remounting, post-teleport velocity, delayed player game-mode and flight enforcement, and world-tick deferrals through Folia schedulers.
- Routed world-level settings and spawn-category settings through the global region scheduler.
- Preserved saved generator settings when WorldManager recreates a world during load and added a regression test.
- Added a Folia autoload guard: if an autoloaded world is not already present, Multiverse skips Bukkit world creation and waits for an external WorldLoadEvent. Paper behavior is unchanged.
- Local build passed on Java 21 with 183 tests. GitHub Actions run [39052453490](https://github.com/CyoriaSMP-Team/Multiverse-Core-Folia/actions/runs/39052453490) passed for commit 7e5a707687646c7cf3fd0cb99591cb2a050eb750 and published artifact folia-1.21.11-17.
- The published JAR SHA-256 is e115f49a455391949dcf2aae5c3af4f86f075e61d9588e0e168730a74fd31d4c.

## Current empty-server runtime check (2026-10-18)

- CyoriaSMP Folia 1.21.11 build 14 is online on Java 25, with 0 of 50 players; the latest server start count is 31.
- The Multiverse artifact enabled without the earlier global-region settings error or the unsupported CraftServer.createWorld stack trace. The expected warning says that Folia autoload is skipped while the plugin waits for an external loader.
- The installed MoreFoWorld 1.13.3 helper logged that it added lobby. mv list then showed lobby, world, world_nether, and world_the_end. FancyNpcs enabled and npc list showed all 19 saved lobby NPCs; one separate modeltest NPC is in world.
- No FancyNpcs missing-world or NPC-load errors appeared after lobby loaded.
- A RandomTeleportation component logged that it could not find lobby before MoreFoWorld added it. After checking that the server was still empty, cmi reload returned success at 00:21:49 and CMI logged that configs and locale files reloaded. No later missing-lobby warning appeared, but actual RTP behavior and the next cold boot remain unverified.
- The plugin configs config.yml, worlds.yml, and anchors.yml remained byte-identical to their pre-test copies in the previous live comparison. The previous Multiverse JAR is retained under /srv/cytech/data/minecraft/plugins/_rollback/Multiverse-Core-before-autoload-guard-20261018/.

MoreFoWorld describes itself as a [temporary world manager](https://github.com/Folia-Inquisitors/MoreFoWorld/blob/master/README.md) and documents its unsupported behavior as hacks. The helper relies on server internals to add a world. Folia still lists Bukkit world loading and unloading as unsupported in its [broken API list](https://github.com/PaperMC/Folia#current-broken-api). This live result establishes that the existing lobby and its NPCs were restored for this boot; it does not make the helper a production-safe world lifecycle API.

## Remaining risks

- Multiverse cannot load or unload worlds through Folia's Bukkit API. Its autoload guard avoids the crash; MoreFoWorld currently provides the external lobby load.
- WorldManager load, unload, import, remove, regenerate, delete, Bukkit.getWorld(s), world config changes, and plugin event dispatch still need an ownership-safe design that preserves result APIs and event ordering.
- Portal and teleport paths still need an ownership audit for destination reads and plugin events that happen before asynchronous teleport completion. Passenger operations can span multiple entity owners.
- CommandQueueManager still uses Bukkit.getScheduler() and a shared non-concurrent map. Expiry and confirmation need to run on the sender's owner, with console and block senders handled separately.
- Async completions, listeners, command completion, custom generator callbacks, and multi-region interactions still need a complete ownership audit and runtime verification.
- Startup consumers may initialize before MoreFoWorld loads lobby. The RandomTeleportation warning above is one observed example.
- No player login, gameplay, portal, teleport, or multi-region interaction was tested.

## Validation

Local validation: ./gradlew build passed on Java 21 with 183 tests and no failures. The focused FoliaSchedulerTest also passed.

GitHub Actions command: ./gradlew build

The draft pull request remains open at https://github.com/CyoriaSMP-Team/Multiverse-Core-Folia/pull/1. Keep it as a draft until world lifecycle and the remaining ownership paths have a safe implementation and runtime coverage.
