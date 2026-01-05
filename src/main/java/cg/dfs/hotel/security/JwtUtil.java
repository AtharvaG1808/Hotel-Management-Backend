package cg.dfs.hotel.security;

import cg.dfs.hotel.entities.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "TravelAndHotelBooking123TravelAndHotelBooking123TravelAndHotelBooking123TravelAndHotelBooking123TravelAndHotelBooking123";
    //private final String SECRET_KEY = "mandpranjalmalegavchimandpranjalmalegavchimandpranjalmalegavchimandpranjalmalegavchimandpranjalmalegavchi";

    public String generateToken(String username, Long userId, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiry
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserIdFromToken(String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Long.parseLong(getClaimFromToken(jwt, "userId"));
    }

    private String getClaimFromToken(String token, String claim) {
        Claims claims = validateToken(token);
        Object claimValue = claims.get(claim);
        if (claimValue == null) {
            throw new IllegalArgumentException("Claim '" + claim + "' not found in token");
        }
        return claimValue.toString();
    }
}
