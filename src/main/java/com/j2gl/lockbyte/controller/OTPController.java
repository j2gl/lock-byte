package com.j2gl.lockbyte.controller;

import com.j2gl.lockbyte.model.User;
import com.j2gl.lockbyte.repository.UserRepository;
import com.j2gl.lockbyte.util.TOTPUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/otp")
public class OTPController {
    private final UserRepository userRepository;

    public OTPController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username,
                                               @RequestParam(required = false) String secretKey) {
        try {
            if (secretKey == null || secretKey.isEmpty()) {
                secretKey = TOTPUtil.generateSecretKey(); // Generate if not provided
            }

            User user = new User();
            user.setUsername(username);
            user.setSecretKey(secretKey);
            userRepository.save(user);

            return ResponseEntity.ok("User registered with secret key: " + secretKey);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error registering user");
        }
    }


    @GetMapping("/generate/{username}")
    public ResponseEntity<String> generateOTP(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            try {
                String otp = TOTPUtil.generateTOTP(user.get().getSecretKey());
                return ResponseEntity.ok(otp);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error generating OTP");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

}
