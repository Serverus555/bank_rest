package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtGenerator {

    private final JwtEncoder jwtEncoder;
    private final String algorithm;

    public JwtGenerator(JwtEncoder jwtEncoder,
                        @Value("${spring.security.oauth2.resourceserver.jwt.jws-algorithms}") String algorithm) {
        this.jwtEncoder = jwtEncoder;
        this.algorithm = algorithm;
    }

    public Jwt generate(Authentication auth) {
        String scope = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(
                ((User) auth.getPrincipal()).getId().toString()
            )
            .claim("scp", scope)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.from(algorithm)).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims));
    }
}
