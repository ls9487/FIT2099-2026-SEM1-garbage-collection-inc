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
  - `OpenWeatherPollutionParser` is the main parser and extracts:
    - `list[0].main.aqi` as an integer AQI index on the OpenWeather **1-5** scale.
    - `list[0].components.no_2` and `list[0].components.so_2` to determine the dominant pollutant (`"no_2"` or `"so_2"`).
  - `FallbackPollutionParser` is selected only when `OPENWEATHER_API_KEY` is absent or blank, ensuring the feature remains executable without exposing secrets while safely disabling atmospheric corruption.

- The resulting `AirQualityReport` is passed to one or more `AtmosphericCorruptor` implementations, which translate air quality into concrete game effects. These effects fall into two categories:
  - **Probability-based world effects**: random map-level effects whose chance is explicitly encoded in the code, such as local toxic spread, hotspot corruption, and disrupted shop payouts.
  - **Guaranteed actor effects**: deterministic reactions that always happen once an actor is processed at the relevant AQI tier, such as direct HP loss, poison, tile corruption, or adjacent damage applied by `AtmosphereSensitiveActor` implementations.

- **HazardCorruptor**
  - Interprets AQI in three tiers on the 1-5 scale:
    - **Tier 1 (mild)**: `aqi <= 2` - no atmospheric effects are applied.
    - **Tier 2 (moderate)**: `aqi == 3` - guaranteed actor-specific effects plus small probabilistic toxic patches.
    - **Tier 3 (severe)**: `aqi >= 4` - guaranteed heavier actor-specific effects plus large-scale terrain corruption.
  - For AQI >= 3, it iterates over the map and finds any actor exposing the `AtmosphereSensitiveActor` capability (currently `ContractedWorker`, `Muckraker`, and `Undead`). For each such actor, it calls `applyAtmosphere(report, here)` so the actor can apply its own guaranteed health, terrain, movement, or area effects according to its own implementation.
  - At **moderate AQI (3)**, after calling `applyAtmosphere` on an atmosphere-sensitive actor, `HazardCorruptor` also creates small **toxic puddles** around that actor:
    - It examines the neighbouring tiles around the actor using the engine's `Exit` API.
    - On neighbouring eligible empty tiles, there is a **25% chance per tile** to replace the ground with `ToxicWaste`, forming a local contaminated cluster rather than corrupting the entire map.
  - At **severe AQI (4-5)**, `HazardCorruptor` applies heavier guaranteed actor-specific effects via `applyAtmosphere`, and then performs two large-scale terrain mutations:
    - **Border ring**: it walks the outer border coordinates of the `GameMap` and replaces the ground with `ToxicWaste`, producing a visible toxic perimeter around the facility.
    - **Monitor hotspot**: it locates the atmospheric anchor near the monitor and, for all eligible empty tiles within Manhattan distance 2 of that anchor, applies a **50% chance per tile** to convert the ground into `ToxicWaste`.

- **EconomyCorruptor**
  - Interprets sulphur dioxide (`SO_2`) as a proxy for economic disruption.
  - When the dominant pollutant in `AirQualityReport` is `"so_2"`, it:
    - Sets a global disruption flag `EconomyCorruptor.ECONOMY_DISRUPTED` to `true`.
    - Iterates over all tiles in the `GameMap` and, for any actor that tracks `EclipseStatistics.CREDITS`, reduces that statistic by 10 with a floor at 0.
  - The disruption flag is consumed by the REQ1 shop system:
    - When `EconomyCorruptor.ECONOMY_DISRUPTED` is `true`, `SellAction` still removes the item from the seller's inventory, but there is a **50% chance** that the **payout is 0 credits** instead of the normal price.
    - When the flag is `false`, `SellAction` behaves normally.

- **PollutantSpawnCorruptor**
  - Reacts to severe AQI by locating the `AtmosphericAnchor` and spawning one `Undead` on a randomly selected valid adjacent tile.

