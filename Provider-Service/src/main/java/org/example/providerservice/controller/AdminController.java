package org.example.providerservice.controller;

import jakarta.validation.Valid;
import org.example.providerservice.dto.AddHospitalDTO;
import org.example.providerservice.dto.HospitalAuthorityDTO;
import org.example.providerservice.model.entity.Hospital;
import org.example.providerservice.model.entity.HospitalAuthority;
import org.example.providerservice.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final HospitalService hospitalService;

    public AdminController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping("/add/hospital")
    public ResponseEntity<Hospital> addHospital(@RequestBody @Valid AddHospitalDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(hospitalService.addHospital(request));
    }
    

    @PostMapping("/map/user")
    public ResponseEntity<HospitalAuthority> postMethodName(@RequestBody @Valid HospitalAuthorityDTO hospitalAuthorityDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(hospitalService.mapUserToHospital(hospitalAuthorityDTO));
    }
    
}
