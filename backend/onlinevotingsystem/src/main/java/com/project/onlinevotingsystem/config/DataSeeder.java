package com.project.onlinevotingsystem.config;

import com.project.onlinevotingsystem.entity.Admin;
import com.project.onlinevotingsystem.entity.DummyAadharRecord;
import com.project.onlinevotingsystem.entity.DummyVoterIdRecord;
import com.project.onlinevotingsystem.repository.AdminRepository;
import com.project.onlinevotingsystem.repository.DummyAadharRecordRepository;
import com.project.onlinevotingsystem.repository.DummyVoterIdRecordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    private final AdminRepository adminRepository;
    private final DummyAadharRecordRepository aadharRepository;
    private final DummyVoterIdRecordRepository voterIdRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdminRepository adminRepository, DummyAadharRecordRepository aadharRepository, DummyVoterIdRecordRepository voterIdRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.aadharRepository = aadharRepository;
        this.voterIdRepository = voterIdRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Seed Admin
            if (adminRepository.count() == 0) {
                Admin admin = new Admin();
                admin.setEmail("admin@voting.com");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setFullName("Super Admin");
                adminRepository.save(admin);
                System.out.println("Admin seeded: admin@voting.com / admin123");
            }

            // Seed Dummy Aadhar
            if (aadharRepository.count() == 0) {
                DummyAadharRecord record = new DummyAadharRecord();
                record.setAadharNumber("123456789012");
                record.setFullName("John Doe");
                record.setDateOfBirth(LocalDate.of(1990, 1, 1));
                record.setIsValid(true);
                aadharRepository.save(record);
                System.out.println("Aadhar seeded: 123456789012 / John Doe / 1990-01-01");
            }

            // Seed Dummy Voter ID
             if (voterIdRepository.count() == 0) {
                DummyVoterIdRecord record = new DummyVoterIdRecord();
                record.setVoterIdNumber("ABC1234567");
                record.setFullName("Jane Doe");
                record.setDateOfBirth(LocalDate.of(1992, 2, 2));
                record.setIsValid(true);
                voterIdRepository.save(record);
                System.out.println("Voter ID seeded: ABC1234567 / Jane Doe / 1992-02-02");
            }
        };
    }
}