- The atmospheric system is driven by a dedicated monitor ground:
  - `AtmosphericMonitor` is a stationary `Ground` that represents the facility's automated probe and also acts as an `AtmosphericAnchor`.
  - It owns an `EnvironmentalMonitorBehaviour`, which keeps an internal tick counter.
  - To keep testing simple and make the feature observable in a short demo, the behaviour is configured to trigger a new scan every turn through a named refresh-interval constant.
  - When the interval elapses, the behaviour triggers an `AtmosphericScanAction`.
  - `AtmosphericScanAction` calls the API via `AtmosphericApiClient`, parses the JSON with the selected parser, and then invokes each configured `AtmosphericCorruptor` with the resulting `AirQualityReport`.

---

### The Architecture

#### New Abstractions

For REQ5, the feature is structured around **two primary gameplay abstractions that are counted for complexity** and one supporting parser abstraction used for API translation.

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

| Class                         | Type     | Implements                              | Summary |
|-------------------------------|----------|-----------------------------------------|---------|
| OpenWeatherPollutionParser    | New      | PollutionDataParser                     | Parses real OpenWeather Air Pollution JSON into an `AirQualityReport` containing the AQI tier and dominant pollutant. |
| FallbackPollutionParser       | New      | PollutionDataParser                     | Safe fallback parser used when the API key is absent or the network fails; returns AQI 1 so no atmospheric effects are applied without real data. |
| AirQualityReport              | New      | (value object)                          | Immutable data class that stores the parsed AQI (1-5) and dominant pollutant string. |
| AtmosphericApiClient          | New      | -                                       | Calls the external API with a query profile derived from the monitor's game-state coordinates and returns the raw JSON string. |
| AtmosphericServicesFactory    | New      | -                                       | Factory that wires together the parser, client, and all `AtmosphericCorruptor`s so higher-level classes do not need to construct concrete implementations directly. |
| AtmosphericMonitor            | New      | Ground, AtmosphericAnchor               | Stationary probe ground; acts as the atmospheric anchor so corruptors can locate it without downcasting to a concrete actor type. |
| EnvironmentalMonitorBehaviour | New      | -                                       | Triggers periodic atmospheric scans and coordinates the API pipeline through abstractions. |
| AtmosphericScanAction         | New      | Action                                  | Fetches JSON, parses it, prints an AQI summary, and invokes all registered `AtmosphericCorruptor`s. |
| HazardCorruptor               | New      | AtmosphericCorruptor                    | Calls `applyAtmosphere` on all `AtmosphereSensitiveActor`s; at moderate AQI spreads local toxic puddles; at severe AQI creates a border ring and monitor hotspot. |
| EconomyCorruptor              | New      | AtmosphericCorruptor                    | `SO_2`-driven: reduces credits for tracked actors and toggles a disruption flag consumed by `SellAction` to probabilistically void transactions. |
| PollutantSpawnCorruptor       | New      | AtmosphericCorruptor                    | At severe AQI, locates the `AtmosphericAnchor` and spawns one `Undead` on a valid adjacent empty tile. |
| ContractedWorker              | Existing | AtmosphereSensitiveActor                | Worker that converts AQI into HP loss and poison. |
| Muckraker                     | Existing | AtmosphereSensitiveActor                | Scavenger that leaks `ToxicWaste` at moderate AQI and shoves adjacent actors at severe AQI. |
| Undead                        | Existing | AtmosphereSensitiveActor                | Corrupts its tile with `ToxicWaste` at moderate AQI and radiates adjacent damage at severe AQI. |

---

### Request

Example dynamic query:

```text
GET https://api.openweathermap.org/data/2.5/air_pollution?lat={monitorLat}&lon={monitorLon}&appid={OPENWEATHER_API_KEY}
```

The `lat` and `lon` values are derived from the atmospheric monitor's current map coordinates, so the request changes with the game state instead of being a static URL.

### Schema

Example JSON structure expected by the game:

```json
{
  "list": [
    {
      "main": {
        "aqi": 4
      },
      "components": {
        "no_2": 18.7,
        "so_2": 42.1
      }
    }
  ]
}
```

The game reads `list[0].main.aqi` and compares `components.no_2` and `components.so_2` to determine the dominant pollutant.
