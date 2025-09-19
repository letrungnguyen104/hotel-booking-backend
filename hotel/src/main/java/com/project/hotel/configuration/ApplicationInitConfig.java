package com.project.hotel.configuration;

import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.RoleRepository;
import com.project.hotel.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                Role role = roleRepository.findById("ADMIN").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(roles)
                        .status(1)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it!");
            }
        };
    }
}
