package game.actions;

import game.grounds.QuotaManager;
import game.items.Depositable;
import org.junit.jupiter.api.Test;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DepositAction (REQ1).
 * Tests that company credits are added to QuotaManager,
 * that depositedBy() is called, and the menu description is correct.
 *
 * Uses Mockito to isolate DepositAction from real engine objects.
 *
 * @author eche0116
 */
class DepositActionTest {

    /**
     * execute() must call quotaManager.addCompanyCredits() with the correct value.
     * Three deposit values: 10 (fan), 50 (scrap), 100 (artifact).
     */
    @Test
    void execute_typicalCondition_addsCorrectCreditsToQuotaManager() {
        Actor actor = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        QuotaManager quotaManager = mock(QuotaManager.class);

        Depositable fan = mock(Depositable.class);
        when(fan.getDepositValue()).thenReturn(10);
        when(fan.depositedBy(actor, map)).thenReturn("Fan deposited.");
        new DepositAction(fan, quotaManager).execute(actor, map);
        verify(quotaManager).addCompanyCredits(10);

        Depositable scrap = mock(Depositable.class);
        when(scrap.getDepositValue()).thenReturn(50);
        when(scrap.depositedBy(actor, map)).thenReturn("Scrap deposited.");
        new DepositAction(scrap, quotaManager).execute(actor, map);
        verify(quotaManager).addCompanyCredits(50);

        Depositable artifact = mock(Depositable.class);
        when(artifact.getDepositValue()).thenReturn(100);
        when(artifact.depositedBy(actor, map)).thenReturn("Artifact deposited.");
        new DepositAction(artifact, quotaManager).execute(actor, map);
        verify(quotaManager).addCompanyCredits(100);
    }

    /**
     * execute() must call depositedBy() on the item and return its result.
     * Three return messages checked.
     */
    @Test
    void execute_typicalCondition_delegatesToDepositableAndReturnsResult() {
        Actor actor = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        QuotaManager quotaManager = mock(QuotaManager.class);

        Depositable d1 = mock(Depositable.class);
        when(d1.getDepositValue()).thenReturn(10);
        when(d1.depositedBy(actor, map)).thenReturn("Fan deposited. Worker healed!");
        assertEquals("Fan deposited. Worker healed!",
                new DepositAction(d1, quotaManager).execute(actor, map));

        Depositable d2 = mock(Depositable.class);
        when(d2.getDepositValue()).thenReturn(50);
        when(d2.depositedBy(actor, map)).thenReturn("Scrap deposited. Worker cut!");
        assertEquals("Scrap deposited. Worker cut!",
                new DepositAction(d2, quotaManager).execute(actor, map));

        Depositable d3 = mock(Depositable.class);
        when(d3.getDepositValue()).thenReturn(100);
        when(d3.depositedBy(actor, map)).thenReturn("Artifact deposited. Teleported!");
        assertEquals("Artifact deposited. Teleported!",
                new DepositAction(d3, quotaManager).execute(actor, map));
    }

    /**
     * addCompanyCredits() must be called before depositedBy() in the same execute().
     * Verified via Mockito's inOrder for three deposit types.
     */
    @Test
    void execute_boundaryCondition_creditsAddedBeforeSideEffects() {
        Actor actor = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        QuotaManager quotaManager = mock(QuotaManager.class);

        Depositable fan = mock(Depositable.class);
        when(fan.getDepositValue()).thenReturn(10);
        when(fan.depositedBy(actor, map)).thenReturn("");
        new DepositAction(fan, quotaManager).execute(actor, map);
        var order1 = inOrder(quotaManager, fan);
        order1.verify(quotaManager).addCompanyCredits(10);
        order1.verify(fan).depositedBy(actor, map);

        Depositable scrap = mock(Depositable.class);
        when(scrap.getDepositValue()).thenReturn(50);
        when(scrap.depositedBy(actor, map)).thenReturn("");
        new DepositAction(scrap, quotaManager).execute(actor, map);
        var order2 = inOrder(quotaManager, scrap);
        order2.verify(quotaManager).addCompanyCredits(50);
        order2.verify(scrap).depositedBy(actor, map);

        Depositable artifact = mock(Depositable.class);
        when(artifact.getDepositValue()).thenReturn(100);
        when(artifact.depositedBy(actor, map)).thenReturn("");
        new DepositAction(artifact, quotaManager).execute(actor, map);
        var order3 = inOrder(quotaManager, artifact);
        order3.verify(quotaManager).addCompanyCredits(100);
        order3.verify(artifact).depositedBy(actor, map);
    }

    /**
     * menuDescription must show actor name, item name, and credit amount.
     * Three deposit values checked.
     */
    @Test
    void menuDescription_typicalCondition_includesActorItemAndCredits() {
        QuotaManager quotaManager = mock(QuotaManager.class);
        Actor actor = mock(Actor.class);
        when(actor.toString()).thenReturn("Bob");

        Depositable fan = mock(Depositable.class);
        when(fan.getDepositValue()).thenReturn(10);
        when(fan.toString()).thenReturn("Industrial Fan");
        String desc1 = new DepositAction(fan, quotaManager).menuDescription(actor);
        assertTrue(desc1.contains("Bob"));
        assertTrue(desc1.contains("10"));

        Depositable scrap = mock(Depositable.class);
        when(scrap.getDepositValue()).thenReturn(50);
        when(scrap.toString()).thenReturn("Aluminium Scrap");
        String desc2 = new DepositAction(scrap, quotaManager).menuDescription(actor);
        assertTrue(desc2.contains("50"));

        Depositable artifact = mock(Depositable.class);
        when(artifact.getDepositValue()).thenReturn(100);
        when(artifact.toString()).thenReturn("Alien Artifact");
        String desc3 = new DepositAction(artifact, quotaManager).menuDescription(actor);
        assertTrue(desc3.contains("100"));
    }
}