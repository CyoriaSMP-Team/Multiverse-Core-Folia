# Folia 1.21.11 port status

**Status: experimental live testing.** The fork enables Folia metadata for the requested iterative test cycle. This is not a claim that every ownership path is complete or safe.

## Verified source changes

- Added FoliaScheduler for entity-owned work and server-wide deferred work; it uses Paper API schedulers and retains Paper compatibility.
- Routed passenger remounting, post-teleport velocity, delayed player game-mode and flight enforcement, and world-tick deferrals through Folia schedulers.
- Routed world-level settings and spawn-category settings through the global region scheduler.
- Preserved saved generator settings when WorldManager recreates a world during load and added a regression test.
- Added a Folia autoload guard: if an autoloaded world is not already present, Multiverse skips Bukkit world creation and waits for an external WorldLoadEvent. Paper behavior is unchanged.
- Local build passed on Java 21 with 183 tests. Commit `4cc9e240` also passed GitHub Actions build and PR test workflows; experimental release [folia-1.21.11-18](https://github.com/CyoriaSMP-Team/Multiverse-Core-Folia/releases/tag/folia-1.21.11-18) contains the tested artifact (SHA-256 `b6f1f5b4c2e8623a1ef028f69f77b423a2a5ceeb70cfa0bef9fce66097058b7b`).

## Empty-server runtime checks (2026-10-18)

- CyoriaSMP Folia 1.21.11 build 14 is online on Java 25, with 0 of 50 players; the latest server start count is 58.
- MoreFoWorld 1.13.4 loaded before CMI and logged that it added lobby. The final `mv list` showed only lobby, world, world_nether, and world_the_end; the Multiverse configuration was restored byte-for-byte from its original backup.
- `/mv create folia-api-smoke-20261018-v2 normal` succeeded using the public `MoreFoWorld.createWorld(WorldCreator)` integration. A synchronous NMS operation stalled Folia's global region past the 5-second watchdog reporting threshold.
- A copied disposable world passed `/mv import`; after a cold boot it appeared as `UNLOADED`, and `/mv load` completed about 37 seconds later. `mv list` then showed it as `NORMAL`. The test world and WorldGuard data were moved into rollback after the test.
- FancyNpcs previously listed 19 lobby NPCs and the separate modeltest NPC in world; no missing-world or NPC-load errors appeared after lobby loaded.
- The preceding boot logged that RandomTeleportation could not find lobby. cmi reload then succeeded at 00:21:49, and the new MoreFoWorld-before-CMI order prevented the warning on the subsequent cold boot. Actual RTP behavior remains unverified.
- The active Multiverse JAR matches release 18. Original `worlds.yml` is preserved in `.plugin-rollback/multiverse-morefo-folia-world-api-20261018/Multiverse-Core-data/`; post-smoke YAML and copied smoke worlds remain in that rollback directory.

MoreFoWorld describes itself as a [temporary world manager](https://github.com/Folia-Inquisitors/MoreFoWorld/blob/master/README.md) and documents its unsupported behavior as hacks. The helper relies on server internals to add a world. Folia still lists Bukkit world loading and unloading as unsupported in its [broken API list](https://github.com/PaperMC/Folia#current-broken-api). This live result establishes that the existing lobby and its NPCs were restored for this boot; it does not make the helper a production-safe world lifecycle API.

## Remaining risks

- Folia's Bukkit world load/unload APIs remain unsupported. The live create/import/load checks pass only through the MoreFoWorld internal helper on empty disposable worlds; the create call exceeded the watchdog threshold and import/load took about 37 seconds. Do not create or load batches of worlds while players are online.
- WorldManager unload, remove, regenerate, delete, `Bukkit.getWorld(s)`, world config changes, and plugin event dispatch still need an ownership-safe design that preserves result APIs and event ordering.
- Portal and teleport paths still need an ownership audit for destination reads and plugin events that happen before asynchronous teleport completion. Passenger operations can span multiple entity owners.
- CommandQueueManager still uses Bukkit.getScheduler() and a shared non-concurrent map. Expiry and confirmation need to run on the sender's owner, with console and block senders handled separately.
- Async completions, listeners, command completion, custom generator callbacks, and multi-region interactions still need a complete ownership audit and runtime verification.
- Other startup consumers may still initialize before MoreFoWorld loads lobby; only CMI ordering and this startup path were checked.
- No player login, gameplay, actual RTP, portal, teleport, or multi-region interaction was tested.

## Validation

Local validation: `./gradlew build` passed on Java 21 with 183 tests and no failures. The focused FoliaSchedulerTest also passed.

GitHub Actions command: ./gradlew build

The draft pull request remains open at https://github.com/CyoriaSMP-Team/Multiverse-Core-Folia/pull/1. Keep it as a draft until world lifecycle and the remaining ownership paths have a safe implementation and runtime coverage.
