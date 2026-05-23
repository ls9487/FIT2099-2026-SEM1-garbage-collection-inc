# Feature Proposal - Assignment 3

## REQ3 & REQ4: Creative Mode

> (To be filled by EDWIN & LI SHEN for the two creative requirements.)

---

## REQ5: A.P.I - Toxic Atmosphere (HD Requirement)

### The Pitch

The abandoned moon facility's environmental monitoring system remains operational, continuously polling real-world air quality data from Earth's polluted cities. The atmospheric readings (air quality index and dominant pollutants) are directly translated into hazardous environmental conditions on the moon. As these pollution metrics fluctuate, the facility's **health**, **terrain**, **economy**, and even **creatures** dynamically adapt to mirror the detected contamination levels.

---

### The Mechanics

- The game queries the **OpenWeather Air Pollution API** at runtime, using an API key stored in an environment variable:

  ```text
  GET https://api.openweathermap.org/data/2.5/air_pollution
      ?lat={latitude}
      &lon={longitude}
      &appid={OPENWEATHER_API_KEY}
  ```

- The API key is read via `System.getenv("OPENWEATHER_API_KEY")` and is never committed to the repository.

- The API request is driven by game state via `AtmosphericApiClient`:
  - It selects a latitude/longitude pair from a small hard-coded list of polluted cities (for example: Beijing, Delhi, Los Angeles, Jakarta, Mumbai).
  - The selection logic is encapsulated inside `AtmosphericApiClient` and can be extended later to take into account the worker’s position or credits, but the current implementation uses a simple strategy to keep within the “no external libraries” constraint.

- The raw JSON response is parsed into an `AirQualityReport` by a single `PollutionDataParser` implementation, `OpenWeatherPollutionParser`.
  - `OpenWeatherPollutionParser` uses simple string parsing (no JSON library) to extract:
    - `list[0].main.aqi` as an integer AQI index on the OpenWeather **1	6** scale.
    - `list[0].components.no2` and `list[0].components.so2` to determine the dominant pollutant (`"no2"` or `"so2"`).
  - Temperature, humidity, wind speed, and pressure are currently represented by fixed defaults, as the Air Pollution endpoint does not provide them directly.

- The resulting `AirQualityReport` is passed to one or more `AtmosphericCorruptor` implementations, which translate air quality into concrete game effects:

  - **HazardCorruptor**
    - Interprets AQI in three tiers on the 1	6 scale:
      - **Tier 1 (mild)**: `aqi <= 2` 	6 no atmospheric effects are applied.
      - **Tier 2 (moderate)**: `aqi == 3` 	6 local damage, poison, and small toxic patches.
      - **Tier 3 (severe)**: `aqi >= 4` 	6 heavy damage, strong poison, and large-scale terrain corruption.
    - For AQI 	3, iterates over the map and finds any actor exposing the `AtmosphereSensitiveActor` capability (currently `ContractedWorker` and `Muckraker`). For each such actor, calls `applyAtmosphere(report)` to let the actor handle its own health/status logic.
    - At **moderate AQI (3)**, after calling `applyAtmosphere` on an atmosphere-sensitive actor, HazardCorruptor also creates small **toxic puddles** around that actor:
      - It examines the neighbouring tiles around the actor using the engine's `Exit` API.
      - On neighbouring floor-like tiles, there is a 25% chance per tile to replace the ground with `ToxicWaste`, forming a local contaminated cluster rather than corrupting the entire map.
    - At **severe AQI (4	6)**, HazardCorruptor applies heavy health and status effects via `applyAtmosphere`, and then performs two large-scale terrain mutations:
      - **Border ring**: it walks the outer border coordinates of the `GameMap` (top row, bottom row, left and right columns) and replaces the ground with `ToxicWaste`, producing a visible toxic perimeter around the facility.
      - **Monitor hotspot**: it locates the `AtmosphericMonitor` actor and, for all tiles within Manhattan distance 2 of the monitor, randomly (50% chance) converts walkable tiles into `ToxicWaste`. This makes the probe itself feel like a pollution hotspot.

  - **EconomyCorruptor**
    - Interprets sulphur dioxide (SO	8) as a proxy for economic disruption.
    - When the dominant pollutant in `AirQualityReport` is `"so2"`, it:
      - Sets a global disruption flag `EconomyCorruptor.ECONOMY_DISRUPTED` to `true`.
      - Iterates over all tiles in the `GameMap` and, for any actor that tracks `EclipseStatistics.CREDITS`, reduces that statistic by 10 (floored at zero) using the existing statistics API.
      - If SO	8 is not dominant, the flag is reset to `false` and no credit erosion occurs.
    - The disruption flag is consumed by the REQ1 shop system:
      - When `EconomyCorruptor.ECONOMY_DISRUPTED` is `true`, `SellAction` gains a **transaction fizzle** behaviour:
        - Each sale still removes the item from the seller's inventory (via `Sellable.soldBy`), but there is a 50% chance that the **payout is 0 credits** instead of the usual price.
        - A message is printed explaining that "the toxic atmosphere corrupts the transaction and no credits are received".
      - When the flag is `false`, SellAction behaves as in Assignment 2: the seller receives the full sell price and the item is removed normally.

