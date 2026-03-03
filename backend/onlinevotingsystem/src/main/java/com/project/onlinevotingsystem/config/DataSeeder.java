package com.project.onlinevotingsystem.config;

import com.project.onlinevotingsystem.entity.Admin;
import com.project.onlinevotingsystem.entity.DummyAadharRecord;
import com.project.onlinevotingsystem.entity.DummyVoterIdRecord;
import com.project.onlinevotingsystem.repository.AdminRepository;
import com.project.onlinevotingsystem.repository.DummyAadharRecordRepository;
import com.project.onlinevotingsystem.repository.DummyVoterIdRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final AdminRepository adminRepository;
    private final DummyAadharRecordRepository aadharRepository;
    private final DummyVoterIdRecordRepository voterIdRepository;
    private final PasswordEncoder passwordEncoder;

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

            // Ensure a consistent verification pair exists for registration demos.
            String demoFullName = "John Doe";
            LocalDate demoDob = LocalDate.of(1990, 1, 1);
            String demoAadhar = "123456789012";
            String demoVoterId = "ABC1234567";

            DummyAadharRecord aadharRecord = aadharRepository.findByAadharNumber(demoAadhar).orElseGet(DummyAadharRecord::new);
            aadharRecord.setAadharNumber(demoAadhar);
            aadharRecord.setFullName(demoFullName);
            aadharRecord.setDateOfBirth(demoDob);
            aadharRecord.setIsValid(true);
            aadharRepository.save(aadharRecord);
            System.out.println("Aadhar ensured: 123456789012 / John Doe / 1990-01-01");

            DummyVoterIdRecord voterRecord = voterIdRepository.findByVoterIdNumber(demoVoterId).orElseGet(DummyVoterIdRecord::new);
            voterRecord.setVoterIdNumber(demoVoterId);
            voterRecord.setFullName(demoFullName);
            voterRecord.setDateOfBirth(demoDob);
            voterRecord.setIsValid(true);
            voterIdRepository.save(voterRecord);
            System.out.println("Voter ID ensured: ABC1234567 / John Doe / 1990-01-01");
        };
    }
}
