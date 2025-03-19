package com.j2gl.lockbyte.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

public class TOTPUtil {
    private static final Duration TIME_STEP = Duration.ofSeconds(30); // 30-second OTP
    private static final int DIGITS = 6; // 6-digit OTP

    public static String generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA1");
        keyGenerator.init(160);
        SecretKey key = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String generateTOTP(String base32Secret) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base32Secret);
        Key key = new javax.crypto.spec.SecretKeySpec(decodedKey, "HmacSHA1");

        TimeBasedOneTimePasswordGenerator totpGenerator =
                new TimeBasedOneTimePasswordGenerator(TIME_STEP, DIGITS);

        Instant now = Instant.now();
        return String.valueOf(totpGenerator.generateOneTimePassword(key, now));
    }
}