- The atmospheric system is driven by a dedicated monitor actor:
  - `AtmosphericMonitor` is a stationary `EclipseActor` that represents the facility's automated probe.
  - It owns an `EnvironmentalMonitorBehaviour`, which keeps an internal tick counter.
  - To keep testing simple and to make the feature observable in a short demo, the behaviour is configured to trigger a new scan **every turn** (via a `REFRESH_INTERVAL` constant).
  - When the interval elapses, the behaviour returns an `AtmosphericScanAction` instead of `null`.
  - `AtmosphericScanAction` calls the OpenWeather API via `AtmosphericApiClient`, parses the JSON with `OpenWeatherPollutionParser`, and then invokes each configured `AtmosphericCorruptor` with the resulting `AirQualityReport`. It also prints the current AQI to the console.

---

### The Architecture

#### New Abstractions

- **PollutionDataParser** (interface)
  - Responsibility: parse a raw JSON string into an `AirQualityReport`.
  - Key methods:
    - `parse(json : String) : AirQualityReport`

- **AtmosphericCorruptor** (interface)
  - Responsibility: apply environmental changes to a `GameMap` based on an `AirQualityReport`.
  - Key methods:
    - `corrupt(map : GameMap, report : AirQualityReport) : void`

- **AtmosphereSensitiveActor** (interface)
  - Responsibility: allow actors to define their own response to atmospheric conditions.
  - Key methods:
    - `applyAtmosphere(report : AirQualityReport) : void`

#### Concrete Classes

