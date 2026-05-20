# Feature Proposal - Assignment 3

## REQ3 & REQ4: Creative Mode

> *(To be filled in by Edwin/Li Shen for their custom creative requirements.)*

---

## REQ5: A.P.I - The Signal (HD Requirement)

---

### The Pitch

The abandoned moon's long-range communication array intercepts classified aggregate crime statistics transmitted from Earth's law enforcement networks. These transmissions; cold, numeric, impersonal, are parsed by the facility's corrupted AI and translated into physical anomalies: entities spawn, terrain degrades, and the economy destabilises in direct proportion to the severity of the data received. The player may manually tune into The Signal as an action, or the facility's `BroadcastBehaviour` will passively trigger it every fixed number of turns.

---

### The Mechanics

- The player or the facility passively queries the **FBI Crime Data Explorer API** (`https://api.usa.gov/crime/fbi/cde/`) at runtime.
- The API query is **dynamically constructed** from the current game state:
  - The **worker's current HP** is mapped to a US state abbreviation (e.g., HP 1–10 --> `"AL"`, 11–20 --> `"AK"`, up to 50 states).
  - The **current turn number** determines the year range queried (`from = 2010 + (turn / 50)`, `to = from + 1`).
  - The **current Company Rank** selects which crime category is emphasised in the effect resolution (Rank 1 --> violent crime, Rank 2 --> property crime, Rank 3 --> arson).
- The JSON response is parsed by the appropriate `CrimeDataParser` implementation.
- The parsed `CrimeReport` object is passed to a `FacilityCorruptor` implementation, which applies a **complex, state-mutating effect** to the game world:
  - `violent_crime` value --> spawns `ceil(violent_crime / 500)` Scrap Snatchers on random valid tiles.
  - `homicide` value --> if > 500, all Undead on the current map gain the **Frenzied** status (move twice per turn, deal +2 damage).
  - `aggravated_assault` value --> every 100 points destroys one Aluminium Door on the map (tile becomes Floor, drops Aluminium Scrap).
  - `property_crime` value --> sets the Supercomputer's quota multiplier for the current cycle (`property_crime / 1000 × base quota`, rounded up).
  - `arson` value --> if > 200, converts 3 random Floor tiles into **Burning Ground** (actors standing on them take 1 damage per turn).
  - `robbery` value --> disables Supercomputer buy/sell interactions for `floor(robbery / 100)` turns.
- A descriptive message is printed to the console each time a corruption effect fires, identifying which data field triggered it and what changed.
- The API key is loaded exclusively from an **environment variable** (`FBI_API_KEY`) and is never stored in source code or committed to the repository.

---

### The Architecture

#### New Abstractions

| # | Name | Type | Responsibility |
|---|------|------|----------------|
| 1 | `CrimeDataParser` | Interface | Parses a raw JSON response into a typed `CrimeReport` object; exposes `parse(String json)` and `getCategory()` |
| 2 | `FacilityCorruptor` | Interface | Accepts a `CrimeReport` and a `GameMap` reference to apply a complex, stateful corruption effect; exposes `corrupt(GameMap map, CrimeReport report)` and `getSeverity()` |

#### Concrete Classes

| Class | New / Retrofitted | Implements | Description |
|-------|-------------------|------------|-------------|
| `ViolentCrimeParser` | **New** | `CrimeDataParser` | Parses `violent_crime`, `homicide`, `aggravated_assault` fields from JSON |
| `PropertyCrimeParser` | **New** | `CrimeDataParser` | Parses `property_crime`, `robbery` fields from JSON |
| `ArsonCrimeParser` | **New** | `CrimeDataParser` | Parses `arson` field from JSON |
| `EntitySpawnCorruptor` | **New** | `FacilityCorruptor` | Spawns Scrap Snatchers and applies Frenzied status to Undead based on violent crime data |
| `TerrainCorruptor` | **New** | `FacilityCorruptor` | Destroys Aluminium Doors and converts Floor tiles to Burning Ground based on assault/arson data |
| `EconomyCorruptor` | **New** | `FacilityCorruptor` | Mutates the quota multiplier and temporarily disables the Supercomputer based on property/robbery data |

#### Higher-Level Classes (Dependency Inversion Principle)

Both classes depend **strictly on the abstractions** (`CrimeDataParser`, `FacilityCorruptor`) - never on the concrete implementations.

| Class | Type | Role |
|-------|------|------|
| `CrimeSignalAction` | `Action` | Player-triggered action (e.g., "Tune into The Signal"). Constructs the dynamic API URL from game state, calls `CrimeDataParser.parse()`, then delegates to the injected `FacilityCorruptor.corrupt()`. |
| `BroadcastBehaviour` | `Behaviour` | Passive auto-trigger attached to the facility's communication tower actor. Every `N` turns, it selects the appropriate `CrimeDataParser` and `FacilityCorruptor` based on Company Rank and fires the corruption pipeline automatically. |

---

### The Request

**Endpoint pattern:**

