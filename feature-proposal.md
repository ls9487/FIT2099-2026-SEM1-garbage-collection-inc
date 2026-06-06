# Feature Proposals
This markdown file contains the feature proposals for REQs 3-5 for Assignment 3.

---

# REQ 3: Vehicle System Implementation
by Li Shen

## Core Abstractions

**Rideable** (abstract)  
A non-portable item that an actor can mount or dismount. Mounting adds `RideStatus` to the actor and moves the rideable into the actor's inventory. Dismounting removes the status and places the rideable back on the ground.

**RideableUpgrade** (abstract)  
A portable item that can be carried in inventory. Its special abilities are only active while the carrier has `RideStatus` (i.e., is mounted on a rideable).

**VehicleAbilities** (enum)  
`HOVER`, `CRUSH`, `PURIFY`, `BULLDOZE`, `EXTRA_ENERGY`

---

## Rideable Vehicles

### HoverBike
- Grants `HOVER` ability.
- An actor with `HOVER` can enter **Hole** and **Vent** (normally impassable).
- **Toxic Waste** does not deal damage to an actor with `HOVER`.
- **Special action - Hover Blast** (requires `EXTRA_ENERGY`):
    - Destroys all items within a radius of 3 tiles around the rider.
    - Every actor within that radius is blown away to a random empty tile within a radius of 5 tiles that the actor can enter.

### MechSuit
- Grants `CRUSH` ability.
- **Passive effects while mounted:**
    - Each turn, a fire (duration 1) appears on every adjacent tile.
    - With a 60% chance, the tile the suit occupied in the previous turn collapses into a **Hole** that spawns parasites.
- **Special action - Overclock:**
    - The rider sacrifices up to 5 health.
    - For each tile within that reduced health radius, the ground becomes **Dirt** and a fire (duration equal to sacrificed health) is placed there.

### AlienBeast
- Grants `PURIFY` ability.
- **Passive effect while mounted:**
    - If the ground under the rider implements `Consumable`, it is automatically consumed.

---

## Rideable Upgrades

### Bulldozer Plough
- Grants `BULLDOZE` ability.
- Allows the rider to push actors backward and to turn walls into **Sand** (see Ground Interactions).

### Magnetic Field
- Grants `EXTRA_ENERGY` ability.
- **Passive effects while mounted:**
    - Each turn, all items on adjacent tiles are automatically added to the rider's inventory.
    - The tile the rider occupied in the previous turn becomes **Toxic Waste**.
- **Special action - Ignite** (available when an adjacent actor exists):
    - A random item is removed from the rider's inventory.
    - A fire (duration 4) is placed under that adjacent actor.

### Warp Battery
- Grants `EXTRA_ENERGY` ability.
- **Special action - Teleport:**
    - The rider can teleport through a single impassable tile (e.g., a wall or door) to a valid empty tile two steps away.
    - Teleportation always poisons the rider for 3 turns:
        - Normal teleport: 1 poison damage per turn.
        - If the destination contains another actor that can be pushed back: that actor is knocked back one tile, and poison damage increases to 2 per turn.
        - If that actor cannot be pushed back: it is killed outright, poison damage becomes 3 per turn, and all actors within a radius of 3 tiles are also poisoned (3 damage per turn).

---

## Ground Interactions

- **Door** - Can be **crushed** by an actor with `CRUSH`, turning it into **Dirt**.
- **Wall** - Can be **crushed** (turns into **Dirt**) or **bulldozed** (turns into **Sand**).
- **Hole and Vent** - Only actors with `HOVER` can enter them.
- **Toxic Waste** - Damages any actor standing on it each turn, unless that actor has `HOVER`. An actor with `PURIFY` can **purify** a Toxic Waste tile, turning it into **Dirt**. When purified, there is an 80% chance that the purifier and all adjacent actors become poisoned for 2 turns (1 damage per turn).
- **Sand** - Any actor standing on sand is randomly moved to an empty tile within 2 tiles of its current position each turn.

