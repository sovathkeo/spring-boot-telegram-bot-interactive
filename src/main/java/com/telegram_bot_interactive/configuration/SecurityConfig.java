package com.telegram_bot_interactive.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SecurityConfig {

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**", "/charts/**")
                    .permitAll() // Allow homepage and login
                //.anyRequest().authenticated()
            )
            *//*.oauth2Login(oauth2 -> oauth2
                .successHandler(new MicrosoftOAuthSuccessHandler()) // Handle successful login
            )*//*
        ;

        return http.build();
    }*/

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().anyRequest();
    }

}

//class MicrosoftOAuthSuccessHandler implements AuthenticationSuccessHandler {
//    @Override
//    public void onAuthenticationSuccess(
//        HttpServletRequest request, HttpServletResponse response, Authentication authentication)
//        throws IOException, ServletException {
//
//        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
//        System.out.println("User Logged in: " + oidcUser.getEmail());
//
//        response.sendRedirect("/dashboard");
//    }
//}
