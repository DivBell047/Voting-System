package com.example.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateUpdateDTO {

    private String name;
    private String party;
    private String position;
    private String description;
    private String imageUrl;
}