package com.telegram_bot_interactive.services.auth;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

    public String generateSecret() {
        return authenticator.createCredentials().getKey();
    }

    public String generateQR(String userEmail, String secret) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("MyApp", userEmail, new GoogleAuthenticatorKey.Builder(secret).build());
    }

    public boolean verifyOTP(String secret, int otp) {
        return authenticator.authorize(secret, otp);
    }
}
