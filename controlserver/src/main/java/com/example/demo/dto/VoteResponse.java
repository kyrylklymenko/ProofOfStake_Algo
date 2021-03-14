package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponse {
    int id;
    int voteStake;
}
