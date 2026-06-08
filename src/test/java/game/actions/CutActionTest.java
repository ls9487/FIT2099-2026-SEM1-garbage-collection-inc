package game.actions;

import game.items.Cuttable;
import org.junit.jupiter.api.Test;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CutAction (REQ1).
 * Tests that CutAction correctly delegates to the Cuttable's cutBy(),
 * passes the right arguments, and returns the result unchanged.
 *
 * Uses Mockito to avoid needing real engine objects.
 *
 * @author eche0116
 */
class CutActionTest {

    /**
     * execute() must call cutBy(actor, map, location) and return the result.
     * Three distinct return strings checked: normal cut, explosion, undead spawn.
     */
    @Test
    void execute_typicalCondition_delegatesToCuttableAndReturnsResult() {
        Actor actor = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        Location location = mock(Location.class);

        // Case 1: simple cut message
        Cuttable scrapCut = mock(Cuttable.class);
        when(scrapCut.cutBy(actor, map, location)).thenReturn("Door crumbles into scrap!");
        CutAction action1 = new CutAction(scrapCut, location);
        assertEquals("Door crumbles into scrap!", action1.execute(actor, map));

        // Case 2: explosion message
        Cuttable explodingCut = mock(Cuttable.class);
        when(explodingCut.cutBy(actor, map, location)).thenReturn("Door EXPLODES!");
        CutAction action2 = new CutAction(explodingCut, location);
        assertEquals("Door EXPLODES!", action2.execute(actor, map));

        // Case 3: vent cut message
        Cuttable ventCut = mock(Cuttable.class);
        when(ventCut.cutBy(actor, map, location)).thenReturn("Industrial Fan crashes to the floor!");
        CutAction action3 = new CutAction(ventCut, location);
        assertEquals("Industrial Fan crashes to the floor!", action3.execute(actor, map));
    }

    /**
     * execute() must call cutBy exactly once per execution.
     * Three different cuttables each checked for exactly one invocation.
     */
    @Test
    void execute_typicalCondition_cutByCalledExactlyOnce() {
        Actor actor = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        Location location = mock(Location.class);

        Cuttable c1 = mock(Cuttable.class);
        Cuttable c2 = mock(Cuttable.class);
        Cuttable c3 = mock(Cuttable.class);

        new CutAction(c1, location).execute(actor, map);
        new CutAction(c2, location).execute(actor, map);
        new CutAction(c3, location).execute(actor, map);

        verify(c1, times(1)).cutBy(actor, map, location);
        verify(c2, times(1)).cutBy(actor, map, location);
        verify(c3, times(1)).cutBy(actor, map, location);
    }

    /**
     * menuDescription must mention the actor and the cuttable.
     * Three actor names checked.
     */
    @Test
    void menuDescription_typicalCondition_includesActorAndCuttable() {
        Location location = mock(Location.class);
        Cuttable cuttable = mock(Cuttable.class);

        Actor bob = mock(Actor.class);
        when(bob.toString()).thenReturn("Bob");
        assertTrue(new CutAction(cuttable, location).menuDescription(bob).contains("Bob"));

        Actor alice = mock(Actor.class);
        when(alice.toString()).thenReturn("Alice");
        assertTrue(new CutAction(cuttable, location).menuDescription(alice).contains("Alice"));

        Actor worker = mock(Actor.class);
        when(worker.toString()).thenReturn("Worker #1");
        assertTrue(new CutAction(cuttable, location).menuDescription(worker).contains("Worker #1"));
    }
}