---

## Actor Behaviours

- **EclipseActor** can be **bulldozed**: if an actor with `BULLDOZE` pushes them, they move one tile backward. If that tile is blocked by a wall, another actor, or the map edge, they take damage (5, 3, or 5 respectively).
- **ContractedWorker** - Only hostile actors can attack them, and only if the worker does **not** have the `HOVER` ability (flying workers cannot be attacked).

---

## Status Effect

**RideStatus** - Indicates that an actor is currently mounted on a rideable. This status never expires on its own; it is removed only by dismounting.

---

# REQ 4: Turrets and Projectiles
by Edwin

The company, worried about their ~~workers' wellbeing~~ annual profits, has authorized the deployment of Turrets, which fire Projectiles.
Turrets are grounded structures designed by the company to assist workers' operations, by targeting non-workers and firing at them.
Projectiles are items that have been fired by Turrets, which travel to their target location.

---

**Abstract Class 1: Turret**  
Stationary grounded entities that fire at non-workers.
- Extends Ground.
- Carries a limited number of ammunition (projectiles). Firing a projectile costs 1 ammo. Does not despawn when running out of ammo.
- Has a 3-turn cooldown between firing projectiles to avoid wasting ammo.
- Does not allow actors to walk onto it.
- Logic for handling firing projectiles is up to subclasses of Turret.
- Can check surrounding locations (this radius varies) for any non-workers to target. If multiple targets exist, a random one is chosen.

**Abstract Class 2: Projectile**  
Moving entities that travel toward a destination location set by whoever fired it.
- Extends Item.
- No weight, not portable (obviously).
- Has a destination location (determined by the turret that fired it) that the projectile travels towards.
- Has a velocity, which determines up to how many tiles it can move per turn (diagonals are counted as 1 tile).
- Projectile is stopped (and removed) when it reaches its final destination, or hits an actor or ground that blocks projectiles (blocksThrownObjects method).
- When stopped, it activates its hit effect. This is up to the subclasses of Projectile.
- Walls and Doors should block projectiles.

---

### Turret
**Concrete Class 1: GunTurret**  
This will likely be placed outside the ship in case there's any lifeforms that aren't so welcoming of the ship landing on their moon.
- Detection radius of 7.
- Shoots FireBullets, capacity of 15 ammunition.

**Concrete Class 2: SiphonTurret**  
This experimental tech will likely be placed inside facilities since it's still under development and isn't standard issue.
- Detection radius of 5.
- Shoots SiphonBullets, capacity of 10 ammunition.
- BUT, before it can fire bullets, it needs to register a worker as the "source" for the SiphonBullets to heal.
    - Registration radius of 1 (so to register, a worker needs to stand next to it).
    - Any worker that enters its registration range can be automatically registered. Only one worker may be registered, so if the turret finds multiple workers in range, it'll pick a random one.
    - Deals 3 damage to the worker being registered (this might accidentally kill someone).
    - Workers cannot be un-registered by a turret unless they die. If dead, a turret will need to look for a new worker.
- Note that registration and firing can happen on the same turn.

**Concrete Class 3: NuclearPad**  
This will likely be placed outside a facility on a moon.
- Detection radius of 10.
- Carries 1 big, bad NuclearMissile.
- But, to avoid wrecking the entire place, these cannot fire unless the NuclearPad detects that there is any worker standing adjacent to it for at least 3 consecutive turns.
    - This doesn't have to be the same worker, just any worker.
    - This timer resets if it finds that there are no adjacent workers.

### Projectile
**Concrete Class 1: FireBullet**  
Incendiary bullet that should light up the target on impact. But due to low oxygen levels on moons, the igniter doesn't work half the time. There also isn't enough fuel to create a lasting fire on the ground.
- Velocity of 3.
- Upon hitting something, deals 2 damage to an actor on that location with a 50% chance to burn them (1 damage per turn, lasts 3 turns).
- Note that if no actor is present, the bullet just fizzles out and disappears.

