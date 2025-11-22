package com.project.onlinevotingsystem.verification.service;

import com.project.onlinevotingsystem.user.model.IdProofType;
import com.project.onlinevotingsystem.verification.repository.DummyAadharRecordRepository;
import com.project.onlinevotingsystem.verification.repository.DummyPanRecordRepository;
import com.project.onlinevotingsystem.verification.repository.DummyPassportRecordRepository;
import com.project.onlinevotingsystem.verification.repository.DummyVoterIdRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    @Autowired
    private DummyAadharRecordRepository aadharRepository;
    @Autowired
    private DummyPanRecordRepository panRepository;
    @Autowired
    private DummyVoterIdRecordRepository voterIdRepository;
    @Autowired
    private DummyPassportRecordRepository passportRepository;

    public boolean verifyId(IdProofType type, String idNumber) {
        switch (type) {
            case AADHAR:
                return aadharRepository.findByAadharNumber(idNumber).map(r -> r.isValid()).orElse(false);
            case PAN:
                return panRepository.findByPanNumber(idNumber).map(r -> r.isValid()).orElse(false);
            case VOTER_ID:
                return voterIdRepository.findByVoterIdNumber(idNumber).map(r -> r.isValid()).orElse(false);
            case PASSPORT:
                return passportRepository.findByPassportNumber(idNumber).map(r -> r.isValid()).orElse(false);
            default:
                return false;
        }
    }
}
