package org.example.policyservice.controller;

import java.util.List;

import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService){
        this.planService = planService;
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Plan>> getAllPlan(){
        return ResponseEntity.status(HttpStatus.OK).body(planService.getAllPlans());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(planService.getPlan(id));
    }
    

}
