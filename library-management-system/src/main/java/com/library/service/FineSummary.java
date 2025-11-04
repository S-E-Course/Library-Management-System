package com.library.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates a user's unpaid fines grouped by media type (e.g., book, cd, journal),
 * and also provides a grand total across all types.
 *
 * <p>Used by US5.3 (Mixed Media Handling) to display a single summary that combines
 * fines for Books, CDs, and Journals.</p>
 *
 * @author
 * @version 1.0
 */
public class FineSummary {

    /** Per-type totals, value is the sum of fines. */
    private final Map<String, Double> perType = new HashMap<>();

    /** Grand total across all media types. */
    private double total = 0.0;

    /**
     * Adds a fine amount to the summary under the specified media type.
     *
     * @param mediaType media type string (e.g., "book", "cd", "journal"); if null, uses "unknown"
     * @param amount    fine amount to add; values ≤ 0 are ignored
     */
    public void add(String mediaType, double amount) {
        if (amount <= 0) return;
        String key = mediaType == null ? "unknown" : mediaType.trim().toLowerCase();
        perType.merge(key, amount, Double::sum);
        total += amount;
    }

    /**
     * Returns an unmodifiable map of per-type totals.
     *
     * @return map of media type to total amount
     */
    public Map<String, Double> getPerType() {
        return Collections.unmodifiableMap(perType);
    }

    /**
     * Returns the grand total across all media types.
     *
     * @return total fines amount
     */
    public double getTotal() {
        return total;
    }
}
