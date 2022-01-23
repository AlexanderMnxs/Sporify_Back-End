package com.backend.sporify_backend;

import com.backend.sporify_backend.models.User;
import com.backend.sporify_backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SporifyBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(SporifyBackEndApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder encoder) {
        return (args) -> {

            // create new user's account
            User user = new User("test",
                    "test@test.com",
                    encoder.encode("12345678"));

            // if user doesn't already exist
            // create user
            if(!userRepository.existsByUsername(user.getUsername())){
                userRepository.insert(user);

                return;
            }

            System.out.println("Test user already exists!");
        };
    }
}
