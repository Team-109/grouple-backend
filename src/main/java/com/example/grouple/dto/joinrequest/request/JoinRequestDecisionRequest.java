package com.example.grouple.dto.joinrequest.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDecisionRequest {

    @Size(max = 255)
    private String reason;
}
