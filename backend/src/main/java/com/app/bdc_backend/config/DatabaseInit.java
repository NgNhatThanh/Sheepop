package com.app.bdc_backend.config;

import com.app.bdc_backend.dao.RoleRepository;
import com.app.bdc_backend.dao.UserRepository;
import com.app.bdc_backend.model.enums.RoleName;
import com.app.bdc_backend.model.user.Role;
import com.app.bdc_backend.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInit implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<Role> roles = roleRepository.findAll();
        if(roles.isEmpty()){
            Role userRole = new Role();
            userRole.setDescription("Role for users and shops");
            userRole.setName(RoleName.USER);

            Role adminRole = new Role();
            adminRole.setDescription("Role for big admins");
            adminRole.setName(RoleName.ADMIN);

            roles.add(userRole);
            roles.add(adminRole);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(adminRole);
            roleRepository.saveAll(roles);
            userRepository.save(admin);
        }
    }

}
