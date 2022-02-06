package az.et.zuulgatewayserver.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    @Value("${jwt.token.public-key}")
    private String publicKey;

    private final JwtUserDetailsService jwtUserDetailsService;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public Authentication getAuthentication(String token) {
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        System.out.println(expiration.toString());
        return expiration.before(new Date());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private PublicKey getPublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
            return kf.generatePublic(keySpecX509);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
    }
}