package com.leka.teashop.service.jwt;

import com.leka.teashop.service.jwt.config.JwtConfiguration;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Jwt Service class implements main operations with jwt, like creating, extracting claims etc.
 *
 * @author osynelnyk
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfiguration.JwtProperties jwtProperties;

    public String generateAdminToken(Authentication auth) {
        return generateTokenWithExtraClaims(Map.of(
                "username", auth.getName(),
                "roles", "ROLE_ADMIN"
        ), jwtProperties.expiration());
    }

    private String generateTokenWithExtraClaims(Map<String, ?> extraClaims,
                                                Long expiration) {
        log.debug("Creating JWT Token with custom claims");
        final Date now = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(
                System.currentTimeMillis() + expiration * 1000);
        return new DefaultJwtBuilder()
                .setClaims(extraClaims)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
