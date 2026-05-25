# REQ5 Checkpoint Note

## Quick overview

This is a short summary of how my REQ5 feature fits together so it is easier to explain during the checkpoint.

My REQ5 idea is a **Toxic Atmosphere** system. The game calls a real air-pollution API, turns the JSON into an `AirQualityReport`, and then uses that report to change the game world, economy, spawning, and actor behaviour.

## Main parts

| Part | Type | New / Existing | Connects to | What it does |
|---|---|---|---|---|
| `AtmosphereSensitiveActor` | Interface | New | `ContractedWorker`, `Muckraker`, `Undead` | This is the shared contract for actors that react to air quality in their own way. |
| `ContractedWorker` | Concrete class | Existing, retrofitted | `AtmosphereSensitiveActor` | Lets workers react to toxic atmosphere with worker-specific penalties/effects. |
| `Muckraker` | Concrete class | Existing, retrofitted | `AtmosphereSensitiveActor` | Lets muckrakers react to toxic atmosphere differently from workers. |
| `Undead` | Concrete class | Existing, retrofitted | `AtmosphereSensitiveActor`, `ToxicWaste` | Lets undead corrupt the ground and damage nearby actors when pollution is severe. |
| `AtmosphericCorruptor` | Interface | New | `HazardCorruptor`, `EconomyCorruptor`, `PollutantSpawnCorruptor` | This is the shared contract for map-wide corruption effects caused by the atmosphere. |
| `HazardCorruptor` | Concrete class | New | `AtmosphericCorruptor`, `AirQualityReport` | Uses pollution data to create environmental hazards like toxic waste. |
| `EconomyCorruptor` | Concrete class | New | `AtmosphericCorruptor`, `AirQualityReport`, `SellAction` | Uses pollution data to affect the game economy and selling results. |
| `PollutantSpawnCorruptor` | Concrete class | New | `AtmosphericCorruptor`, `AtmosphericAnchor`, `Undead` | Uses severe AQI to spawn an undead near the monitor. |
| `PollutionDataParser` | Interface | New supporting abstraction | `OpenWeatherPollutionParser`, `FallbackPollutionParser` | This converts raw API JSON into a usable in-game report object. |
| `OpenWeatherPollutionParser` | Concrete class | New | `PollutionDataParser`, `AirQualityReport` | Parses the real API response into game data. |
| `FallbackPollutionParser` | Concrete class | New | `PollutionDataParser`, `AirQualityReport` | Gives a safe fallback report if the API setup is missing. |
| `AirQualityReport` | Data class | New | Parsers, corruptors, atmosphere-sensitive actors | Stores the AQI and pollutant info that gets passed through the REQ5 system. |
| `AtmosphericApiClient` | Service class | New | `AtmosphericScanAction`, `EnvironmentalMonitorBehaviour`, `HttpClient` | Sends the API request and gets the JSON back. |
| `AtmosphericServicesFactory` | Factory class | New | Parser implementations, corruptor implementations | Centralises object creation so higher-level classes depend on abstractions, not concrete classes. |
| `AtmosphericAnchor` | Interface | New | `AtmosphericMonitor` | Marks the monitor as the anchor point for pollution-based spawning. |
| `AtmosphericMonitor` | Concrete class | New | `AtmosphericAnchor`, `EnvironmentalMonitorBehaviour` | Represents the in-game monitor that owns the scanning behaviour. |
| `EnvironmentalMonitorBehaviour` | Behaviour class | New | `AtmosphericApiClient`, `PollutionDataParser`, `AtmosphericServicesFactory`, `AtmosphericScanAction` | Controls the scan cycle and prepares the action that applies the API result to the game. |
| `AtmosphericScanAction` | Action class | New | `AtmosphericApiClient`, `PollutionDataParser`, `AtmosphericCorruptor` | Actually runs the scan: call API, parse data, create report, then apply corruptors. |

## Basic Core idea

| Question | Answer                                                                                                                                                                      |
|---|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| What are the two main REQ5 abstractions? | `AtmosphereSensitiveActor` and `AtmosphericCorruptor`.                                                                                                                      |
| Which old classes did we retrofit? | `ContractedWorker`, `Muckraker`, and `Undead`.                                                                                                                              |
| Which new concrete corruptors did we add? | `HazardCorruptor`, `EconomyCorruptor`, and `PollutantSpawnCorruptor`.                                                                                                       |
| What does the API actually affect? | Terrain, economy, enemy spawning, and actor behaviour.                                                                                                                      |
| Where does the API data go? | `AtmosphericApiClient` gets it, a parser turns it into `AirQualityReport`, then actions/corruptors apply it to the game.                                                    |
| How do we show DIP? | `EnvironmentalMonitorBehaviour` and `AtmosphericScanAction` depend on abstractions like `PollutionDataParser` and `AtmosphericCorruptor`, not directly on concrete classes. |

All in all, REQ5 is: the monitor scans real pollution data, turns it into an `AirQualityReport`, and then different parts of the game react to it. Some reactions are map-wide through `AtmosphericCorruptor`, and some are actor-specific through `AtmosphereSensitiveActor`.
