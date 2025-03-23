package com.j2gl.lockbyte.controller;

import com.j2gl.lockbyte.model.AccountOtp;
import com.j2gl.lockbyte.repository.AccountOtpRepository;
import com.j2gl.lockbyte.util.TOTPUtil;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/otp")
public class AccountOtpController {
    private final AccountOtpRepository accountOtpRepository;
    private final TOTPUtil totpUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username,
                                               @RequestParam(required = false) String secretKey) {
        try {
            if (secretKey == null || secretKey.isEmpty()) {
                secretKey = totpUtil.generateSecretKey(); // Generate if not provided
            }

            AccountOtp accountOtp = new AccountOtp();
            accountOtp.setAccountName(username);
            accountOtp.setSecretKey(secretKey);
            accountOtpRepository.save(accountOtp);

            return ResponseEntity.ok("User registered with secret key: " + secretKey);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error registering user");
        }
    }


    @GetMapping("/generate/{username}")
    public ResponseEntity<String> generateOTP(@PathVariable String username) {
        Optional<AccountOtp> user = accountOtpRepository.findByAccountName(username);
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

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {
        return ResponseEntity.ok(accountOtpRepository.findAll());
    }

}
