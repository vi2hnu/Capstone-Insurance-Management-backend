package org.example.policyservice.dto;

import org.example.policyservice.model.enums.Status;

public record PolicyStatusCountDTO(
    Status status,
    long count
) {}
