# FIT2099 Assignment (Semester 1, 2026)

```
 _______  _______  ______    _______  _______  _______  _______                          
|       ||   _   ||    _ |  |  _    ||   _   ||       ||       |                         
|    ___||  |_|  ||   | ||  | |_|   ||  |_|  ||    ___||    ___|                         
|   | __ |       ||   |_||_ |       ||       ||   | __ |   |___                          
|   ||  ||       ||    __  ||  _   | |       ||   ||  ||    ___|                         
|   |_| ||   _   ||   |  | || |_|   ||   _   ||   |_| ||   |___                          
|_______||__| |__||___|  |_||_______||__| |__||_______||_______|                         
 _______  _______  ___      ___      _______  _______  _______  ___   _______  __    _   
|       ||       ||   |    |   |    |       ||       ||       ||   | |       ||  |  | |  
|       ||   _   ||   |    |   |    |    ___||       ||_     _||   | |   _   ||   |_| |  
|       ||  | |  ||   |    |   |    |   |___ |       |  |   |  |   | |  | |  ||       |  
|      _||  |_|  ||   |___ |   |___ |    ___||      _|  |   |  |   | |  |_|  ||  _    |  
|     |_ |       ||       ||       ||   |___ |     |_   |   |  |   | |       || | |   |  
|_______||_______||_______||_______||_______||_______|  |___|  |___| |_______||_|  |__|  
 ___   __    _  _______                                                                  
|   | |  |  | ||       |                                                                 
|   | |   |_| ||       |                                                                 
|   | |       ||       |                                                                 
|   | |  _    ||      _| ___                                                             
|   | | | |   ||     |_ |   |                                                            
|___| |_|  |__||_______||___|                                                                                                                                                                                                                         
```
LINK TO OUR CONTRIBUTION LOGS FOR ASSIGNMENT 2:

https://docs.google.com/spreadsheets/d/1TWNLnvuanieYjkHCNkfedISp4kWQJtyMlGK2CqDwMns/edit?usp=sharing

LINK TO OUR CONTRIBUTION LOGS FOR ASSIGNMENT 3:

https://docs.google.com/spreadsheets/d/1pSkZRv998trWtFAZIj4YCjXskz3HTpm3TfvSJoTNiO0/edit?usp=sharing

## REQ 5: Stateful Creatures (HD Requirement)

We have implemented **two stateful creatures** as required for the HD component:

### 1. Muckraker

The Muckraker is a scavenging creature that changes its emotional state based on inventory status and nearby enemies. It has **4 distinct states**:

| State | Emotion | Behaviour | Immediate Effect on Entry |
|-------|---------|-----------|--------------------------|
| Scavenge | CURIOUS | PickupBehaviour (NEW) - Moves to nearest ground item and picks it up | Steals the most valuable item from nearest worker within vigilanceRange (3 tiles) |
| Hoard | GREEDY | WanderBehaviour (EXISTING) | Drops one random item from its inventory |
| Panic | FEARFUL | FleeBehaviour (NEW) - Runs directly away from nearest enemy | All enemies within vigilanceRange stunned for 2 ticks |
| Defensive | ANGRY | AttackBehaviour (EXISTING) | All items within vigilanceRange pulled 1 tile closer |

#### State Transition Conditions

| Current State | Condition | Next State |
|---------------|-----------|------------|
| Scavenge | Inventory full AND no enemy | Hoard (GREEDY) |
| Scavenge | Any enemy detected | Panic (FEARFUL) |
| Hoard | Inventory not full AND no enemy | Scavenge (CURIOUS) |
| Hoard | Any enemy detected | Defensive (ANGRY) |
| Panic | Enemy lost | Scavenge (CURIOUS) |
| Panic | Inventory becomes full AND enemy present | Defensive (ANGRY) |
| Defensive | Enemy lost | Hoard (GREEDY) |
| Defensive | Inventory not full AND enemy present | Panic (FEARFUL) |

**Rules compliance:**
- 4 states implemented 
- Deterministic transitions based on conditions (no randomness) 
- Each state transitions to at least 2 other states (or stays) 
- Conditions involve complex logic (inventory status + enemy detection) 
- Immediate effects on state change (stealing, stunning, pulling items, dropping) 
- New behaviours: PickupBehaviour, FleeBehaviour 

---

### 2. Phantasm Wisp

The Phantasm Wisp is a mischievous ethereal creature that reacts to player proximity and counts. It has **4 distinct states**:

| State | Emotion | Behaviour | Immediate Effect on Entry |
|-------|---------|-----------|--------------------------|
| Lure | MISCHIEVOUS | SwapItemsBehaviour (NEW) – swaps two random ground items within vigilanceRange (5 tiles) | All items within vigilanceRange pushed 1 tile away |
| Shadow | CAUTIOUS | FollowBehaviour (EXISTING) – follows nearest worker within vigilanceRange | All enemies within vigilanceRange poisoned for 2 ticks |
| Tinker | CURIOUS | TeleportBehaviour (NEW) – teleports nearest player to random walkable tile within vigilanceRange | Surrounding tile of nearest worker set on fire for 3 turns |
| Flee | FEARFUL | WanderBehaviour (EXISTING) | All players within vigilanceRange drop their most valuable item |

