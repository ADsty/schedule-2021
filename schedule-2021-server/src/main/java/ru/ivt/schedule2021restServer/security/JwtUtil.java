package ru.ivt.schedule2021restServer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.error.ForbiddenApiException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Value("${server.jwt.secret.key}")
    private String SECRET_KEY;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public void validateToken(String token, StudentDetails studentDetails) {
        try {
            final Claims claims = extractAllClaims(token);
            final String subject = claims.getSubject();
            final Long id = claims.get("id", Long.class);
            final String groupName = claims.get("groupName", String.class);
            final String password = claims.get("password", String.class);
            final String role = claims.get("role", String.class);

            if (!(studentDetails.getStudent().getName().equals(subject)
                && studentDetails.getStudent().getId().equals(id)
                && studentDetails.getStudent().getGroup().getName().equals(groupName)
                && studentDetails.getPassword().equals(password)
                && studentDetails.getStudent().getRole().name().equals(role)
                && !isTokenExpired(token))) {
                throw new ForbiddenApiException("Некорректные поля токена");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ForbiddenApiException("Некорректный токен");
        }
    }

    public String generateToken(StudentDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userDetails.getStudent().getId());
        claims.put("password", userDetails.getPassword());
        claims.put("groupName", userDetails.getStudent().getGroup().getName());
        claims.put("role", userDetails.getStudent().getRole());

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        final long currentTimeMillis = System.currentTimeMillis();
        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(new Date(currentTimeMillis + 3_600_000_000L))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
}
