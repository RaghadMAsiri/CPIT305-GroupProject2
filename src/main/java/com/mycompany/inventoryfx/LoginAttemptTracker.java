/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Amani
 */
public final class LoginAttemptTracker {
     private LoginAttemptTracker(){}

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(2);

    private static final class Entry {
        int attempts = 0;
        Instant lockedUntil = null;
    }

    private static final Map<String, Entry> MAP = new ConcurrentHashMap<>();

    public static boolean isLocked(String username) {
        Entry e = MAP.get(normalize(username));
        if (e == null || e.lockedUntil == null) return false;
        if (Instant.now().isAfter(e.lockedUntil)) {
            e.lockedUntil = null;
            e.attempts = 0;
            return false;
        }
        return true;
    }

    public static long secondsLeft(String username) {
        Entry e = MAP.get(normalize(username));
        if (e == null || e.lockedUntil == null) return 0;
        return Math.max(0, Duration.between(Instant.now(), e.lockedUntil).getSeconds());
    }

    public static void recordSuccess(String username) {
        MAP.remove(normalize(username));
    }

    public static void recordFailure(String username) {
        String u = normalize(username);
        Entry e = MAP.computeIfAbsent(u, k -> new Entry());
        e.attempts++;
        if (e.attempts >= MAX_ATTEMPTS) {
            e.lockedUntil = Instant.now().plus(LOCK_DURATION);
        }
    }

    private static String normalize(String s){
        return s == null ? "" : s.trim().toLowerCase();
    }
}
