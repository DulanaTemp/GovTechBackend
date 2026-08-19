package com.govtechparking.GovTechBackend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Convenience accessor for the authenticated user's id, which the
 * {@link JwtAuthenticationFilter} stores as the authentication principal.
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static UUID id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        return userId;
    }
}
