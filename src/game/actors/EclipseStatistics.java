package game.actors;

/**
 * Stats that are unique to actors in this game. Right now there's just credits,
 * but living in its own enum keeps it clear of the engine's locked
 * {@code ActorStatistics} (HEALTH/STAMINA/MANA) which we CANNOT extend.
 *
 * @author esoo0013
 */
public enum EclipseStatistics {
    CREDITS,
    INFECTION_PROGRESS,
}