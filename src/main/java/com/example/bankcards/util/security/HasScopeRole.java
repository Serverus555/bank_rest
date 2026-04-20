package com.example.bankcards.util.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;


public class HasScopeRole implements AuthorizationManager<RequestAuthorizationContext> {

    private final String targetRole;

    public HasScopeRole(String role) {
        this.targetRole = "SCOPE_ROLE_" + role;
    }

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication, RequestAuthorizationContext object) {
        Authentication auth = authentication.get();

        return () ->  auth != null && auth.isAuthenticated() &&
                          auth.getAuthorities().stream()
                              .anyMatch(a -> targetRole.equals(a.getAuthority()));
    }
}
