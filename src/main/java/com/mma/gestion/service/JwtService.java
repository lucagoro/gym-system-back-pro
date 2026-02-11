package com.mma.gestion.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mma.gestion.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    // Esta es la firma secreta del token. En producción debe ser una variable de entorno.
    private static final String SECRET_KEY = "tu_clave_secreta_super_larga_y_segura_para_el_sistema_pro";

    public String generateToken(User user) {
    Map<String, Object> extraClaims = new HashMap<>();
    
    // Ahora sí tenemos acceso al método getGym()
    if (user.getGym() != null) {
        extraClaims.put("gymId", user.getGym().getId());
    }
    
    return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
}

    private Key getSignInKey() {
    // Usamos Keys.hmacShaKeyFor con los bytes de la cadena directamente
    // Esto evita el error de "Illegal base64 character"
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
}

private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
}

// Agregamos este método para sacar el gymId cuando lo necesitemos
public Long extractGymId(String token) {
    return extractClaim(token, claims -> claims.get("gymId", Long.class));
}
    
}
