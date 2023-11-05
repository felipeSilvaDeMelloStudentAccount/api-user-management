package api.user.management.service;

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


    public String generateJwtToken(String email) {
        log.debug("jwtExpirationMs: {} jwtSecret : {}", jwtExpirationMs, jwtSecret);
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);
        log.debug("Generating JWT token for email {} with expiry set to {}", email, expirationDate);
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(now)
                    .expirationTime(expirationDate)
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512).build();
            SignedJWT signedJWT = new SignedJWT(header, claims);

            JWSSigner signer = new MACSigner(jwtSecret);
            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (Exception e) {
            log.error("Error while generating JWT token for email {} with expiry set to {} error message : {}", email, expirationDate, e.getMessage());
            return null;
        }


    }
}
