# Folia 1.21.11 port status

**Status: experimental live testing.** `plugin.yml` sets `folia-supported: true` so Folia can load
the fork for the requested iterative test-and-fix cycle. This is not a claim that every ownership
path below is complete or safe.

## Verified source changes

- [x] Added `FoliaScheduler`, using the entity scheduler for entity-owned work and the global
  region scheduler for server-wide deferred work. Both are Paper API schedulers and retain Paper
  compatibility.
- [x] Routed passenger remounting, post-teleport velocity, and delayed player game-mode/flight
  enforcement through the relevant entity scheduler.
- [x] Routed all world-tick deferrals through the global scheduler. Its regen/delete actions still need
  world ownership review before Folia support can be enabled.
- [x] Preserved saved generator settings when `WorldManager` recreates a world during load.
- [x] Added a `WorldManagerTest` regression case for generator-settings retention over unload/load.
- [x] Enabled Folia plugin metadata for iterative runtime testing; the unresolved blockers below remain open.
- [x] GitHub Actions compiled and passed the 182-test suite before the live test.
- [x] Tested the CI artifact on the empty Folia server; it enabled but did not load `lobby`.

## Unresolved blockers

- [ ] Folia 1.21.11's `CraftServer.createWorld(WorldCreator)` unconditionally throws
  `UnsupportedOperationException`; the tested artifact therefore cannot autoload `lobby` through
  Bukkit. Moving the call to the global scheduler cannot fix this platform API gap. World load and
  unload are also listed by Folia as unsupported APIs.
- [ ] Startup config handling also logged `Cannot modify server settings off of the global region`.
  The exact config callback is not yet identified.
- [ ] `WorldManager` load/unload/import/remove/regenerate/delete, `Bukkit.getWorld(s)`, world config
  mutation, and plugin event dispatch still need an ownership-safe design that preserves result
  APIs and event ordering.
- [ ] Portal and teleport paths read destination/world data and fire plugin events before the
  asynchronous teleport completion; those reads and event calls are not comprehensively routed
  to owning schedulers. Passenger handling also spans multiple entity owners.
- [ ] `CommandQueueManager` still uses `Bukkit.getScheduler()` and a shared non-concurrent map.
  Expiry and confirmation must execute against the sender's owner, with console/block sender
  behavior handled separately.
- [ ] Other entity/world API access from async completions, listeners, command completion, and
  custom generator/biome-provider callbacks has not received a complete ownership audit.
- [ ] Player, portal and multi-region runtime behavior is not yet covered. MockBukkit tests cannot
  establish Folia region ownership correctness.

## Validation

No Gradle command was run on the Home Server. The branch is `folia/1.21.11` and uses Paper API
`1.21.11-R0.1-SNAPSHOT` in `build.gradle`.

GitHub Actions compile and test command: `./gradlew build`

Focused test command: `./gradlew test --tests org.mvplugins.multiverse.core.world.WorldManagerTest`

GitHub Actions run `39048491913` passed the compile and 182 tests and published artifact
`folia-1.21.11-9` from commit `2e5eee4bf8c44c0badb7adabacffe8f8b0b9c90d`. Its JAR SHA-256 is
`54a4a11f69087820c5594f371cca8c2a0ffbc72e705836082b8294c5af8da1f0` and is installed at
`/srv/cytech/data/minecraft/plugins/Multiverse-Core.jar` for iterative testing. On 2026-10-17,
Folia logged `UnsupportedOperationException` from `CraftServer.createWorld(WorldCreator)` while
autoloading `lobby`, then logged the global-region config error. FancyNpcs again skipped all 19
saved lobby NPCs. The server returned online with 0 players. `worlds.yml` still matches its
pre-test backup SHA-256, and the lobby world remains 186 MB; no config or world data changed.
