package com.telegram_bot_interactive.controllers;


import com.telegram_bot_interactive.services.auth.GoogleAuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController {

    private final GoogleAuthService authService = new GoogleAuthService();

    @PostMapping("/generate-secret/{email}")
    public String generateSecret(@PathVariable String email) {
        var secret = authService.generateSecret();
        System.out.println("==> secret: " + secret);
        return authService.generateQR(email, secret);
    }

    @PostMapping("/verify-otp")
    public boolean verifyOTP(@RequestParam String secret, @RequestParam int otp) {
        return authService.verifyOTP(secret, otp);
    }
}