**Concrete Class 2: SiphonBullet**  
A type of bullet that uses weird science to draw life force from targets near the impact point, transferring it to someone. This is so intense that this effect doesn't only affect one tile (rather, a 3x3 area).
- Velocity of 3.
- Has a reference to an actor as the "source" (damage dealt to other actors will be converted to healing for this actor).
- Upon hitting something, deals 3 damage to an actor if they're present on the same tile. Additionally, adjacent actors are also hit for 1 damage.
- "Source" actor will be healed by total damage dealt (note: if a target has 2 hp and gets hit, the source actor still heals by 3 hp).
    - "Source" actors that are dead (0 health) will NOT be healed by this. This healing technology is not necromancy.
    - "Source" actor can still be damaged by this projectile, though they'll be healed afterwards (if they didn't die).

**Concrete Class 3: NuclearMissile**  
The company has authorized the use of extremely low-yield nuclear missiles to (somehow) help with the workers' operations. This isn't powerful enough to destroy an entire moon.
Still, these things are dangerous if not carefully handled! Nuclear blasts are unpredictably destructive here, but you can expect long-lasting fires and significant permanent damage to the terrain.
- Velocity of 1 (slow).
- Upon hitting, it has a blast radius of 4, meaning its impact affects a 9x9 area.
- On the same tile the missile landed:
    - 500 damage to an actor, if they're standing there
    - Spawns a fire, lasting 20 turns
    - Always turns the ground into toxic waste
- For the other tiles around where the missile landed:
    - 5 damage to any actor standing there
    - Spawns a fire, lasting 5-10 turns (random)
    - 50% chance to turn the ground into toxic waste

---

# REQ5: A.P.I - Toxic Atmosphere (HD Requirement)
by Erwyna

### The Pitch

The moon facility has an automated monitor that calls the OpenWeather Air Pollution API using game-state-driven coordinates. The returned AQI and dominant pollutant are converted into changes to health, terrain, economy, and creature behaviour. Severe pollution can also create new hazards and spawn new enemies.

---

### The Mechanics

- The game queries the **OpenWeather Air Pollution API** at runtime, using an API key stored in an environment variable.

- The API key is read via `System.getenv("OPENWEATHER_API_KEY")` and is never committed to the repository.

- The API request is driven by game state via `AtmosphericApiClient`:
  - It derives the latitude/longitude dynamically from the monitor's current map coordinates and converts those coordinates into a bounded city-like search point before issuing the request.
  - This means the exact request changes with the in-game state of the atmospheric monitor rather than using a static URL.

- The raw JSON response is parsed into an `AirQualityReport` by the parser strategy supplied by `AtmosphericServicesFactory`.
  - `OpenWeatherPollutionParser` is the main parser and extracts:
    - `list[0].main.aqi` as an integer AQI index on the OpenWeather **1-5** scale.
    - `list[0].components.no2` and `list[0].components.so2` to determine the dominant pollutant (`"no2"` or `"so2"`).
  - `FallbackPollutionParser` is selected only when `OPENWEATHER_API_KEY` is absent or blank, ensuring the feature remains executable without exposing secrets while safely returning a benign fallback report that disables atmospheric corruption.

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
  - Interprets sulphur dioxide (`SO2`) as a proxy for economic disruption.
  - When the dominant pollutant in `AirQualityReport` is `"so2"`, it:
    - Sets a global disruption flag `SuperComputer.isEconomyDisrupted()` to `true`.
    - Iterates over all tiles in the `GameMap` and, for any actor that tracks `EclipseStatistics.CREDITS`, reduces that statistic by 10 with a floor at 0.
  - The disruption flag is consumed by the REQ1 shop system:
    - When `SuperComputer.isEconomyDisrupted()` is `true`, `SellAction` still removes the item from the seller's inventory, but there is a **50% chance** that the **payout is 0 credits** instead of the normal price.
    - When the flag is `false`, `SellAction` behaves normally.

