# FIT2099 Assignment (Semester 1, 2026)

## Project Snapshot

A team-based Java software engineering project developed for **FIT2099 Object-Oriented Design and Implementation** at Monash University.

The project extends an existing game engine with modular actors, behaviours, vehicles, environmental systems and interactive mechanics. My primary ownership focused on two advanced subsystems:

- **Assignment 2 REQ 5 — Stateful Creatures:** deterministic, state-driven autonomous entities with reusable behaviour components.
- **Assignment 3 REQ 3 — Vehicle System:** an extensible capability-driven vehicle and upgrade architecture with automated testing.

**Core technologies:** Java · Object-Oriented Design · OOP · UML · JUnit 5 · Mockito · Maven · Git · GitLab · GitHub

---

## My Contributions — Yang Li Shen (lyan0121/ls9487)

### Stateful Autonomous Creatures — A2 REQ 5

Designed and implemented the complete **HD-level Stateful Creatures requirement**, including the architecture, creature implementations, behaviours, UML and sequence design.

- Built a reusable `StatefulCreature` abstraction that maps runtime emotions to independent state objects and coordinates **state transitions → transition effects → behaviour execution**.
- Implemented **two autonomous creatures with four deterministic states each**:
  - **Muckraker** — transitions based on inventory capacity and nearby enemy detection.
  - **Phantasm Wisp** — transitions based on player count, proximity and adjacency.
- Developed reusable behaviours including `FleeBehaviour`, `PickItemBehaviour`, `SwapItemsBehaviour` and `TeleportBehaviour`.
- Designed transition logic around runtime game state rather than random state changes, while keeping behaviour and transition responsibilities separated.
- Applied object-oriented design principles to make new states and behaviours extensible without rewriting the core creature lifecycle.

**Code:**  
[`StatefulCreature.java`](src/main/java/game/actors/StatefulCreature.java) ·
[`Muckraker.java`](src/main/java/game/actors/Muckraker.java) ·
[`PhantasmWisp.java`](src/main/java/game/actors/PhantasmWisp.java)

**Design:**  
[A2 REQ5 UML](docs/design/assignment2/REQ5/A2-UML-REQ5.pdf) ·
[Sequence Diagram](docs/design/assignment2/REQ5/REQ5-SequenceDiagram.pdf)

---

### Extensible Vehicle & Capability System — A3 REQ 3

Owned the design and implementation of the **Vehicle System**, building a reusable architecture for mountable vehicles and runtime-dependent upgrades.

- Architected `Rideable` as the common abstraction for vehicle mounting/dismounting and lifecycle management.
- Designed `RideableUpgrade` to dynamically activate and deactivate capabilities depending on the rider's runtime `MOUNTED` state.
- Implemented **five specialised vehicle/upgrade components**:
  - `HoverBike`
  - `MechSuit`
  - `BulldozerPlough`
  - `MagneticField`
  - `WarpBattery`
- Used capability-based composition to support behaviours such as hovering, crushing, bulldozing, extra-energy actions and teleportation without coupling every feature directly to individual actors.
- Built a **JUnit 5 + Mockito regression test suite** covering mounting state, capability activation, vehicle behaviour, environmental interactions, boundary cases and failure conditions.

**Code:**  
[`game/vehicles`](src/main/java/game/vehicles)

**Tests:**  
[`game/vehicles tests`](src/test/java/game/vehicles)

**Design:**  
[A3 REQ3 UML](docs/design/assignment3/REQ3/A3-UML-REQ3.png)

---

## Engineering Highlights

This work gave me practical experience designing beyond individual classes and thinking about software at the **system level**:

- **State-driven architecture** for autonomous entity behaviour
- **Abstraction and polymorphism** for extensible software components
- **Capability-based composition** instead of large conditional class hierarchies
- **Separation of concerns** between state, behaviour, actions and entities
- **UML and sequence modelling** before and alongside implementation
- **Automated regression testing** with JUnit and Mockito
- Extending an **existing codebase and game engine** while preserving compatibility with other team members' features

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

