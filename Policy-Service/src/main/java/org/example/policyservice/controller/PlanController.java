package org.example.policyservice.controller;

import jakarta.validation.Valid;
import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService){
        this.planService = planService;
    }

    @GetMapping("/plan/all")
    public ResponseEntity<List<Plan>> getAllPlan(){
        return ResponseEntity.status(HttpStatus.OK).body(planService.getAllPlans());
    }


}