#### State Transition Table – Phantasm Wisp

| Current State | Condition | Next State |
|---------------|-----------|---------------------|
| Lure | Exactly 1 player within 2–4 tiles (non‑adjacent) | Shadow (Cautious) |
| Lure | Exactly 1 player adjacent | Tinker (Curious) |
| Lure | More than 1 player detected | Flee (Fearful) |
| Shadow | The followed player moves adjacent | Tinker (Curious) |
| Shadow | A second player appears | Flee (Fearful) |
| Shadow | No players remain | Lure (Mischievous) |
| Tinker | Adjacent player moves away (still exactly 1, non‑adjacent) | Shadow (Cautious) |
| Tinker | A second player appears | Flee (Fearful) |
| Tinker | Lone player is gone (zero players) | Lure (Mischievous) |
| Flee | Exactly 1 player non‑adjacent | Shadow (Cautious) |
| Flee | Exactly 1 player adjacent | Tinker (Curious) |
| Flee | No players remain | Lure (Mischievous) |

**Rules compliance:**
- 4 states implemented
- Deterministic transitions based on player count and adjacency
- Each state transitions to at least 2 other states (or stays)
- Conditions involve complex logic (player count, adjacency, following) 
- Immediate effects (push items, poison, fire, drop items) 
- New behaviours: SwapItemsBehaviour, TeleportBehaviour 

---

### SOLID Principles Applied

- **Single Responsibility:** Each state's behaviour and entry effect are encapsulated separately
- **Open/Closed:** New states/emotions can be added without modifying existing state logic
- **Liskov Substitution:** State behaviours implement common interfaces
- **Interface Segregation:** Separate interfaces for different behaviour types (PickupBehaviour, FleeBehaviour, etc.)
- **Dependency Inversion:** High-level state machine depends on abstractions (State interfaces), not concrete implementations

---

### Known Issues / Limitations

- [List any known bugs or incomplete features here]

### Testing Notes

- Muckraker transitions have been tested with various inventory states and workers
- Stun effect on Panic entry lasts exactly 2 ticks
- Item pulling on Defensive entry works within vigilanceRange

## REQ 5: A.P.I : Toxic Atmosphere (HD Requirement)

### Pitch

The moon facility has an automated monitor that calls the OpenWeather Air Pollution API using game-state-driven coordinates. The returned AQI and dominant pollutant are converted into changes to health, terrain, economy, and creature behaviour. Severe pollution can also create new hazards and spawn new enemies.

### Mechanics

- The system queries the OpenWeather Air Pollution API at runtime using an API key stored in `OPENWEATHER_API_KEY`.
- The API request can be described as:

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
    - `list[0].main.aqi` as an integer AQI index on the OpenWeather 1-5 scale.
    - `list[0].components.no_2` and `list[0].components.so_2` to determine the dominant pollutant (`"no_2"` or `"so_2"`).
  - `FallbackPollutionParser` is selected only when `OPENWEATHER_API_KEY` is absent or blank. By default it returns a safe fallback report so the feature remains executable without exposing secrets, and it can also be used locally with the commented demo presets to simulate REQ 5 corruption scenarios during testing or presentation.
- The resulting `AirQualityReport` is passed to one or more `AtmosphericCorruptor` implementations, which translate air quality into concrete game effects. These effects fall into two categories:
  - **Probability-based world effects**: random map-level effects whose chance is explicitly encoded in the code, such as local toxic spread, hotspot corruption, and disrupted shop payouts.
  - **Guaranteed actor effects**: deterministic reactions that always happen once an actor is processed at the relevant AQI tier, such as direct HP loss, poison, tile corruption, or adjacent damage applied by `AtmosphereSensitiveActor` implementations.
- `HazardCorruptor` interprets AQI in three tiers on the 1-5 scale:
  - **Tier 1 (mild)**: `aqi <= 2` - no atmospheric effects are applied.
  - **Tier 2 (moderate)**: `aqi == 3` - guaranteed actor-specific effects plus small probabilistic toxic patches.
  - **Tier 3 (severe)**: `aqi >= 4` - guaranteed heavier actor-specific effects plus large-scale terrain corruption.
- For AQI >= 3, `HazardCorruptor` iterates over the map and finds any actor exposing the `AtmosphereSensitiveActor` capability, currently `ContractedWorker`, `Muckraker`, and `Undead`. For each such actor, it calls `applyAtmosphere(report, here)` so the actor can apply its own guaranteed health, terrain, movement, or area effects according to its own implementation.
- At moderate AQI (3), after calling `applyAtmosphere` on an atmosphere-sensitive actor, `HazardCorruptor` also creates small toxic puddles around that actor:
  - It examines the neighbouring tiles around the actor using the engine's `Exit` API.
  - On neighbouring eligible empty tiles, there is a **25% chance per tile** to replace the ground with `ToxicWaste`, forming a local contaminated cluster rather than corrupting the entire map.
