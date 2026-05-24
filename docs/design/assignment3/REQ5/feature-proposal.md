# Feature Proposal - Assignment 3

## REQ3 & REQ4: Creative Mode

> (To be filled by EDWIN & LI SHEN for the two creative requirements.)

---

## REQ5: A.P.I - Toxic Atmosphere (HD Requirement)

### The Pitch

The moon facility has an automated monitor that calls the OpenWeather Air Pollution API using game-state-driven coordinates. The returned AQI and dominant pollutant are converted into changes to health, terrain, economy, and creature behaviour. Severe pollution can also create new hazards and spawn new enemies.

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
  - It derives the latitude/longitude dynamically from the monitor's current map coordinates and converts those coordinates into a bounded city-like search point before issuing the request.
  - This means the exact request changes with the in-game state of the atmospheric monitor rather than using a static URL.

- The raw JSON response is parsed into an `AirQualityReport` by the parser strategy supplied by `AtmosphericServicesFactory`.
  - `OpenWeatherPollutionParser` is the main parser and uses simple string parsing (no JSON library) to extract:
    - `list[0].main.aqi` as an integer AQI index on the OpenWeather **1-5** scale.
    - `list[0].components.no_2` and `list[0].components.so_2` to determine the dominant pollutant (`"no_2"` or `"so_2"`).
  - `FallbackPollutionParser` is selected only when `OPENWEATHER_API_KEY` is absent or blank, ensuring the feature remains executable without exposing secrets while safely disabling atmospheric corruption.

- The resulting `AirQualityReport` is passed to one or more `AtmosphericCorruptor` implementations, which translate air quality into concrete game effects:

  - **HazardCorruptor**
    - Interprets AQI in three tiers on the 1-5 scale:
      - **Tier 1 (mild)**: `aqi <= 2` - no atmospheric effects are applied.
      - **Tier 2 (moderate)**: `aqi == 3` - local damage, poison, and small toxic patches.
      - **Tier 3 (severe)**: `aqi >= 4` - heavy damage, strong poison, and large-scale terrain corruption.
    - For AQI >= 3, it iterates over the map and finds any actor exposing the `AtmosphereSensitiveActor` capability (currently `ContractedWorker`, `Muckraker`, and `Undead`). For each such actor, it calls `applyAtmosphere(report, here)` so the actor can apply its own complex health, terrain, movement, or area effects.
    - At **moderate AQI (3)**, after calling `applyAtmosphere` on an atmosphere-sensitive actor, HazardCorruptor also creates small **toxic puddles** around that actor:
      - It examines the neighbouring tiles around the actor using the engine's `Exit` API.
      - On neighbouring floor-like tiles, there is a 25% chance per tile to replace the ground with `ToxicWaste`, forming a local contaminated cluster rather than corrupting the entire map.
    - At **severe AQI (4-5)**, HazardCorruptor applies heavy health and status effects via `applyAtmosphere`, and then performs two large-scale terrain mutations:
      - **Border ring**: it walks the outer border coordinates of the `GameMap` (top row, bottom row, left and right columns) and replaces the ground with `ToxicWaste`, producing a visible toxic perimeter around the facility.
      - **Monitor hotspot**: it locates the atmospheric anchor near the monitor and, for all tiles within Manhattan distance 2 of that anchor, randomly (50% chance) converts walkable empty tiles into `ToxicWaste`. This makes the probe itself feel like a pollution hotspot.

  - **EconomyCorruptor**
    - Interprets sulphur dioxide (SO_2) as a proxy for economic disruption.
    - When the dominant pollutant in `AirQualityReport` is `"so_2"`, it:
      - Sets a global disruption flag `EconomyCorruptor.ECONOMY_DISRUPTED` to `true`.
      - Iterates over all tiles in the `GameMap` and, for any actor that tracks `EclipseStatistics.CREDITS`, reduces that statistic by 10 (floored at zero) using the existing statistics API.
      - If SO_2 is not dominant, the flag is reset to `false` and no credit erosion occurs.
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

For REQ5, the feature is structured around **two primary abstractions that are counted for complexity** and one supporting parser abstraction used for API translation.

