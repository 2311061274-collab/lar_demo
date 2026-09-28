package com.petcare.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TrailingSlashRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        
        // Strip trailing slash or space if it's not the root path "/"
        if (requestUri != null && requestUri.length() > 1 && (requestUri.endsWith("/") || requestUri.endsWith(" ") || requestUri.endsWith("%20"))) {
            String newUri = requestUri.replaceAll("[/\\s%20]+$", "");
            String queryString = request.getQueryString();
            if (queryString != null) {
                newUri += "?" + queryString;
            }
            
            response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
            response.setHeader(HttpHeaders.LOCATION, newUri);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
