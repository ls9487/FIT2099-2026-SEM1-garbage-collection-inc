# Vehicle System Implementation

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