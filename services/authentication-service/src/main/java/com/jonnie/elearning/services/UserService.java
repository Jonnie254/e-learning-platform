package com.jonnie.elearning.services;

import com.jonnie.elearning.email.EmailService;
import com.jonnie.elearning.email.EmailTemplateEngine;
import com.jonnie.elearning.exceptions.UserNotFoundException;
import com.jonnie.elearning.jwt.JwtService;
import com.jonnie.elearning.repositories.TokenRepository;
import com.jonnie.elearning.repositories.UserRepository;
import com.jonnie.elearning.user.Token;
import com.jonnie.elearning.user.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activationUrl}")
    private String activationUrl;

    public void registerUser(@Valid UserRegistrationRequest userRegistrationRequest) throws MessagingException {
        var user = userMapper.toUser(userRegistrationRequest);
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateEngine.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken, "Activate your account"
                
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createAt(LocalDateTime.now())
                .expireAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if(LocalDateTime.now().isAfter(savedToken.getExpireAt())){
          sendValidationEmail(savedToken.getUser());
          throw new RuntimeException("Token expired. A new token has been sent to your email.");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(true);
        userRepository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public AuthenticationResponse authenticateUser(UserAuthenticationRequest userAuthenticationRequest) {
        var user = userRepository.findByEmail(userAuthenticationRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!user.isActive()) {
            throw new RuntimeException("User is not active");
        }
        if(!user.getPassword().equals(userAuthenticationRequest.password())) {
            throw new RuntimeException("Invalid credentials");
        }
        var claims = new HashMap<String, Object>();
        claims.put("fullname", user.getFullName());
        claims.put("userId", user.getId());
        claims.put("roles", user.getRole());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
