package org.example.claimsservice.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.example.claimsservice.dto.ClaimDTO;
import org.example.claimsservice.dto.ClaimStatusCountDTO;
import org.example.claimsservice.dto.UserDTO;
import org.example.claimsservice.feign.IdentityService;
import org.example.claimsservice.model.entity.Claim;
import org.example.claimsservice.model.enums.ClaimStatus;
import org.example.claimsservice.repository.ClaimRepository;
import org.example.claimsservice.service.implementation.AnalyticServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AnalyticServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private AnalyticServiceImpl analyticService;

    @Test
    void getClaimCountByStatus_returnsMappedList() {
        Object[] row1 = {ClaimStatus.APPROVED, 10L};
        Object[] row2 = {ClaimStatus.REJECTED, 5L};
        when(claimRepository.countClaimsByStatus()).thenReturn(List.of(row1, row2));

        List<ClaimStatusCountDTO> result = analyticService.getClaimCountByStatus();

        assertEquals(2, result.size());
        assertEquals(ClaimStatus.APPROVED, result.get(0).status());
        assertEquals(10L, result.get(0).count());
        assertEquals(ClaimStatus.REJECTED, result.get(1).status());
        assertEquals(5L, result.get(1).count());
    }

    @Test
    void getClaimCountByStatus_returnsEmptyList_whenNoData() {
        when(claimRepository.countClaimsByStatus()).thenReturn(Collections.emptyList());

        List<ClaimStatusCountDTO> result = analyticService.getClaimCountByStatus();

        assertTrue(result.isEmpty());
    }

    @Test
    void getClaimsByHospital_returnsPageOfClaimDTOs() {
        Long hospitalId = 100L;
        int page = 0;
        int size = 5;
        
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setUserId("user123");
        
        Page<Claim> mockPage = new PageImpl<>(List.of(claim));
        PageRequest pageRequest = PageRequest.of(page, size);

        // CORRECTED: Matches (id, username, name, email, bankAccount)
        UserDTO mockUser = new UserDTO("user123", "testUser", "Test Name", "test@mail.com", null);

        when(claimRepository.findByHospitalIdOrderByClaimRequestDateAsc(hospitalId, pageRequest))
                .thenReturn(mockPage);
        when(identityService.getUser("user123")).thenReturn(mockUser);

        Page<ClaimDTO> result = analyticService.getClaimsByHospital(hospitalId, page, size);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).id());
        assertEquals("testUser", result.getContent().get(0).username());
        
        verify(claimRepository).findByHospitalIdOrderByClaimRequestDateAsc(hospitalId, pageRequest);
        verify(identityService).getUser("user123");
    }

    @Test
    void getTopHighValueClaimsLastMonth_returnsClaimDTOsAndCalculatesDateCorrectly() {
        Claim claim = new Claim();
        claim.setId(2L);
        claim.setUserId("user456");
        
        PageRequest pageRequest = PageRequest.of(0, 10);
        
        // CORRECTED: Matches (id, username, name, email, bankAccount)
        UserDTO mockUser = new UserDTO("user456", "highValueUser", "High Name", "high@mail.com", null);
        
        when(claimRepository.findTopHighValueClaimsLastMonth(any(LocalDateTime.class), eq(pageRequest)))
                .thenReturn(List.of(claim));
        when(identityService.getUser("user456")).thenReturn(mockUser);

        List<ClaimDTO> result = analyticService.getTopHighValueClaimsLastMonth();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).id());
        assertEquals("highValueUser", result.get(0).username());
        
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(claimRepository).findTopHighValueClaimsLastMonth(dateCaptor.capture(), eq(pageRequest));
        
        LocalDateTime capturedDate = dateCaptor.getValue();
        LocalDateTime expectedDate = LocalDateTime.now().minusMonths(1);
        
        long diff = ChronoUnit.SECONDS.between(capturedDate, expectedDate);
        assertTrue(Math.abs(diff) < 5); 
    }
}