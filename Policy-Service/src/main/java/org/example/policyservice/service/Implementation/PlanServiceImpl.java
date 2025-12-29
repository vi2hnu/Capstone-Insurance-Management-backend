package org.example.policyservice.service.Implementation;

import lombok.extern.slf4j.Slf4j;
import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.exception.PlanAlreadyExistsException;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.enums.Status;
import org.example.policyservice.repository.PlanRepository;
import org.example.policyservice.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlanServiceImpl implements PlanService {

    private PlanRepository planRepository;

    public PlanServiceImpl(PlanRepository planRepository){
        this.planRepository = planRepository;
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @Override
    public Plan addPlan(PlanDTO request) {
        if(planRepository.existsPlanByName(request.name())){
            log.error("Plan already exists {}",request.name());
            throw new PlanAlreadyExistsException("Plan already exists");
        }
        Plan plan = new Plan(request.name(),request.description(),
                request.premiumAmount(),request.coverageAmount(),request.duration(), Status.ACTIVE);

        return planRepository.save(plan);
    }



}