```
GET https://api.usa.gov/crime/fbi/cde/api/data/summarized/state/{stateCode}/all
    ?API_KEY={FBI_API_KEY}
    &from={2010 + (currentTurn / 50)}
    &to={2011 + (currentTurn / 50)}
```

**Example (Worker HP = 15, Turn = 100, Company Rank = 1):**

```
GET https://api.usa.gov/crime/fbi/cde/api/data/summarized/state/AK/all
    ?API_KEY=abc123xyz
    &from=2012
    &to=2013
```

**Game-state variables driving the query:**

| Variable | Query Parameter Affected | Derivation |
|----------|--------------------------|------------|
| `workerHP` | `stateCode` path segment | `HP % 50` mapped to a 50-entry String array of state abbreviations |
| `currentTurn` | `from` and `to` year values | `from = 2010 + (turn / 50)`, `to = from + 1` |
| `companyRank` | Which `CrimeDataParser` is instantiated | Rank 1 --> `ViolentCrimeParser`, Rank 2 --> `PropertyCrimeParser`, Rank 3 --> `ArsonCrimeParser` |

---

### The Schema

**Expected JSON response structure:**

```json
{
  "results": [
    {
      "state_abbr": "AK",
      "year": 2012,
      "violent_crime": 1423,
      "homicide": 34,
      "rape_legacy": 178,
      "robbery": 201,
      "aggravated_assault": 1010,
      "property_crime": 6724,
      "burglary": 1382,
      "larceny": 4891,
      "motor_vehicle_theft": 451,
      "arson": 87
    }
  ]
}
```

**Fields parsed and their in-game use:**

| JSON Field | Type | Game Effect |
|---|---|---|
| `violent_crime` | Integer | Spawns `ceil(value / 500)` Scrap Snatchers on random valid tiles |
| `homicide` | Integer | If > 500 --> all Undead gain Frenzied status |
| `aggravated_assault` | Integer | Every 100 points --> one Aluminium Door destroyed (tile --> Floor, drops Scrap) |
| `property_crime` | Integer | Sets quota multiplier: `ceil(value / 1000) × baseQuota` |
| `robbery` | Integer | Disables Supercomputer for `floor(value / 100)` turns |
| `arson` | Integer | If > 200 --> 3 random Floor tiles become Burning Ground |

---

### SOLID Principles Applied

| Principle | Application                                                                                                                                                                                                                                                          |
|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Single Responsibility (SRP)** | `CrimeDataParser` only parses JSON; `FacilityCorruptor` only applies game effects. Neither knows about the other.                                                                                                                                                    |
| **Open/Closed (OCP)** | New crime categories (e.g., drug offences) require only a new `CrimeDataParser` + `FacilityCorruptor` pair - no existing class is modified.                                                                                                                          |
| **Liskov Substitution (LSP)** | Any `FacilityCorruptor` implementation can be substituted in `BroadcastBehaviour` without breaking the corruption pipeline.                                                                                                                                          |
| **Interface Segregation (ISP)** | `CrimeDataParser` and `FacilityCorruptor` are intentionally kept as separate, narrow interfaces - corruptors are never forced to implement parsing logic, and vice versa.                                                                                            |
| **Dependency Inversion (DIP)** | `CrimeSignalAction` and `BroadcastBehaviour` are constructed with injected interface references. Concrete classes are instantiated once (at the rank/HP resolution layer) and passed in - the higher-level classes never call `new ViolentCrimeParser()` or similar. |

---

### Security Notes

- The API key is stored as an **environment variable**: `FBI_API_KEY`
- In Java: `String apiKey = System.getenv("FBI_API_KEY");`
- The key is **never** stored in source code, config files committed to GitLab, or hardcoded strings
- A `.env.example` file (with a placeholder value) is included in the repo root to document the expected variable name

---

### README Setup Instructions (Summary)

Full instructions are in `README.md`. In brief:

1. Register a free API key at [https://api.usa.gov/crime/fbi/cde/](https://api.usa.gov/crime/fbi/cde/)
2. Set the environment variable before running:
   - **macOS/Linux:** `export FBI_API_KEY=your_key_here`
   - **Windows CMD:** `set FBI_API_KEY=your_key_here`
   - **IntelliJ:** Add `FBI_API_KEY=your_key_here` under Run --> Edit Configurations --> Environment Variables
3. Run the project via IntelliJ or `./gradlew run` as normal
4. Unit tests that involve API calls use a **mock/stub** of `CrimeDataParser` and do not require a live key

---

### Unit Testing Notes

- `ViolentCrimeParser`, `PropertyCrimeParser`, and `ArsonCrimeParser` are tested with **at least three JSON input variants** each: a normal response, a boundary case (values at exactly the threshold, e.g., `homicide = 500`), and an edge case (empty results array, zero values).
- `EntitySpawnCorruptor`, `TerrainCorruptor`, and `EconomyCorruptor` are tested using **mocked `GameMap` and `CrimeReport` objects** to verify correct spawn counts, tile mutations, and quota changes without requiring a live API call.
- `CrimeSignalAction` is integration-tested with a stubbed `CrimeDataParser` that returns a fixed `CrimeReport`, verifying the end-to-end corruption pipeline fires correctly.

