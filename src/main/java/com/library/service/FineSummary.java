package com.library.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a user's unpaid fines grouped by media type.
 * Also provides the total amount across all types.
 */
public class FineSummary {

    private final Map<String, Double> perType = new HashMap<>();
    private double total = 0.0;

    /**
     * Adds a fine amount under the given media type.
     * Amounts less than or equal to zero are ignored.
     *
     * @param mediaType type name such as "book", "cd", or "journal"
     * @param amount    fine amount to add
     */
    public void add(String mediaType, double amount) {
        if (amount <= 0) return;
        String key = mediaType == null ? "unknown" : mediaType.trim().toLowerCase();
        perType.merge(key, amount, Double::sum);
        total += amount;
    }

    /**
     * Returns a read-only map of totals per media type.
     *
     * @return map of type → amount
     */
    public Map<String, Double> getPerType() {
        return Collections.unmodifiableMap(perType);
    }

    /**
     * Returns the total fines across all types.
     *
     * @return total amount
     */
    public double getTotal() {
        return total;
    }
}