- [Assignment 2 Contribution Log](https://docs.google.com/spreadsheets/d/1TWNLnvuanieYjkHCNkfedISp4kWQJtyMlGK2CqDwMns/edit?usp=sharing)

LINK TO OUR CONTRIBUTION LOGS FOR ASSIGNMENT 3:

- [Assignment 3 Contribution Log](https://docs.google.com/spreadsheets/d/1pSkZRv998trWtFAZIj4YCjXskz3HTpm3TfvSJoTNiO0/edit?usp=sharing)

## Assignment 2 REQ 5: Stateful Creatures (HD Requirement)

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

## Unit testing

All formal JUnit tests live under `src/test/java/game/...` and run with Maven from the project root:

```bash
mvn test
```

This project uses a custom Maven layout:
- main source code in `src/main/java`
- test source code in `src/test/java`

Each requirement also has a standalone console runner under `src/main/java/game/REQ{n}TestRunner.java` (REQ1 through REQ5) that can be executed directly with `java game.REQ{n}TestRunner` after a normal compile. The runners give a colour-coded visual sweep of the whole feature without needing Maven and are useful for quick regression checks during development.

### REQ1 unit tests (Quota, Plasma Cutter, Deposit)

Located under `src/test/java/game/`:

- `actions/CutActionTest.java`, which verifies that CutAction delegates to the Cuttable's cutBy() with the correct arguments, calls it exactly once, and that the menu description includes the actor's name.
- `actions/DepositActionTest.java`, which verifies that DepositAction adds the correct company credits to QuotaManager, delegates side effects to the Depositable, confirms credits are added before side effects, and that the menu description includes the actor name and credit amount.
- `grounds/QuotaManagerTest.java`, which verifies credit accumulation below quota, multiple deposits accumulating correctly, the exact 100-credit rank-up boundary, compounding quota and turn limit math across two rank-ups, isPastDeadline() and isOnDeadline() returning false before the deadline, and getStatus() reflecting rank, credits, and turns correctly.
- `grounds/AluminiumDoorTest.java`, which verifies that cutting drops AluminiumScrap (weight 2), transforms the tile into a passable Floor, produces a result message mentioning the cut and scrap, does not crash with no adjacent actors, does not crash with an adjacent actor in explosion range, and does not crash when cut twice.
- `grounds/REQ1VentTest.java`, which verifies that cutting drops an IndustrialFan (weight 5), transforms the tile into a passable Floor, spawns an Undead on the exact tile, produces a result message mentioning the cut and fan, and does not crash when cut twice.
- `items/AlienCubeTest.java`, which verifies that cutting drops an AlienArtifact (weight 1) at the actor's location, removes the cube from inventory, applies PoisonStatus to the worker, produces a result message mentioning the cut, artifact, and poison, and does not crash when the cube is not in inventory at cut time.
- `items/AluminiumScrapTest.java`, which verifies the deposit value is 50, that it is not Sellable, and that the deposit value is strictly positive.
- `items/IndustrialFanTest.java`, which verifies the sell price is 150, the deposit value is 10, both values are positive and distinct, and that it is both Sellable and Depositable.
- `items/AlienArtifactTest.java`, which verifies the sell price is 200, the deposit value is 100, both values are positive and distinct, and that it is both Sellable and Depositable.
- `items/PlasmaCutterTest.java`, which verifies the buy price is exactly 50, the CUTTER ability is enabled, it is Buyable but not Sellable, and the buy price boundary holds against adjacent values 49 and 51.

### REQ2 unit tests (Scrap Snatcher + Fleshy Trees)

Located under `src/test/java/game/`:

- `actors/ScrapSnatcherTest.java`, which verifies the Scrap Snatcher's base stats, non-hostile starting ability, and behaviour swap on infection.
- `behaviours/SnatchBehaviourTest.java`, which verifies that SnatchBehaviour picks up only depositable items and returns null otherwise.
- `spawners/ScrapSnatcherSpawnerTest.java`, which verifies the loot explosion drops depositables on valid adjacent tiles when a Snatcher is spawned.
- `trees/FleshyTreeMatureTest.java`, which verifies the mature tree's grow chance, grow turns, and spawn behaviour next to a worker.
- `trees/FleshyTreeMonolithTest.java`, which verifies that the monolith teleports adjacent workers on tick.
- `locations/Deprecated99TreeBehaviorTest.java`, which verifies that the 99-deprecated map seeds the correct tree stages and that growth advances through Sprout to Mature to Monolith.
- `locations/Overflow20TreeBehaviorTest.java`, which verifies the 20-overflow map's tree pipeline including the sapling stage.

### REQ3 unit tests (Vehicle System)

Located under `src/test/java/game/`:

- `vehicles/RideableTest.java`, which verifies the mount and dismount flow including RideStatus and item placement.
- `vehicles/RideableUpgradeTest.java`, which verifies that upgrade abilities turn on only while the carrier has RideStatus.
- `vehicles/HoverBikeTest.java`, which verifies the HOVER ability is present and the hover blast action becomes available when the rider has EXTRA_ENERGY.
- `vehicles/MechSuitTest.java`, which verifies the CRUSH ability and the per-turn fire and collapse effects while mounted.
- `vehicles/BulldozerPloughTest.java`, which verifies the BULLDOZE ability is toggled by mount state.
- `vehicles/MagneticFieldTest.java`, which verifies the EXTRA_ENERGY ability, the per-turn item collection, and the previous-tile toxic waste effect.
- `vehicles/WarpBatteryTest.java`, which verifies the EXTRA_ENERGY ability and the teleport action through impassable tiles.
- `grounds/HoleTest.java`, which verifies that only actors with HOVER may enter a hole.
- `grounds/REQ3VentTest.java`, which verifies that only actors with HOVER may enter a vent, and that the vent's ground identity stays consistent.
- `grounds/ToxicWasteTest.java`, which verifies tile damage on tick and the hover immunity that lets HOVER actors stand on the tile safely.
- `grounds/SandTest.java`, which verifies that an actor standing on sand is shifted to a nearby enterable location each tick.

### REQ4 unit tests (Turrets and Projectiles)

Located under `src/test/java/game/`:

- `turrets/GunTurretTest.java`, which verifies turrets targeting non-workers, ignoring workers, and checks boundary values for being able to fire.
- `turrets/SiphonTurretTest.java`, which verifies only non-dead worker registration, which impacts its readiness to fire, and the 3 damage registration cost.
- `turrets/NuclearPadTest.java`, which verifies the arming cooldown progresses only while a worker is adjacent, and progresses only once per turn even with multiple workers.
- `projectiles/FireBulletTest.java`, which verifies the case when an actor is hit by the projectile (direct damage), as well as when the projectile misses (no effect, but it mustn't crash).
- `projectiles/SiphonBulletTest.java`, which verifies the 3-damage direct hit, 1-damage indirect hits, and that the source actor is healed by the total damage dealt (if no source actor, damage is still done, just no healing).
- `projectiles/NuclearMissileTest.java`, which verifies the 500-damage direct hit, 5-damage indirect hits, as well as changes to the terrain (even when no actor was standing there).

### REQ5 unit tests (Toxic Atmosphere)

Located under `src/test/java/game/atmosphere/`:

- `AirQualityReportTest`, which verifies that AirQualityReport stores and returns the AQI and dominant pollutant correctly.
- `OpenWeatherPollutionParserTest`, which verifies that the API parser extracts AQI correctly and chooses the correct dominant pollutant from valid API-like JSON.
- `OpenWeatherPollutionParserDefaultTest`, which verifies that the parser falls back to safe defaults when JSON is null or missing AQI data.
- `FallbackPollutionParserTest`, which verifies that the fallback parser always returns a safe AQI-1 report with no active pollutant.
- `EconomyCorruptorTest`, which verifies that economy disruption is toggled correctly based on the dominant pollutant.
- `HazardCorruptorTest`, which verifies that safe AQI causes HazardCorruptor to return immediately without applying corruption.
- `PollutantSpawnCorruptorTest`, which verifies that AQI below the severe threshold does not attempt undead spawning.

## REQ 5: A.P.I : Toxic Atmosphere (HD Requirement)

### Pitch

The moon facility has an automated monitor that calls the OpenWeather Air Pollution API using game-state-driven coordinates. The returned AQI and dominant pollutant are converted into changes to health, terrain, economy, and creature behaviour. Severe pollution can also create new hazards and spawn new enemies.

### Mechanics

- The system queries the OpenWeather Air Pollution API at runtime using an API key supplied through the `OPENWEATHER_API_KEY` environment variable.
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
    - `list[0].components.no2` and `list[0].components.so2` to determine the dominant pollutant (`"no2"` or `"so2"`).
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
  - **Monitor hotspot**: using the monitor's own location (passed in on each scan), for all eligible empty tiles within Manhattan distance 2 of that location it applies a **50% chance per tile** to convert the ground into `ToxicWaste`.
- `EconomyCorruptor` interprets sulphur dioxide (`SO_2`) as a proxy for economic disruption.
  - When the dominant pollutant in `AirQualityReport` is `"so2"`, it updates the disruption state stored on `SuperComputer`, reduces credits for actors tracking `EclipseStatistics.CREDITS` by 10, and enables a disrupted shop state.
  - When `SuperComputer.isEconomyDisrupted()` is `true`, `SellAction` still removes the item from the seller's inventory, but there is a **50% chance** that the payout becomes **0 credits** instead of the normal price.
  - When the disruption state is `false`, `SellAction` behaves normally.
- `PollutantSpawnCorruptor` reacts to severe AQI by spawning one `Undead` on a randomly selected valid empty tile adjacent to the monitor's location.
- The atmospheric system is driven by a dedicated monitor ground:
  - `AtmosphericMonitor` is a stationary `Ground` that represents the facility's automated probe. The monitor's own `Location`, supplied by the engine on each `tick`, is what every downstream class uses as the centre point for atmosphere effects.
  - It owns an `EnvironmentalMonitorController`, which keeps the monitor logic separate from the ground itself.
  - During each ground tick, the controller delegates to `AtmosphericScanner`.
  - `AtmosphericScanner` calls the API via `AtmosphericApiClient`, parses the JSON with the selected parser, and then invokes each configured `AtmosphericCorruptor` with the resulting `AirQualityReport`.

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
        "no2": 18.7,
        "so2": 42.1
      }
    }
  ]
}
```

The game reads `list[0].main.aqi` and compares `components.no2` and `components.so2` to determine the dominant pollutant.

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

- `AtmosphericScanner` is a higher-level class that works with the `PollutionDataParser` abstraction and a `List<AtmosphericCorruptor>` instead of depending on one concrete corruptor class.
- `EnvironmentalMonitorController` is a higher-level class that coordinates scans and obtains corruptors through `AtmosphericServicesFactory`, allowing the scan pipeline to stay coupled to abstractions instead of directly constructing specific corruptors.
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

Open **Run > Edit Configurations...** and add the following to the environment variables field for your game run configuration:

```text
OPENWEATHER_API_KEY=your_api_key_here
```

## How to run the project

Open the project in IntelliJ IDEA and run the main game entry point as configured for the assignment starter project.
