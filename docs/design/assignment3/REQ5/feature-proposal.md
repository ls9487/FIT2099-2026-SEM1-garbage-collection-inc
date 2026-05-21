# Feature Proposal - Assignment 3

## REQ3 & REQ4: Creative Mode

> (To be filled by EDWIN & LI SHEN for the two creative requirements.)

---

## REQ5: A.P.I - Toxic Atmosphere (HD Requirement)

### The Pitch

The abandoned moon facility's environmental monitoring system remains operational, continuously polling real-world air quality data from Earth's polluted cities. The atmospheric readings (particulate density, toxic gases, and overall air quality) are directly translated into hazardous environmental conditions on the moon. As these pollution metrics fluctuate, the facility's health and economy dynamically adapt to mirror the detected contamination levels.

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
    - `list[0].main.aqi` as an integer AQI index.
    - `list[0].components.no2` and `list[0].components.so2` to determine the dominant pollutant (`"no2"` or `"so2"`).
  - Temperature, humidity, wind speed, and pressure are currently represented by fixed defaults, as the Air Pollution endpoint does not provide them directly.

- The resulting `AirQualityReport` is passed to one or more `AtmosphericCorruptor` implementations, which translate air quality into concrete game effects:

  - **HazardCorruptor**
    - Iterates over the map and finds any actor implementing the `AtmosphereSensitiveActor` interface.
    - For each such actor (currently only `ContractedWorker`), calls `applyAtmosphere(report)`.
    - `ContractedWorker.applyAtmosphere` interprets AQI as:
      - AQI < 51: no effect.
      - AQI 51–150: worker loses 1 HP.
      - AQI 151-200: worker loses 1 HP and takes 1 point of poison damage.
      - AQI ≥ 201: worker loses 2 HP.

  - **EconomyCorruptor**
    - Interprets SO_2 as a proxy for economic disruption.
    - When the dominant pollutant in `AirQualityReport` is `"so2"`, scans the map for `ContractedWorker` instances and reduces their `EclipseStatistics.CREDITS` by 10 (floored at zero) using the existing statistics API.

- The atmospheric system is driven by a dedicated monitor actor:
  - `AtmosphericMonitor` is a stationary `EclipseActor` that represents the facility’s automated probe.
  - It owns an `EnvironmentalMonitorBehaviour`, which keeps an internal tick counter.
  - Every 50 turns, the behaviour returns an `AtmosphericScanAction`; on other turns it returns `null`.
  - `AtmosphericScanAction` calls the OpenWeather API via `AtmosphericApiClient`, parses the JSON with `OpenWeatherPollutionParser`, and then invokes each configured `AtmosphericCorruptor` with the resulting `AirQualityReport`.

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

| Class                     | Type      | Implements            | Summary                                                                                                                                           |
|---------------------------|-----------|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| OpenWeatherPollutionParser| New       | PollutionDataParser   | Parses OpenWeather Air Pollution JSON using simple string searches (no external libs) into an `AirQualityReport` (AQI and dominant pollutant).    |
| AirQualityReport          | New       | (value object)        | Holds AQI, dominant pollutant, and basic weather fields used by `AtmosphericCorruptor` implementations.                                           |
| HazardCorruptor           | New       | AtmosphericCorruptor  | Applies AQI-tier damage and poison to actors that implement `AtmosphereSensitiveActor` (currently `ContractedWorker`).                            |
| EconomyCorruptor          | New       | AtmosphericCorruptor  | Interprets SO_2 as economic disruption and erodes workers’ `EclipseStatistics.CREDITS` when SO_2 dominates.                                       |
| AtmosphericScanAction     | New       | Action                | Action that calls `AtmosphericApiClient`, parses the response via `PollutionDataParser`, and delegates to all configured `AtmosphericCorruptor`s. |
| EnvironmentalMonitorBehaviour | New  | Behaviour             | Passive behaviour attached to `AtmosphericMonitor` that automatically returns an `AtmosphericScanAction` every N turns (50).                      |
| AtmosphericMonitor        | New       | EclipseActor          | Stationary actor representing the facility’s probe; owns an `EnvironmentalMonitorBehaviour` and does not override `playTurn`.                     |
| AtmosphericServicesFactory| New       | (factory)             | Creates the `OpenWeatherPollutionParser` and the set of `AtmosphericCorruptor`s used by the monitor.                                              |

Existing engine classes referenced (not implemented by me): `GameMap`, `Actor`, `Action`, `Behaviour`, `Inventory`, `EclipseActor`.

---

### The Request

Endpoint pattern:

```text
GET https://api.openweathermap.org/data/2.5/air_pollution
    ?lat={latitude}
    &lon={longitude}
    &appid={OPENWEATHER_API_KEY}
```

The current implementation of `AtmosphericApiClient`:

- Reads `OPENWEATHER_API_KEY` from the environment.
- Selects a latitude/longitude pair from a small hard-coded list of polluted cities (for example Delhi at 28.7041, 77.1025).
- Builds the URL at runtime and issues the HTTP GET.

The URL is not a fixed string in the code and always depends on live game state (the selected city), but remains intentionally simple to satisfy the assignment’s “no external libraries” constraint.

---

### The Schema

Expected JSON structure (simplified from the OpenWeather Air Pollution documentation):

