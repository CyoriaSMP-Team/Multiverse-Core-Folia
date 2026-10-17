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
- [x] GitHub Actions compiled and passed the 183-test suite.
- [x] Tested the CI artifact on the empty Folia server; it enables, applies global config changes
  without the previous region error, and still cannot load `lobby`.

## Unresolved blockers

- [ ] Folia 1.21.11's `CraftServer.createWorld(WorldCreator)` unconditionally throws
  `UnsupportedOperationException`; the tested artifact therefore cannot autoload `lobby` through
  Bukkit. Moving the call to the global scheduler cannot fix this platform API gap. World load and
  unload are also listed by Folia as unsupported APIs.
- [x] World-level config setters and spawn-category settings now use Folia's global region scheduler;
  the latest startup no longer logged `Cannot modify server settings off of the global region`.
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

Local validation: `./gradlew build` passed on Java 21 with 183 tests and no failures. The focused
`FoliaSchedulerTest` also passed.

GitHub Actions compile and test command: `./gradlew build`

Focused test command: `./gradlew test --tests org.mvplugins.multiverse.core.utils.FoliaSchedulerTest`

GitHub Actions run `39051060389` passed the build and all 183 tests for commit
`d8ed8535afad2801519abf3c06826c72e84d9b5b` and published artifact `folia-1.21.11-12`. Its JAR
SHA-256 is `cfe4362e18cc3dd8bb1df2881d9665eb8c0b6a2f7babc7751eb52ed08a3d0aba`; it is installed at
`/srv/cytech/data/minecraft/plugins/Multiverse-Core.jar` on CyoriaSMP Folia 1.21.11 build 14.
After restart the server returned online with 0 players, and Multiverse enabled. Its `mv list`
command still reports `lobby - UNLOADED`; startup still logs `UnsupportedOperationException` from
`CraftServer.createWorld(WorldCreator)`, so FancyNpcs skips all 19 saved lobby NPCs. The global
region config error is absent. `config.yml`, `worlds.yml`, and `anchors.yml` are byte-identical to
the pre-restart copies; the 186 MB lobby world is identical to its rollback snapshot. The prior
JAR (`54a4a11f69087820c5594f371cca8c2a0ffbc72e705836082b8294c5af8da1f0`) is retained at
`/srv/cytech/data/minecraft/plugins/_rollback/Multiverse-Core-before-global-region-20261017/Multiverse-Core.jar`.
