package com.project.hotel.configuration;

import com.project.hotel.entity.Amenity;
import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.AmenityRepository;
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
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    AmenityRepository amenityRepository;


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
                        .email("letrungnguyen1004@gmail.com")
                        .roles(roles)
                        .status(1)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it!");
            }
        };
    }
    @Bean
    ApplicationRunner amenityInitializer() {
        return args -> {
            if (amenityRepository.count() == 0) {
                List<String> defaultAmenities = List.of(
                        "WiFi", "Air Conditioning", "Swimming Pool",
                        "Parking", "Breakfast included", "Gym", "TV", "Mini Bar"
                );
                defaultAmenities.forEach(name -> {
                    amenityRepository.save(Amenity.builder().name(name).build());
                });
                log.info("Default amenities initialized!");
            }
        };
    }
}
