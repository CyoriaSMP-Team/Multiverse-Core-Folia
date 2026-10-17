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
- [ ] Run tests and compile. This host is not to run Gradle; use GitHub Actions.

## Unresolved blockers

- [ ] `WorldManager` operations are synchronous and use Bukkit world/global APIs directly:
  `createWorld`, load/unload/import/remove/regenerate/delete, `Bukkit.getWorld(s)`, world config
  mutation, and plugin event dispatch. Callers include enable-time `initAllWorlds` and commands.
  They need an ownership-safe design that preserves their result APIs and event ordering.
- [ ] Startup world import/autoload executes synchronously during `onEnable`; startup behavior
  and any deferred Folia startup lifecycle need end-to-end validation.
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

Static inspection only; no Gradle command was run on the Home Server. The branch is
`folia/1.21.11` and uses Paper API `1.21.11-R0.1-SNAPSHOT` in `build.gradle`.

GitHub Actions compile and test command: `./gradlew build`

Focused test command: `./gradlew test --tests org.mvplugins.multiverse.core.world.WorldManagerTest`

GitHub Actions run `39040848405` passed the compile and 182 tests and published artifact
`folia-1.21.11-7` from commit `1f8598aac071d60f29794766c191bace016f50bf`. The Home Server must not run
Gradle; use that CI for builds. Runtime checks are being performed iteratively on CyoriaSMP with no
players online. A successful startup does not close the ownership blockers above.
