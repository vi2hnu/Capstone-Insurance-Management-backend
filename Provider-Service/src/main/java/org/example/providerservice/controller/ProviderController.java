package org.example.providerservice.controller;

import org.apache.catalina.connector.Response;
import org.example.providerservice.dto.BankDetailsDTO;
import org.example.providerservice.dto.RegisterPlanDTO;
import org.example.providerservice.model.entity.HospitalBank;
import org.example.providerservice.model.entity.HospitalPlan;
import org.example.providerservice.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    private final HospitalService hospitalService;

    public ProviderController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping("/add/bank")
    public ResponseEntity<HospitalBank> addBank(@RequestBody @Valid BankDetailsDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(hospitalService.addHospitalBank(request));
    }
    
    @PostMapping("/register/plan")
    public ResponseEntity<HospitalPlan> registerPlan(@RequestBody @Valid RegisterPlanDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(hospitalService.registerPlan(request));
    }

    @GetMapping("/get/all/{planId}")
    public ResponseEntity<List<HospitalPlan>> getALlPlan(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(hospitalService.getAllHospitalsByPlan(planId));
    }

    @GetMapping("/check/plan/{planId}/{hospitalId}")
    public ResponseEntity<Boolean> checkPlan(@PathVariable Long planId, @PathVariable Long hospitalId) {
        if(hospitalService.checkHospitalPlan(planId, hospitalId)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @GetMapping("/check/association/{userId}/{hospitalId}")
    public ResponseEntity<Boolean> checkAssociation( @PathVariable Long hospitalId, @PathVariable String userId) {
        if(hospitalService.checkAssociation(userId, hospitalId)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

}
