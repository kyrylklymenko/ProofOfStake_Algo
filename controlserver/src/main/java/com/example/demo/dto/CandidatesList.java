package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CandidatesList {
    int candidatesAmount;
}
