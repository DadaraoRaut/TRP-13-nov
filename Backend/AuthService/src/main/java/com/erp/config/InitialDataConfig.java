package com.erp.config;

import com.erp.entity.Role;
import com.erp.entity.User;
import com.erp.repository.RoleRepository;
import com.erp.service.CustomUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class InitialDataConfig {

    @Bean
    CommandLineRunner initAdminAndRoles(CustomUserDetailsService userService,
                                        RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            // 1️⃣ Create roles if they don't exist
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

            Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                    .orElseGet(() -> roleRepository.save(new Role(null, "EMPLOYEE")));

            Role billerRole = roleRepository.findByRoleName("BILLER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "BILLER")));

            Role supplierRole = roleRepository.findByRoleName("SUPPLIER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "SUPPLIER")));

            // 2️⃣ Create Admin user if not exist
            if (userService.findByUsernameIgnoreCase("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("gawandeabhijit57@gmail.com");
                admin.setEnabled(1);

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);

                userService.saveUser(admin);
                System.out.println("✅ Default admin created: username=admin, password=admin123");
            }

            // 3️⃣ Create Biller user if not exist
            if (userService.findByUsernameIgnoreCase("biller").isEmpty()) {
                User biller = new User();
                biller.setUsername("biller");
                biller.setPassword(passwordEncoder.encode("biller123"));
                biller.setEmail("biller@example.com");
                biller.setEnabled(1);

                Set<Role> roles = new HashSet<>();
                roles.add(billerRole);
                biller.setRoles(roles);

                userService.saveUser(biller);
                System.out.println("✅ Default biller created: username=biller, password=biller123");
            }
        };
    }
}
