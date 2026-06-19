package com.notpatch.nbooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil newJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "dGhpcy1pcy1hLWRldi1vbmx5LXNlY3JldC1kby1ub3QtdXNlLWluLXByb2Q=");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);
        return jwtUtil;
    }

    @Test
    void generatesTokenAndParsesClaimsBack() {
        JwtUtil jwtUtil = newJwtUtil();

        String token = jwtUtil.generateToken("owner@business.com", "BUSINESS", 42L);
        Claims claims = jwtUtil.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("owner@business.com");
        assertThat(claims.get("role", String.class)).isEqualTo("BUSINESS");
        assertThat(claims.get("id", Long.class)).isEqualTo(42L);
    }

    @Test
    void rejectsTamperedToken() {
        JwtUtil jwtUtil = newJwtUtil();
        String token = jwtUtil.generateToken("owner@business.com", "BUSINESS", 42L);
        int signatureStart = token.lastIndexOf('.') + 1;
        char flipped = token.charAt(signatureStart) == 'a' ? 'b' : 'a';
        String tampered = token.substring(0, signatureStart) + flipped + token.substring(signatureStart + 1);

        assertThatThrownBy(() -> jwtUtil.parseToken(tampered)).isInstanceOf(JwtException.class);
    }
}
