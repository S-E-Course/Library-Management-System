package com.library.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FineSummary
 */
class FineSummaryTest {

    /**
     * Checks that positive amounts are stored and total is updated.
     */
    @Test
    void addStoresAmountAndUpdatesTotal() {
        FineSummary summary = new FineSummary();

        summary.add("book", 10.0);
        summary.add("book", 5.0);

        Map<String, Double> perType = summary.getPerType();
        assertEquals(15.0, perType.get("book"));
        assertEquals(15.0, summary.getTotal());
    }

    /**
     * Checks that non-positive amounts are ignored.
     */
    @Test
    void addIgnoresNonPositiveAmounts() {
        FineSummary summary = new FineSummary();

        summary.add("book", 0.0);
        summary.add("book", -5.0);

        assertTrue(summary.getPerType().isEmpty());
        assertEquals(0.0, summary.getTotal());
    }

    /**
     * Checks that a null media type is stored under the key "unknown".
     */
    @Test
    void addUsesUnknownForNullType() {
        FineSummary summary = new FineSummary();

        summary.add(null, 8.0);

        Map<String, Double> perType = summary.getPerType();
        assertEquals(8.0, perType.get("unknown"));
        assertEquals(8.0, summary.getTotal());
    }

    /**
     * Checks that type strings are trimmed and converted to lower case.
     */
    @Test
    void addNormalizesTypeToLowerCaseAndTrim() {
        FineSummary summary = new FineSummary();

        summary.add("  BOOK  ", 4.0);

        Map<String, Double> perType = summary.getPerType();
        assertTrue(perType.containsKey("book"));
        assertEquals(4.0, perType.get("book"));
        assertEquals(4.0, summary.getTotal());
    }

    /**
     * Checks that the returned map cannot be modified from outside.
     */
    @Test
    void getPerTypeIsUnmodifiable() {
        FineSummary summary = new FineSummary();
        summary.add("book", 3.0);

        Map<String, Double> perType = summary.getPerType();

        boolean thrown = false;
        try {
            perType.put("book", 99.0);
        } catch (UnsupportedOperationException e) {
            thrown = true;
        }

        assertTrue(thrown);
        assertEquals(3.0, summary.getPerType().get("book"));
    }
}
