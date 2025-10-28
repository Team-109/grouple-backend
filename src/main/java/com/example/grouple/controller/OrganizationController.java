package com.example.grouple.controller;

import com.example.grouple.service.AuthService;
import com.example.grouple.service.OrganizationService;
import com.example.grouple.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02. Organization", description = "조직 관련 API")
@RestController
@RequestMapping("/organization") // api 명세에 따라 변경
public class OrganizationController {

    private final OrganizationService orgService;
    private final AuthService authService;
    private final UserService userService;

    public OrganizationController(OrganizationService orgService, AuthService authService, UserService userService) {
        this.orgService = orgService;
        this.authService = authService;
        this.userService = userService;
    }
}

