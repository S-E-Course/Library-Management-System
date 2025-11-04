package com.library.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FineSummary {
    private final Map<String, Double> perType = new HashMap<>();
    private double total = 0.0;

    public void add(String mediaType, double amount) {
        if (amount <= 0) return;
        String key = mediaType == null ? "unknown" : mediaType.trim().toLowerCase();
        perType.merge(key, amount, Double::sum);
        total += amount;
    }

    // Per-type totals, e.g. {"book": 10.0, "cd": 20.0, "journal": 15.0} 
    public Map<String, Double> getPerType() {
        return Collections.unmodifiableMap(perType);
    }

    // Total for all media types. 
    public double getTotal() {
        return total;
    }
}