- **PollutantSpawnCorruptor**
  - Reacts to severe AQI by spawning one `Undead` on a randomly selected valid empty tile adjacent to the monitor's location.

- The atmospheric system is driven by a dedicated monitor ground:
  - `AtmosphericMonitor` is a stationary `Ground` that represents the facility's automated probe. Its map `Location`, supplied by the engine on each `tick`, is used as the centre point for atmosphere effects without needing any extra marker interface.
  - It owns an `EnvironmentalMonitorController`, which keeps an internal tick counter.
  - To keep testing simple and make the feature observable in a short demo, the behaviour is configured to trigger a new scan every turn through a named refresh-interval constant.
  - During each ground tick, the controller delegates directly to `AtmosphericScanner`.
  - `AtmosphericScanner` calls the API via `AtmosphericApiClient`, parses the JSON with the selected parser, and then invokes each configured `AtmosphericCorruptor` with the resulting `AirQualityReport`.

### Testing Highlights

- REQ5 unit tests are located in `src/test/game/atmosphere` and are intended to validate the new atmospheric feature classes rather than pre-existing engine internals.
- The tests currently cover:
  - `AirQualityReport` as the data carrier for AQI and dominant pollutant.
  - `OpenWeatherPollutionParser` for valid payload parsing and safe default behaviour on null or incomplete JSON.
  - `FallbackPollutionParser` for the no-API-key safe fallback path.
  - `EconomyCorruptor` for SO2-driven disruption logic.
  - `HazardCorruptor` and `PollutantSpawnCorruptor` for threshold guard behaviour, confirming that safe or below-severe AQI values do not trigger corruption or spawning.
- This keeps the testing aligned with the assignment guidance by focusing primarily on the new REQ5 abstractions and rules.

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
| AtmosphericMonitor            | New      | Ground                                  | Stationary probe ground. Its map `Location`, supplied by the engine on each `tick`, is what every downstream class uses as the centre point for atmosphere effects. |
| EnvironmentalMonitorController | New      | -                                       | Triggers periodic atmospheric scans and coordinates the API pipeline through abstractions. |
| AtmosphericScanner         | New      | Action                                  | Fetches JSON, parses it, prints an AQI summary, and invokes all registered `AtmosphericCorruptor`s. |
| HazardCorruptor               | New      | AtmosphericCorruptor                    | Calls `applyAtmosphere` on all `AtmosphereSensitiveActor`s; at moderate AQI spreads local toxic puddles; at severe AQI creates a border ring and monitor hotspot. |
| EconomyCorruptor              | New      | AtmosphericCorruptor                    | `SO2`-driven: reduces credits for tracked actors and toggles a disruption flag consumed by `SellAction` to probabilistically void transactions. |
| PollutantSpawnCorruptor       | New      | AtmosphericCorruptor                    | At severe AQI, spawns one `Undead` on a valid empty tile adjacent to the monitor's location. |
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
        "no2": 18.7,
        "so2": 42.1
      }
    }
  ]
}
```

The game reads `list[0].main.aqi` and compares `components.no2` and `components.so2` to determine the dominant pollutant.

### API Key Setup

To run this feature with the real OpenWeather Air Pollution API, the marker or teammate must first create their own API key.

1. Go to [OpenWeather sign in](https://home.openweathermap.org/users/sign_in). If you do not already have an account, create one first.
2. After logging in, open the [API keys page](https://home.openweathermap.org/api_keys).
3. In the key creation section, enter a label such as `eclipse-nebula-req5` or any other name you prefer, then generate the key.
4. Copy the generated API key and store it locally as an environment variable named `OPENWEATHER_API_KEY`.
5. Run the game from the same environment so `System.getenv("OPENWEATHER_API_KEY")` can read the key at runtime.

**Security note:** The API key must never be committed to GitLab or hardcoded in the source code.

**Activation note:** A newly generated OpenWeather API key may take some time to activate, so if the request initially returns an authorization error, wait a while and try again.