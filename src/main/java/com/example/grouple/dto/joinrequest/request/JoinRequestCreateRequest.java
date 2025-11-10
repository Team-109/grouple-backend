package com.example.grouple.dto.joinrequest.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestCreateRequest {

    @Size(max = 255)
    private String message;
}
