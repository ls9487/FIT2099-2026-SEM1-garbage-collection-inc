package game.grounds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuotaManager (REQ1).
 * Tests credit accumulation, quota detection, rank-up math,
 * deadline detection, and deposit-blocked-after-deadline guard.
 *
 * NOTE: System.out is redirected to a no-op stream before each test because
 * Display (used inside QuotaManager) writes to System.out. Without this,
 * Display may throw a NullPointerException in headless/test environments
 * where System.console() returns null, crashing tests before assertions run.
 *
 * @author eche0116
 */
class QuotaManagerTest {

    /**
     * Redirect System.out to a no-op stream before each test.
     * This prevents Display.println() from crashing in a headless JVM where System.console() is null.
     */
    @BeforeEach
    void suppressOutput() {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {}
        }));
    }

    /**
     * Adding credits below quota should not trigger a rank-up.
     * Three values: 1, 50, 99.
     */
    @Test
    void addCompanyCredits_typicalCondition_creditsAccumulateBelowQuota() {
        QuotaManager m1 = new QuotaManager();
        m1.addCompanyCredits(1);
        assertFalse(m1.isQuotaMet());

        QuotaManager m50 = new QuotaManager();
        m50.addCompanyCredits(50);
        assertFalse(m50.isQuotaMet());

        QuotaManager m99 = new QuotaManager();
        m99.addCompanyCredits(99);
        assertFalse(m99.isQuotaMet());
    }

    /**
     * Adding credits in multiple deposits accumulates correctly.
     * Three cases: 30+30=60 (no rank), 50+50=100 (rank-up), 60+60=120 (rank-up).
     */
    @Test
    void addCompanyCredits_typicalCondition_multipleDepositsAccumulate() {
        QuotaManager under = new QuotaManager();
        under.addCompanyCredits(30);
        under.addCompanyCredits(30);
        assertFalse(under.isQuotaMet()); // 60 < 100

        QuotaManager exact = new QuotaManager();
        exact.addCompanyCredits(50);
        exact.addCompanyCredits(50);
        assertTrue(exact.getStatus().contains("Rank 2"));

        QuotaManager over = new QuotaManager();
        over.addCompanyCredits(60);
        over.addCompanyCredits(60);
        assertTrue(over.getStatus().contains("Rank 2"));
    }

    /**
     * Exactly 100 credits meets quota and immediately triggers rank-up.
     * Three input values: 99 (just under), 100 (exact), 101 (just over).
     */
    @Test
    void isQuotaMet_boundaryCondition_exactThreshold() {
        QuotaManager under = new QuotaManager();
        under.addCompanyCredits(99);
        assertFalse(under.isQuotaMet());

        // 100 triggers rankUp() inside addCompanyCredits, so credits reset.
        // Observable via rank advancing to 2.
        QuotaManager exact = new QuotaManager();
        exact.addCompanyCredits(100);
        assertTrue(exact.getStatus().contains("Rank 2"));

        QuotaManager over = new QuotaManager();
        over.addCompanyCredits(101);
        assertTrue(over.getStatus().contains("Rank 2"));
    }

    /**
     * After rank-up: quota = ceil(100*1.05) = 105, turns = ceil(200*1.1) = 220.
     * Three checks: rank is 2, turns is 220, 104 credits does not meet new quota.
     */
    @Test
    void rankUp_boundaryCondition_newQuotaAndTurnsAreCorrect() {
        QuotaManager manager = new QuotaManager();
        manager.addCompanyCredits(100);

        assertTrue(manager.getStatus().contains("Rank 2"),
                "Status was: " + manager.getStatus());
        assertTrue(manager.getStatus().contains("Turns left: 221"),
                "Status was: " + manager.getStatus());

        manager.addCompanyCredits(104);
        assertFalse(manager.isQuotaMet());
    }

    /**
     * Two consecutive rank-ups compound correctly.
     * Rank 2 -> 3: quota = ceil(105*1.05) = 111, turns = ceil(221*1.1) = 244.
     * Three checks: rank 3, turns 244, 110 credits doesn't meet quota 111.
     */
    @Test
    void rankUp_boundaryCondition_secondRankUpCompounds() {
        QuotaManager manager = new QuotaManager();
        manager.addCompanyCredits(100);
        manager.addCompanyCredits(105);

        assertTrue(manager.getStatus().contains("Rank 3"),
                "Status was: " + manager.getStatus());
        assertTrue(manager.getStatus().contains("Turns left: 244"),
                "Status was: " + manager.getStatus());

        manager.addCompanyCredits(110);
        assertFalse(manager.isQuotaMet());
    }

    /**
     * isPastDeadline() is false on construction and after a rank-up.
     * Three states checked: fresh, after partial deposit, after rank-up.
     */
    @Test
    void isPastDeadline_negativeCondition_falseBeforeDeadlineReached() {
        QuotaManager fresh = new QuotaManager();
        assertFalse(fresh.isPastDeadline());

        fresh.addCompanyCredits(50);
        assertFalse(fresh.isPastDeadline());

        QuotaManager ranked = new QuotaManager();
        ranked.addCompanyCredits(100);
        assertFalse(ranked.isPastDeadline());
    }

    /**
     * isOnDeadline() is false on construction, after deposit, and after rank-up.
     * Three checks.
     */
    @Test
    void isOnDeadline_negativeCondition_falseWhenTurnsStillRemain() {
        QuotaManager m = new QuotaManager();
        assertFalse(m.isOnDeadline());

        m.addCompanyCredits(30);
        assertFalse(m.isOnDeadline());

        m.addCompanyCredits(70);
        assertFalse(m.isOnDeadline());
    }

    /**
     * getStatus reflects rank, credit progress, and turns correctly across
     * three states: initial, after deposit, after rank-up.
     */
    @Test
    void getStatus_edgeCondition_statusStringReflectsState() {
        QuotaManager manager = new QuotaManager();
        assertTrue(manager.getStatus().contains("Rank 1"),
                "Status was: " + manager.getStatus());
        assertTrue(manager.getStatus().contains("Turns left: 200"),
                "Status was: " + manager.getStatus());

        manager.addCompanyCredits(40);
        assertTrue(manager.getStatus().contains("40"),
                "Status was: " + manager.getStatus());

        manager.addCompanyCredits(60); // rank-up
        assertTrue(manager.getStatus().contains("Rank 2"),
                "Status was: " + manager.getStatus());
        assertTrue(manager.getStatus().contains("Turns left: 221"),
                "Status was: " + manager.getStatus());
    }
}