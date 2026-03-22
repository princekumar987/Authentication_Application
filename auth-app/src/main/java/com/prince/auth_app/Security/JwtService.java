package com.prince.auth_app.Security;

import com.prince.auth_app.entities.Role;
import com.prince.auth_app.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refrshTtlSeconds;
    private final String issuer;

    public JwtService(@Value("${Security.jwt.secret}") String key,
                      @Value("${Security.jwt.access-ttl-seconds}") long accessTtlSeconds,
                      @Value("${Security.jwt.refresh-ttl-seconds}") long refrshTtlSeconds,
                      @Value("${Security.jwt.issuer}") String issuer){

        if(key==null || key.length()<64){
             throw new IllegalArgumentException("Invalid Secret");
        }
        this.key= Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds=accessTtlSeconds;
        this.refrshTtlSeconds=refrshTtlSeconds;
        this.issuer=issuer;
    }


    public String generateAccessToken(User user){

        Instant now=Instant.now();
        List<String> roles= user.getRole()==null?List.of():
                             user.getRole().stream().map(Role::getName).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"

                )).signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user, String jti){

        Instant now=Instant.now();
        List<String> roles= user.getRole()==null?List.of():
                user.getRole().stream().map(Role::getName).toList();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refrshTtlSeconds)))
                .claim("typ" , "refresh")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Jws<Claims> parse(String token){
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        }
        catch(JwtException e){
            throw e;
        }
    }

    public boolean isAccessToken(String token){
          Claims c=parse(token).getBody();
          return "access".equals(c.get("typ"));
    }

    public boolean isRefreshToken(String token){
        Claims c=parse(token).getPayload();
        return "refresh".equals(c.get("typ"));
    }

    public UUID getUserId(String token){
        Claims c=parse(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    public String getId(String token){
        Claims c=parse(token).getPayload();
        return c.getId();
    }

    public List<String> getRoles(String token){
        Claims c=parse(token).getPayload();
        return (List<String>) c.get("roles");
    }

    public String getEmail(String token){
        Claims c=parse(token).getPayload();
        return (String) c.get("email");
    }

}
