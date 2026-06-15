package com.emirhan.redis.cache.benchmark.filter;

/**
 * ThreadLocal tabanlı HIT/MISS tracker.
 * Service layer cache sonucunu buraya yazar, TimingFilter response header'a aktarır.
 *
 * Akış:
 *   TimingFilter.doFilter() → clear()
 *   ProductServiceImpl → @Cacheable çalışır, method çağrılırsa MISS olarak işaretlenir
 *   TimingFilter → getStatus() okur, X-Cache-Status header'a yazar
 */
public final class CacheHitTracker {

    public enum Status { HIT, MISS, DISABLED }

    private static final ThreadLocal<Status> CURRENT = new ThreadLocal<>();

    private CacheHitTracker() {}

    public static void clear() {
        CURRENT.remove();
    }

    public static void markDisabled() {
        CURRENT.set(Status.DISABLED);
    }

    public static void markMiss() {
        CURRENT.set(Status.MISS);
    }


    public static void markHit() {
        CURRENT.set(Status.HIT);
    }

    public static Status getStatus() {
        return CURRENT.get();
    }
}
