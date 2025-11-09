package com.anhtu.ftaskbackend.helper;

import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class QRTokenHelper {

    @Value("${jwt.SIGNER_KEY}")
    private String SIGNER_KEY;

    private static final String TOKEN_TYPE = "qr_booking";
    private static final String ISSUER = "ftask";
    private static final long EXPIRATION_SECONDS = 3600; // 1 hour

    /**
     * Generate QR token for booking
     * @param bookingId Booking ID to encode in token
     * @return JWT token string
     */
    public String generateQRToken(Long bookingId) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            Date now = new Date();
            Date expirationTime = Date.from(Instant.now().plusSeconds(EXPIRATION_SECONDS));

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(bookingId))
                    .issuer(ISSUER)
                    .issueTime(now)
                    .expirationTime(expirationTime)
                    .claim("bookingId", bookingId)
                    .claim("type", TOKEN_TYPE)
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return jwsObject.serialize();

        } catch (JOSEException e) {
            log.error("Failed to generate QR token for bookingId: {}", bookingId, e);
            throw new RuntimeException("Failed to generate QR token", e);
        }
    }

    /**
     * Verify QR token and extract bookingId
     * @param token JWT token string
     * @return Booking ID from token
     * @throws AppException if token is invalid or expired
     */
    public Long verifyQRToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify signature
            MACVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new AppException(ErrorCode.QRTokenInvalid);
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Check expiration
            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new AppException(ErrorCode.QRTokenExpired);
            }

            // Verify token type
            String tokenType = claimsSet.getStringClaim("type");
            if (tokenType == null || !TOKEN_TYPE.equals(tokenType)) {
                throw new AppException(ErrorCode.QRTokenInvalid);
            }

            // Verify issuer
            String issuer = claimsSet.getIssuer();
            if (issuer == null || !ISSUER.equals(issuer)) {
                throw new AppException(ErrorCode.QRTokenInvalid);
            }

            // Extract bookingId
            Object bookingIdObj = claimsSet.getClaim("bookingId");
            if (bookingIdObj == null) {
                throw new AppException(ErrorCode.QRTokenInvalid);
            }

            Long bookingId;
            if (bookingIdObj instanceof Long) {
                bookingId = (Long) bookingIdObj;
            } else if (bookingIdObj instanceof Integer) {
                bookingId = ((Integer) bookingIdObj).longValue();
            } else if (bookingIdObj instanceof Number) {
                bookingId = ((Number) bookingIdObj).longValue();
            } else {
                throw new AppException(ErrorCode.QRTokenInvalid);
            }

            return bookingId;

        } catch (AppException e) {
            throw e;
        } catch (JOSEException e) {
            log.error("Failed to verify QR token signature", e);
            throw new AppException(ErrorCode.QRTokenInvalid);
        } catch (Exception e) {
            log.error("Failed to verify QR token", e);
            throw new AppException(ErrorCode.QRTokenInvalid);
        }
    }
}

