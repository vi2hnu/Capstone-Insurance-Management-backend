package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PlanService planService;

    public  AdminController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/plan/add")
    public ResponseEntity<Plan> addPlan(@RequestBody @Valid PlanDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.addPlan(request));
    }
}
