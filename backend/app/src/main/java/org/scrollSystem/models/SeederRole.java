package org.scrollSystem.models;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.scrollSystem.config.ApplicationConfig;
import org.scrollSystem.config.SecurityConfiguration;
import org.scrollSystem.repository.SettingRepo;
import org.scrollSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.github.cdimascio.dotenv.Dotenv;


import java.util.Random;
import java.util.Base64;
import java.security.MessageDigest;


@Configuration
@RequiredArgsConstructor
public class SeederRole implements ApplicationRunner
{
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ApplicationConfig applicationConfig;
    private final SettingRepo settingRepo;



    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        var setting = Setting.builder().timer(10).build();
        settingRepo.save(setting);
        Dotenv dotenv = Dotenv.configure().directory("../").load();
        String salt = Base64.getEncoder().encodeToString(applicationConfig.getNextSalt());
        var user = User.builder()
                .firstName(dotenv.get("ADMIN_FIRST_NAME"))
                .lastName(dotenv.get("ADMIN_LAST_NAME"))
                .email(dotenv.get("ADMIN_EMAIL"))
                .password(passwordEncoder.encode(dotenv.get("ADMIN_PASSWORD") + salt))
                .username(dotenv.get("ADMIN_NAME"))
                .role("ROLE_ADMIN")
                .phone(dotenv.get("ADMIN_PHONE"))
                .salt(salt)
                .build();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return;
        }
        userRepository.save(user);
    }
}