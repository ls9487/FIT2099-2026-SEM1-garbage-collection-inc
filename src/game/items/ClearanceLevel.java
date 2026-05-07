package game.items;

/**
 * What tier of door the access card opens. Stored on AccessCard and
 * (later, in REQ2) compared against the door's required level.
 *
 * @author esoo0013
 */
public enum ClearanceLevel {
    LEVEL_1,
    LEVEL_2,
    LEVEL_3
}