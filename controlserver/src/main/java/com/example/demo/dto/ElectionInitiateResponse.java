package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElectionInitiateResponse {
    int candidateStatus;
    int id;
}
