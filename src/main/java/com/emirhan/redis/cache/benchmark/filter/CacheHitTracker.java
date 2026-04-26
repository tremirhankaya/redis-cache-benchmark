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

    /** Her request başında çağır — temiz state ile başla. */
    public static void clear() {
        CURRENT.remove();
    }

    /** Cache DISABLED olduğunda (toggle OFF) çağır. */
    public static void markDisabled() {
        CURRENT.set(Status.DISABLED);
    }

    /** Cache açık ama method body çalıştı → MISS. */
    public static void markMiss() {
        CURRENT.set(Status.MISS);
    }

    /**
     * Cache açık ve method body çalışmadı → HIT.
     * TimingFilter: eğer service layer hiç mark etmediyse VE cache enabled ise → HIT.
     */
    public static void markHit() {
        CURRENT.set(Status.HIT);
    }

    /** Mevcut status — null ise henüz belirlenmedi. */
    public static Status getStatus() {
        return CURRENT.get();
    }
}