```json
{
  "coord": {
    "lon": 77.1025,
    "lat": 28.7041
  },
  "list": [
    {
      "main": {
        "aqi": 3
      },
      "components": {
        "co": 201.94,
        "no": 0.0,
        "no2": 20.1,
        "o3": 68.66,
        "so2": 0.64,
        "pm2_5": 35.5,
        "pm10": 54.3
      },
      "dt": 1605182400
    }
  ]
}
```

Fields used and their effects (summary):

| JSON field                 | Type    | Game effect                                                                                                                                                             |
|----------------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `list[0].main.aqi`         | int     | Parsed by `OpenWeatherPollutionParser` and stored as AQI in `AirQualityReport`; `HazardCorruptor` uses it to decide how much damage/poison to apply to workers.         |
| `list[0].components.no2`   | double  | Compared with SO_2; if NO_2 ≥ SO_2, `dominantPollutant` in `AirQualityReport` is set to `"no2"`.                                                                        |
| `list[0].components.so2`   | double  | Compared with NO_2; if SO_2 > NO_2, `dominantPollutant` in `AirQualityReport` is set to `"so2"`. `EconomyCorruptor` erodes credits when `dominantPollutant` is `"so2"`. |

Other components (CO, O₃, PM_(2*5), PM_10) are present in the JSON and are not currently used in gameplay. They remain available for future extensions without changing the public interfaces.

---

### SOLID Principles Applied

- **Single Responsibility**
  - `OpenWeatherPollutionParser` only parses JSON into `AirQualityReport`. It does not modify the game world.
  - `AtmosphericCorruptor` implementations (`HazardCorruptor`, `EconomyCorruptor`) only mutate the `GameMap` and actors based on an `AirQualityReport`. They do not perform parsing or HTTP calls.
  - `AtmosphericApiClient` only handles HTTP and query construction.

- **Open and Closed**
  - Adding support for new atmospheric parameters (for example additional pollutants or different economic rules) can be done by adding new `PollutionDataParser` or `AtmosphericCorruptor` implementations. Existing code that depends on these interfaces does not need to change.

- **Liskov Substitution**
  - Any `AtmosphericCorruptor` can be injected into `EnvironmentalMonitorBehaviour`. The behaviour always calls `corrupt(map, report)` without caring which concrete implementation it receives.
  - Any actor that implements `AtmosphereSensitiveActor` can participate in hazard effects, not just `ContractedWorker`.

- **Interface Segregation**
  - High-level code depends on two small interfaces, `PollutionDataParser` and `AtmosphericCorruptor`, instead of a large combined interface. Parsing and corruption responsibilities are kept separate.
  - `AtmosphereSensitiveActor` is a focused interface for actors that care about air quality.

- **Dependency Inversion**
  - `AtmosphericScanAction` and `EnvironmentalMonitorBehaviour` depend on `PollutionDataParser` and `AtmosphericCorruptor` abstractions, not on concrete classes.
  - `AtmosphericServicesFactory` selects and instantiates concrete parsers and corruptors, then injects them into high-level classes.

---

### Security Notes

- The OpenWeather API key is stored only in the `OPENWEATHER_API_KEY` environment variable.
- In Java the key is accessed with `System.getenv("OPENWEATHER_API_KEY")`.
- The key is never hard coded or committed to Git.
- A short note in the README explains how to set the environment variable.

---

### README Setup Instructions (Summary)

- Register a free API key at the OpenWeather website.
- Set `OPENWEATHER_API_KEY` before running the game:
  - macOS and Linux: `export OPENWEATHER_API_KEY=your_key_here`
  - Windows Command Prompt: `set OPENWEATHER_API_KEY=your_key_here`
  - IntelliJ: Run → Edit Configurations → Environment variables → `OPENWEATHER_API_KEY=your_key_here`
- Run the game as usual (for example via IntelliJ or `./gradlew run`).
- Unit tests that depend on `AirQualityReport` can use stub `PollutionDataParser` implementations instead of real HTTP calls.

---

### Unit Testing Notes

- **OpenWeatherPollutionParser**
  - Tested with synthetic JSON strings to ensure:
    - AQI is parsed correctly from the `main.aqi` field.
    - The dominant pollutant is correctly chosen between NO_2 and SO_2 based on their numeric values.
    - Reasonable defaults are applied when fields are missing or malformed.

- **HazardCorruptor**
  - Tested with mock or stub actors that implement `AtmosphereSensitiveActor` (for example a test double for `ContractedWorker`):
    - Given different AQI values in `AirQualityReport`, tests assert that `applyAtmosphere` is called and that the correct damage/poison behaviour occurs.

- **EconomyCorruptor**
  - Tested with a small synthetic `GameMap` and a `ContractedWorker`:
    - When `dominantPollutant` is `"so2"`, tests assert that the worker’s `EclipseStatistics.CREDITS` statistic is reduced by 10 and never below zero.
    - When `dominantPollutant` is `"no2"`, tests assert that credits are unchanged.

- **AtmosphericScanAction and EnvironmentalMonitorBehaviour**
  - Integration-tested using a stub `PollutionDataParser` that returns a fixed `AirQualityReport` and a mock `AtmosphericCorruptor` that records calls.
  - Tests verify that:
    - After the configured tick interval, the behaviour returns an `AtmosphericScanAction`.
    - Executing the action results in `corrupt(map, report)` being called on each corruptor.
```

