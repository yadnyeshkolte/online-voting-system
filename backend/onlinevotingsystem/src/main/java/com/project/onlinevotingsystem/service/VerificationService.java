package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.entity.DummyAadharRecord;
import com.project.onlinevotingsystem.entity.DummyVoterIdRecord;
import com.project.onlinevotingsystem.repository.DummyAadharRecordRepository;
import com.project.onlinevotingsystem.repository.DummyVoterIdRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class VerificationService {

    private final DummyAadharRecordRepository aadharRepository;
    private final DummyVoterIdRecordRepository voterIdRepository;

    public VerificationService(DummyAadharRecordRepository aadharRepository, DummyVoterIdRecordRepository voterIdRepository) {
        this.aadharRepository = aadharRepository;
        this.voterIdRepository = voterIdRepository;
    }

    public boolean verifyUserIdentity(String aadharNumber, String voterIdNumber, String fullName, LocalDate dob) {
        // Verify Aadhar
        Optional<DummyAadharRecord> aadharRecord = aadharRepository.findByAadharNumberAndFullNameAndDateOfBirth(
                aadharNumber, fullName, dob
        );
        boolean isAadharValid = aadharRecord.isPresent() && Boolean.TRUE.equals(aadharRecord.get().getIsValid());

        // Verify Voter ID
        Optional<DummyVoterIdRecord> voterRecord = voterIdRepository.findByVoterIdNumberAndFullNameAndDateOfBirth(
                voterIdNumber, fullName, dob
        );
        boolean isVoterIdValid = voterRecord.isPresent() && Boolean.TRUE.equals(voterRecord.get().getIsValid());

        return isAadharValid && isVoterIdValid;
    }
}
