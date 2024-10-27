package com.example.enhance_fitness_task.filter;

import com.example.enhance_fitness_task.config.RateLimitingConfig;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@Component
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingConfig rateLimitingConfig;

    private Map<String, Bucket> cache;

    private static final int TOO_MANY_REQUESTS = 429;

    public RateLimitingInterceptor(RateLimitingConfig rateLimitingConfig) {
        this.rateLimitingConfig = rateLimitingConfig;
    }

    @Autowired
    public RateLimitingInterceptor(RateLimitingConfig rateLimitingConfig, Map<String, Bucket> cache) {
        this.rateLimitingConfig = rateLimitingConfig;
        this.cache = cache;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to make this request");
            return false;
        }

        String key = principal.getName();
        Bucket bucket = rateLimitingConfig.resolveBucket(key);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            log.info("Rate limit exceeded try after 1 minute");
            response.setStatus(TOO_MANY_REQUESTS);
            response.getWriter().write("Rate limit exceeded");
            response.getWriter().flush();
            return false;
        }
    }
}
