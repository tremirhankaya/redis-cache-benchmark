package com.emirhan.redis.cache.benchmark.filter;

import com.emirhan.redis.cache.benchmark.service.CacheToggleService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * Her /api/products* isteğine iki header ekler:
 *   X-Response-Time-Ms  — sunucu tarafı süre
 *   X-Cache-Status      — HIT / MISS / DISABLED
 *
 * ContentCachingResponseWrapper kullanıyor çünkü Spring MVC, chain.doFilter()
 * sırasında response'u commit ediyor. Wrapper olmadan setHeader() çalışmaz.
 * copyBodyToResponse() ile body en sona yazılıyor, header'lar önce gidiyor.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TimingFilter implements Filter {

    private final CacheToggleService cacheToggleService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        if (!uri.startsWith("/api/products")) {
            chain.doFilter(request, response);
            return;
        }


        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(res);

        CacheHitTracker.clear();

        long t0 = System.nanoTime();
        chain.doFilter(request, wrappedRes);
        long elapsed = System.nanoTime() - t0;

        double ms = elapsed / 1_000_000.0;
        wrappedRes.setHeader("X-Response-Time-Ms", String.format("%.2f", ms));

        CacheHitTracker.Status status = CacheHitTracker.getStatus();
        if (status == null && cacheToggleService.isEnabled()) {
            status = CacheHitTracker.Status.HIT;
        }
        if (status != null) {
            wrappedRes.setHeader("X-Cache-Status", status.name());
        }

        CacheHitTracker.clear();


        wrappedRes.copyBodyToResponse();
    }
}
