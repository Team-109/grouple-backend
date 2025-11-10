package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "08. Schedule", description = "조직 일정 관련 API")
@RestController
@RequestMapping("/organizations/{organizationCode}/schedule")
public class OrgScheduleController {
}
