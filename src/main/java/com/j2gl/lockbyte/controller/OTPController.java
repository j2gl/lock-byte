package com.j2gl.lockbyte.controller;

import com.j2gl.lockbyte.model.OTPAccount;
import com.j2gl.lockbyte.repository.OTPAccountRepository;
import com.j2gl.lockbyte.util.TOTPUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/otp")
public class OTPController {
    private final OTPAccountRepository otpAccountRepository;
    private final TOTPUtil totpUtil;

    public OTPController(OTPAccountRepository userRepository, TOTPUtil totpUtil) {
        this.otpAccountRepository = userRepository;
        this.totpUtil = totpUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username,
                                               @RequestParam(required = false) String secretKey) {
        try {
            if (secretKey == null || secretKey.isEmpty()) {
                secretKey = totpUtil.generateSecretKey(); // Generate if not provided
            }

            OTPAccount user = new OTPAccount();
            user.setUsername(username);
            user.setSecretKey(secretKey);
            otpAccountRepository.save(user);

            return ResponseEntity.ok("User registered with secret key: " + secretKey);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error registering user");
        }
    }


    @GetMapping("/generate/{username}")
    public ResponseEntity<String> generateOTP(@PathVariable String username) {
        Optional<OTPAccount> user = otpAccountRepository.findByUsername(username);
        if (user.isPresent()) {
            try {
                String otp = totpUtil.generateTOTP(user.get().getSecretKey());
                return ResponseEntity.ok(otp);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error generating OTP");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

}
