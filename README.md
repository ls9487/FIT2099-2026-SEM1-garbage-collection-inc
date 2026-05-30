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

## REQ 5: A.P.I : Toxic Atmosphere (HD Requirement)

### Pitch

The moon facility has an automated monitor that calls the OpenWeather Air Pollution API using game-state-driven coordinates. The returned AQI and dominant pollutant are converted into changes to health, terrain, economy, and creature behaviour. Severe pollution can also create new hazards and spawn new enemies.

### Mechanics

- The system queries the OpenWeather Air Pollution API at runtime using an API key stored in `OPENWEATHER_API_KEY`.
- The request is dynamic: `AtmosphericApiClient` derives latitude and longitude from the monitor's current map coordinates, so the query changes with game state.
- The JSON response is parsed into an `AirQualityReport` by `PollutionDataParser` implementations created through `AtmosphericServicesFactory`.
- `OpenWeatherPollutionParser` extracts the AQI and dominant pollutant (`NO_2` or `SO_2`), while `FallbackPollutionParser` returns a safe report when no API key is configured.
- `HazardCorruptor` applies tiered atmosphere effects: at AQI 3 it creates random local  ToxicWaste  patches around affected actors, and at AQI 4-5 it creates a toxic border ring plus a hotspot around the atmospheric anchor.
- `EconomyCorruptor` reacts to dominant `SO_2` by reducing credits across the map and enabling a 50% transaction failure effect in `SellAction`.
- `PollutantSpawnCorruptor` reacts to severe AQI by locating the `AtmosphericAnchor` and spawning a new `Undead` on a valid adjacent tile.
- `ContractedWorker`, `Muckraker`, and `Undead` implement `AtmosphereSensitiveActor` so each actor responds differently to the same `AirQualityReport`.
- The API key is read with `System.getenv("OPENWEATHER_API_KEY")` and is never committed to the repository.

### Architecture

#### New abstractions

- `AtmosphericCorruptor` (new interface)
  - Implementations:
    - `HazardCorruptor` (new)
    - `EconomyCorruptor` (new)
    - `PollutantSpawnCorruptor` (new)

- `AtmosphereSensitiveActor` (new interface)
  - Implementations:
    - `ContractedWorker` (retrofitted existing class)
    - `Muckraker` (retrofitted existing class)
    - `Undead` (retrofitted existing class)

#### Supporting infrastructure

- `PollutionDataParser` (new interface, supporting infrastructure)
  - Implementations:
    - `OpenWeatherPollutionParser` (new)
    - `FallbackPollutionParser` (new)

#### Higher-level classes using abstractions

- `EnvironmentalMonitorBehaviour` depends on `PollutionDataParser` and `AtmosphericServicesFactory` rather than concrete corruptors.
- `AtmosphericScanAction` depends on `PollutionDataParser` and `List<AtmosphericCorruptor>` rather than concrete implementations.

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
