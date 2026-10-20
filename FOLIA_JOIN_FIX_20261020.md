# CyoriaSMP Folia join-destination: world-mismatch workaround (2026-10-20)

## Issue

Folia 1.21.11 `PlayerJoinEvent` from `world_the_end` can initiate Multiverse's configured `join-destination: lobby`. `MVPlayerListener.teleportToDestinationOnJoin()` calls `AsyncSafetyTeleporterAction.doSafetyCheck()`, synchronously inspecting `lobby` blocks from the original `world_the_end` region thread. Folia rejects the access:

`IllegalStateException: World mismatch: expected lobby but got world_the_end`

## Scoped change

For Multiverse's **first-spawn and per-join destination only**, set `checkSafety(false)` on Folia, preserving the async teleport and `join-destination: lobby`. Paper retains its normal safety check. Normal `/mvtp`, `/mvspawn` and other teleport safety operations remain unchanged. The lobby spawn **must be configured at known safe coordinates**; this workaround does not validate the destination's collision or surrounding blocks.

## Validation so far

- A bytecode-equivalent, one-method patch to the existing `Multiverse-Core vlocal` artifact (SHA-256 after patch `6ef7e43d7068daa0463bf5179924ec70e56530e498c06ca06a8a55e5ddd5ec73`) booted in a disposable Folia 1.21.11 server without startup errors; production original JAR/config backed up before deployment.
- Production `plugins/Multiverse-Core/config.yml`: `first-spawn-location: lobby`, `enable-join-destination: true`, `join-destination: lobby` unchanged.
- Full cross-world **real-player login regression not verified** at initial deployment. Inspect logs when a player joins from `world_the_end` and verify arrival in `lobby` without block ownership exceptions or suffocation.
- Folia source branch still has broader world-management ownership issues documented in `FOLIA_PORT_STATUS.md` and remains experimental. Do NOT merge without verifying all critical code paths.

## Follow-up design

Move destination safety checks to the destination's owning region scheduler, using async result composition before teleport; then reenable checks instead of permanently bypassing them. This is the correct long-term solution.