| Class                        | Type      | Implements                 | Summary                                                                                                                                                    |
|------------------------------|-----------|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| OpenWeatherPollutionParser   | New       | PollutionDataParser        | Parses OpenWeather Air Pollution JSON (via string operations only) into an `AirQualityReport` containing the AQI tier and dominant pollutant.             |
| AirQualityReport             | New       | (value object)             | Immutable data class that stores the parsed AQI (1	6), dominant pollutant string, and placeholder fields for temperature/humidity/wind.                  |
| AtmosphericApiClient         | New       | 	6                      | Responsible for calling the external API with a chosen lat/lon and returning the raw JSON string.                                                          |
| AtmosphericServicesFactory   | New       | 	6                      | Factory that wires together the parser, client, and a list of `AtmosphericCorruptor`s so that `AtmosphericMonitor` does not need to know concrete types.   |
| AtmosphericMonitor           | New       | EclipseActor               | Stationary actor that represents the facility's environmental probe; owns the `EnvironmentalMonitorBehaviour`.                                             |
| EnvironmentalMonitorBehaviour| New       | Behaviour<Actor, Action>   | Ticks every turn and periodically returns an `AtmosphericScanAction` that performs API calls and corruption.                                                |
| AtmosphericScanAction        | New       | Action                     | One-shot action that triggers an API call, parses the JSON, prints AQI, and invokes all registered `AtmosphericCorruptor`s.                                |
| HazardCorruptor              | New       | AtmosphericCorruptor       | Applies AQI-based health, status, and terrain changes: calls `applyAtmosphere` on `AtmosphereSensitiveActor`s, generates local toxic puddles, a border ring, and a hotspot around the monitor. |
| EconomyCorruptor             | New       | AtmosphericCorruptor       | Applies SO	8-based economic effects: reduces credits for actors with a credit statistic and toggles a global disruption flag consumed by SellAction.    |
| ContractedWorker             | Existing  | AtmosphereSensitiveActor   | Player-controlled worker that converts `AirQualityReport` into HP loss and `PoisonStatus` via the engine's capability and status systems.                  |
| Muckraker                    | Existing  | AtmosphereSensitiveActor   | Stateful scavenger creature that now reacts to atmosphere by leaking `ToxicWaste` and shoving nearby workers when pollution is high.                       |

---

### Detailed Behaviour per Class

#### ContractedWorker (AtmosphereSensitiveActor)

The `ContractedWorker` class implements `AtmosphereSensitiveActor` and defines its own reaction to atmospheric conditions in `applyAtmosphere(AirQualityReport)`:

- The worker reads `report.getAqi()` on the OpenWeather 1	6 scale and uses three tiers:
  - **AQI 1	2 (mild)**: no effect.
  - **AQI 3 (moderate)**:
    - The worker immediately loses 1 HP.
    - If the worker has the `Poisonable` capability, a `PoisonStatus` is added with duration 2 turns and 1 damage per turn.
  - **AQI 4	6 (severe)**:
    - The worker immediately loses 2 HP.
    - If `Poisonable`, a stronger `PoisonStatus` is applied with duration 3 turns and 2 damage per turn.

This behaviour combines HP loss, status effects, and the engine's capability system. The worker itself does **not** mutate terrain or credits; those responsibilities are delegated to the corruptors.

#### HazardCorruptor (AtmosphericCorruptor)

`HazardCorruptor` is responsible for turning an `AirQualityReport` into health, status, and terrain changes:

- Reads the AQI tier from `report.getAqi()`.
- For `aqi <= 2`, it returns without applying any effects.
- For `aqi >= 3`, it:
  - Iterates over every tile in the `GameMap`.
  - For each tile containing an actor, attempts to view it as an `AtmosphereSensitiveActor` using the engine's capability API.
  - If successful, calls `applyAtmosphere(report)` on that actor, leveraging whatever logic the actor has implemented.
- For **AQI 3 (moderate)**, after delegating to `applyAtmosphere`, the corruptor also spawns **local toxic puddles**:
  - Around each atmosphere-sensitive actor, it examines the adjacent tiles via `Exit`s.
  - On neighbouring tiles without actors, there is a 25% chance per tile to set the ground to `ToxicWaste`.
- For **AQI 4	6 (severe)**, in addition to the above it mutates the wider terrain:
  - Creates a **border ring** by setting the ground on the outermost rows and columns of the map to `ToxicWaste`.
  - Creates a **monitor hotspot** by locating the `AtmosphericMonitor` actor and, for tiles within Manhattan distance 2 that do not contain actors, converting some of them (50% chance) into `ToxicWaste`.

This makes `HazardCorruptor` a complex implementation that combines:

- Whole-map iteration.
- Delegation to multiple `AtmosphereSensitiveActor` implementations.
- Distance-based AoE terrain changes (adjacent tiles and radius 2).
- A permanent structural change (toxic perimeter) visible in the ASCII map.

