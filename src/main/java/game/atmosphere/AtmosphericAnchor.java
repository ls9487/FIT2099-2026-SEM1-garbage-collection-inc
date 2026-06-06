package game.atmosphere;

/**
 * Marker interface for map elements that act as an atmospheric anchor in REQ5.
 *
 * A ground implementing this interface can be located by atmosphere-related
 * systems that need a central point for corruption effects, such as creating a
 * hotspot around the monitor or spawning pollution-based enemies nearby.
 *
 * @author esoo0013
 */
public interface AtmosphericAnchor {
}
