package api.user.management.service;

import api.user.management.model.collection.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;


    public String generateJwtToken(User user) {
        log.debug("jwtExpirationMs: {} jwtSecret : {}", jwtExpirationMs, jwtSecret);
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);
        log.debug("Generating JWT token for email {} with expiry set to {}", user.getEmail(), expirationDate);
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getId().toString())
                    .issueTime(now)
                    .expirationTime(expirationDate)
                    .claim("username", user.getUserName())
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512).build();
            SignedJWT signedJWT = new SignedJWT(header, claims);

            JWSSigner signer = new MACSigner(jwtSecret);
            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (Exception e) {
            log.error("Error while generating JWT token for userId {} with expiry set to {} error message : {}", user.getId().toString(), expirationDate, e.getMessage());
            return null;
        }


    }

    public void validateToken(String authorizationHeader) {
        log.debug("Validating JWT token");
        log.debug("authorizationHeader : {}", authorizationHeader);
        //Remove Authorization prefix
        String jwtToken = authorizationHeader.substring(7);
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if (claims.getExpirationTime().before(new Date())) {
                log.debug("JWT token expired");
                throw new RuntimeException("JWT token expired");
            }
        } catch (Exception e) {
            log.error("Error while validating JWT token error message : {}", e.getMessage());
            throw new RuntimeException("Error while validating JWT token");
        }
    }
}