- At severe AQI (4-5), `HazardCorruptor` applies heavier guaranteed actor-specific effects via `applyAtmosphere`, and then performs two large-scale terrain mutations:
  - **Border ring**: it walks the outer border coordinates of the `GameMap` and replaces the ground with `ToxicWaste`, producing a visible toxic perimeter around the facility.
  - **Monitor hotspot**: it locates the atmospheric anchor near the monitor and, for all eligible empty tiles within Manhattan distance 2 of that anchor, applies a **50% chance per tile** to convert the ground into `ToxicWaste`.
- `EconomyCorruptor` interprets sulphur dioxide (`SO_2`) as a proxy for economic disruption.
  - When the dominant pollutant in `AirQualityReport` is `"so_2"`, it sets the global disruption flag, reduces credits for actors tracking `EclipseStatistics.CREDITS` by 10 with a floor at 0, and enables a disrupted shop state.
  - When `EconomyCorruptor.ECONOMY_DISRUPTED` is `true`, `SellAction` still removes the item from the seller's inventory, but there is a **50% chance** that the payout becomes **0 credits** instead of the normal price.
  - When the flag is `false`, `SellAction` behaves normally.
- `PollutantSpawnCorruptor` reacts to severe AQI by locating the `AtmosphericAnchor` and spawning one `Undead` on a randomly selected valid adjacent tile.
- The atmospheric system is driven by a dedicated monitor ground:
  - `AtmosphericMonitor` is a stationary `Ground` that represents the facility's automated probe and also acts as an `AtmosphericAnchor`.
  - It owns an `EnvironmentalMonitorBehaviour`, which keeps an internal tick counter.
  - To keep testing simple and make the feature observable in a short demo, the behaviour is configured to trigger a new scan every turn through a named refresh-interval constant.
  - When the interval elapses, the behaviour triggers an `AtmosphericScanAction`.
  - `AtmosphericScanAction` calls the API via `AtmosphericApiClient`, parses the JSON with the selected parser, and then invokes each configured `AtmosphericCorruptor` with the resulting `AirQualityReport`.

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

### Architecture

#### Gameplay abstractions used to implement the feature

- `AtmosphericCorruptor` (new gameplay abstraction)
  - Responsibility: apply a distinct world-level corruption effect to a `GameMap` based on an `AirQualityReport`.
  - Key method:
    - `corrupt(map : GameMap, report : AirQualityReport) : void`
  - Implementations:
    - `HazardCorruptor` (new)
    - `EconomyCorruptor` (new)
    - `PollutantSpawnCorruptor` (new)

- `AtmosphereSensitiveActor` (new gameplay abstraction)
  - Responsibility: allow actors to define their own actor-specific response to atmospheric conditions.
  - Key method:
    - `applyAtmosphere(report : AirQualityReport, here : Location) : void`
  - Implementations:
    - `ContractedWorker` (retrofitted existing class)
    - `Muckraker` (retrofitted existing class)
    - `Undead` (retrofitted existing class)

#### Supporting API infrastructure

- `PollutionDataParser` (new supporting interface for API parsing)
  - Responsibility: parse a raw JSON string into an `AirQualityReport`.
  - Key method:
    - `parse(json : String) : AirQualityReport`
  - Implementations:
    - `OpenWeatherPollutionParser` (new)
    - `FallbackPollutionParser` (new)

#### Higher-level classes demonstrating dependency inversion

- `AtmosphericScanAction` is a higher-level class that works with the `PollutionDataParser` abstraction and a `List<AtmosphericCorruptor>` instead of depending on one concrete corruptor class.
- `EnvironmentalMonitorBehaviour` is a higher-level class that coordinates periodic scans and obtains corruptors through `AtmosphericServicesFactory`, allowing the scan pipeline to stay coupled to abstractions instead of directly constructing specific corruptors.
- `AtmosphericServicesFactory` centralises the creation of parser and corruptor implementations so the scan workflow stays decoupled from specific concrete classes.
## Running the feature

1. Set your API key before running the game:
   ```bash
   export OPENWEATHER_API_KEY="your_api_key_here"
   ```
2. Launch the game normally.
3. Travel to the Moonbase map and observe the atmospheric monitor effects over time.
4. If no API key is configured, the system falls back to a safe pollution report.

## Environment variable setup

The OpenWeather API key must be available as an environment variable named `OPENWEATHER_API_KEY`.

### macOS / Linux

```bash
export OPENWEATHER_API_KEY="your_api_key_here"
```

### Windows PowerShell

```powershell
$env:OPENWEATHER_API_KEY="your_api_key_here"
```

### IntelliJ IDEA

Open **Run > Edit Configurations...** and add `OPENWEATHER_API_KEY=your_api_key_here` to the environment variables field for the game run configuration.

## How to run the project

Open the project in IntelliJ IDEA and run the main game entry point as configured for the assignment starter project.