- **AtmosphericCorruptor** (interface)
  - Responsibility: apply a distinct world-level corruption effect to a `GameMap` based on an `AirQualityReport`.
  - Key methods:
    - `corrupt(map : GameMap, report : AirQualityReport) : void`
  - Counted REQ5 implementations:
    - `HazardCorruptor`
    - `EconomyCorruptor`
    - `PollutantSpawnCorruptor`

- **AtmosphereSensitiveActor** (interface)
  - Responsibility: allow actors to define their own actor-specific response to atmospheric conditions.
  - Key methods:
    - `applyAtmosphere(report : AirQualityReport, here : Location) : void`
  - Counted REQ5 implementations:
    - `ContractedWorker`
    - `Muckraker`
    - `Undead`

- **PollutionDataParser** (interface, supporting infrastructure)
  - Responsibility: parse a raw JSON string into an `AirQualityReport`.
  - Key methods:
    - `parse(json : String) : AirQualityReport`

#### Concrete Classes

| Class                         | Type     | Implements                              | Summary                                                                                                                                                     |
|-------------------------------|----------|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| OpenWeatherPollutionParser    | New      | PollutionDataParser                     | Parses real OpenWeather Air Pollution JSON (string ops only) into an `AirQualityReport` containing the AQI tier and dominant pollutant.                    |
| FallbackPollutionParser       | New      | PollutionDataParser                     | Safe-fallback parser used when the API key is absent or the network fails; always returns AQI 1 so no atmospheric effects are applied without real data.    |
| AirQualityReport              | New      | (value object)                          | Immutable data class that stores the parsed AQI (1-5) and dominant pollutant string.                                                                        |
| AtmosphericApiClient          | New      | -                                       | Calls the external API with a lat/lon pair derived from the monitor's game-state coordinates and returns the raw JSON string.                                  |
| AtmosphericServicesFactory    | New      | -                                       | Factory that wires together the parser, client, and all `AtmosphericCorruptor`s so higher-level classes never depend on concrete types.                      |
| AtmosphericMonitor            | New      | EclipseActor, AtmosphericAnchor         | Stationary probe actor; implements the `AtmosphericAnchor` marker so corruptors can locate it on the map through capability-based lookup without downcasting. |
| EnvironmentalMonitorBehaviour | New      | Behaviour\<Actor, Action\>            | Ticks every turn and periodically returns an `AtmosphericScanAction` to trigger the API pipeline.                                                           |
| AtmosphericScanAction         | New      | Action                                  | Fetches JSON, parses it, prints a tiered AQI summary, and invokes all registered `AtmosphericCorruptor`s.                                                   |
| HazardCorruptor               | New      | AtmosphericCorruptor                    | Calls `applyAtmosphere` on all `AtmosphereSensitiveActor`s; at moderate AQI spreads local toxic puddles; at severe AQI creates a border ring and monitor hotspot. |
| EconomyCorruptor              | New      | AtmosphericCorruptor                    | SO_2-driven: reduces credits for all tracked actors and toggles a global disruption flag consumed by `SellAction` to probabilistically void transactions.     |
| PollutantSpawnCorruptor       | New      | AtmosphericCorruptor                    | At severe AQI, locates the `AtmosphericAnchor` via `getActorAs` (no downcast) and spawns one `Undead` on a valid adjacent empty tile.               |
| ContractedWorker              | Existing | AtmosphereSensitiveActor                | Worker that converts AQI into HP loss and `PoisonStatus` (moderate: 2 turns / 1 dmg; severe: 3 turns / 2 dmg per turn).                                    |
| Muckraker                     | Existing | AtmosphereSensitiveActor                | Scavenger that leaks `ToxicWaste` onto its tile at moderate AQI and also shoves adjacent actors using `map.moveActor` at severe AQI.                        |
| Undead                        | Existing | AtmosphereSensitiveActor                | Corrupts its tile with `ToxicWaste` at moderate AQI; at severe AQI additionally radiates 1-damage AoE toxic energy to every actor on adjacent tiles.        |

---
