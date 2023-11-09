package api.user.management.service;

import api.user.management.model.collection.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    log.debug("Generating JWT token for email {} with expiry set to {}", user.getEmail(),
        expirationDate);
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
      log.error(
          "Error while generating JWT token for userId {} with expiry set to {} error message : {}",
          user.getId().toString(), expirationDate, e.getMessage());
      return null;
    }


  }

  public boolean isValidToken(String userid, String authorizationHeader) {
    log.debug("authorizationHeader : {}", authorizationHeader);
    //Remove Authorization prefix
    String jwtToken = authorizationHeader.substring(7);
    try {
      SignedJWT signedJWT = SignedJWT.parse(jwtToken);
      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
      //Only allow the user to access their own data
      if (claims.getExpirationTime().before(new Date())) {
        log.debug("JWT token expired for user {}", userid);
        return false;
      }
      if (claims.getSubject().equals(userid)) {
        log.debug("JWT token valid for user {}", userid);
        return true;
      }
    } catch (Exception e) {
      log.error("Error while validating JWT token error message : {}", e.getMessage());
      return false;
    }
    log.debug("JWT token is invalid for user {}", userid);
    return false;
  }
}
