package com.j2gl.lockbyte.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

@Component
public class TOTPUtil {
    
    private static final Duration TIME_STEP = Duration.ofSeconds(30); // 30-second OTP
    private static final int DIGITS = 6; // 6-digit OTP

    private KeyGenerator keyGenerator;
    private Encoder base64Encoder;
    private Decoder base64Decoder;

    public TOTPUtil() throws Exception {
        keyGenerator = KeyGenerator.getInstance("HmacSHA1");
        keyGenerator.init(160);
        base64Encoder = Base64.getEncoder();
        base64Decoder = Base64.getDecoder();
    }

    public String generateSecretKey() throws Exception {
        SecretKey key = keyGenerator.generateKey();
        return base64Encoder.encodeToString(key.getEncoded());
    }

    public String generateTOTP(String base32Secret) throws Exception {
        byte[] decodedKey = base64Decoder.decode(base32Secret);
        Key key = new javax.crypto.spec.SecretKeySpec(decodedKey, "HmacSHA1");

        TimeBasedOneTimePasswordGenerator totpGenerator =
                new TimeBasedOneTimePasswordGenerator(TIME_STEP, DIGITS);

        Instant now = Instant.now();
        return String.valueOf(totpGenerator.generateOneTimePassword(key, now));
    }
}