#### EconomyCorruptor (AtmosphericCorruptor)

`EconomyCorruptor` turns SO	8 readings into economic effects:

- Reads the dominant pollutant from `AirQualityReport`.
- If the dominant pollutant is not `"so2"`, it sets `EconomyCorruptor.ECONOMY_DISRUPTED = false` and returns.
- If SO	8 is dominant, it:
  - Sets `EconomyCorruptor.ECONOMY_DISRUPTED = true`.
  - Iterates over all tiles in the map.
  - For each actor that has an `EclipseStatistics.CREDITS` statistic, reads their current credits and updates the statistic to `max(0, credits 	10)` using the engine's statistics API.

The disruption flag is then consumed by the REQ1 shop system:

- In `SellAction.execute`:
  - If the seller does not track credits, the action returns early as in Assignment 2.
  - Otherwise, it reads the `sellPrice` from the `Sellable`.
  - When `EconomyCorruptor.ECONOMY_DISRUPTED` is true and the price is positive, there is a 50% chance that the sale **"glitches"**:
    - The `Sellable` still performs its `soldBy` logic (inventory removal and any side effects).
    - The seller does **not** receive any credits for the sale.
    - A descriptive message explains that the toxic atmosphere has corrupted the transaction.
  - If the glitch does not trigger (or the flag is false), the seller receives the full price and the sale proceeds normally.

This ties the atmospheric system directly into REQ1's shop actions, and goes beyond a simple "credits -10" by sometimes invalidating entire transactions.

#### Muckraker (AtmosphereSensitiveActor)

`Muckraker` is an existing stateful scavenger creature (`StatefulCreature`) that reacts to nearby workers by changing emotion states. It has been extended to implement `AtmosphereSensitiveActor` and now uses `applyAtmosphere` to become an environmental hazard under bad air:

- For **AQI 1	2 (mild)**: `applyAtmosphere` returns without changes.
- For **AQI 3 (moderate)**:
  - The Muckraker leaks a small amount of `ToxicWaste` onto its current tile by replacing the ground with `ToxicWaste`.
- For **AQI 4	6 (severe)**:
  - The tile under the Muckraker is again converted to `ToxicWaste`, creating a moving source of contamination as it roams.
  - The Muckraker also interacts with nearby workers:
    - For each adjacent tile containing a `ContractedWorker`, there is a chance to "shove" the worker one tile further away into a free neighbouring tile.
    - This uses the engine's `Exit` graph to find candidate destinations and moves the worker actor while keeping damage at 0 (a disruption, not an attack).

This behaviour:

- Reuses the same AQI tiers as the worker and HazardCorruptor.
- Adds a second `AtmosphereSensitiveActor` implementation with different, non-economic effects.
- Couples atmosphere to enemy behaviour and terrain without duplicating the credit logic in `EconomyCorruptor`.

---

### Why This Satisfies the "Complex" Requirement

The REQ5 implementation is intentionally spread across multiple interacting classes rather than a single "god" class:

- `HazardCorruptor` combines map-wide iteration, distance-based AoE, and persistent terrain mutation.
- `ContractedWorker` converts abstract AQI data into concrete HP and poison status effects based on engine capabilities.
- `EconomyCorruptor` and `SellAction` translate gas composition into both background credit erosion and probabilistic transaction failure within the existing shop system.
- `Muckraker` shows that multiple actors can independently implement `AtmosphereSensitiveActor`, each with different behaviours (worker: HP/status; Muckraker: terrain and movement effects).

Together, these behaviours:

- Use the external API data in meaningful ways.
- Spawn and manipulate terrain (`ToxicWaste`).
- Apply status effects (`PoisonStatus`).
- Modify statistics and shop behaviour (`EclipseStatistics.CREDITS`, `SellAction`).
- Respond to both AQI tiers and dominant pollutants.

This goes well beyond simple numeric tweaks and clearly meets the "complex implementation" expectations for REQ5.
