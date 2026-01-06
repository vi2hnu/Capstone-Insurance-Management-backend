package org.example.policyservice.service;

import java.util.List;

import org.example.policyservice.dto.PlanDTO;
import org.example.policyservice.exception.PlanAlreadyExistsException;
import org.example.policyservice.exception.PlanNotFoundException;
import org.example.policyservice.model.entity.Plan;
import org.example.policyservice.model.enums.Status;
import org.example.policyservice.repository.PlanRepository;
import org.example.policyservice.service.implementation.PlanServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanServiceImplTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanServiceImpl planService;

    @Test
    void getAllPlans_shouldReturnListOfPlans() {
        Plan plan1 = new Plan("Gold", "Desc1", 100.0, 5000.0, 12, Status.ACTIVE);
        Plan plan2 = new Plan("Silver", "Desc2", 50.0, 2000.0, 6, Status.ACTIVE);
        when(planRepository.findAll()).thenReturn(List.of(plan1, plan2));

        List<Plan> result = planService.getAllPlans();

        assertEquals(2, result.size());
        assertEquals("Gold", result.get(0).getName());
    }

    @Test
    void addPlan_shouldSaveAndReturnPlan_WhenPlanDoesNotExist() {
        PlanDTO request = new PlanDTO("Platinum", "Best Plan", 200.0, 10000.0, 24);
        
        when(planRepository.existsPlanByName(request.name())).thenReturn(false);
        when(planRepository.save(any(Plan.class))).thenAnswer(i -> i.getArguments()[0]);

        Plan result = planService.addPlan(request);

        assertNotNull(result);
        assertEquals("Platinum", result.getName());
        assertEquals(Status.ACTIVE, result.getStatus());
        assertEquals(200.0, result.getPremiumAmount());

        ArgumentCaptor<Plan> captor = ArgumentCaptor.forClass(Plan.class);
        verify(planRepository).save(captor.capture());
        assertEquals("Platinum", captor.getValue().getName());
    }

    @Test
    void addPlan_shouldThrowException_WhenPlanAlreadyExists() {
        PlanDTO request = new PlanDTO("Platinum", "Best Plan", 200.0, 10000.0, 24);
        
        when(planRepository.existsPlanByName(request.name())).thenReturn(true);

        assertThrows(PlanAlreadyExistsException.class, () -> planService.addPlan(request));
        verify(planRepository, never()).save(any());
    }

    @Test
    void getPlan_shouldReturnPlan_WhenFound() {
        Long id = 1L;
        Plan plan = new Plan("Gold", "Desc", 100.0, 5000.0, 12, Status.ACTIVE);
        plan.setId(id);
        
        when(planRepository.findPlanById(id)).thenReturn(plan);

        Plan result = planService.getPlan(id);

        assertEquals(id, result.getId());
        assertEquals("Gold", result.getName());
    }

    @Test
    void getPlan_shouldThrowException_WhenNotFound() {
        Long id = 99L;
        when(planRepository.findPlanById(id)).thenReturn(null);

        assertThrows(PlanNotFoundException.class, () -> planService.getPlan(id));
    }
}