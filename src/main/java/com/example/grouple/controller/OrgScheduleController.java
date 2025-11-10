package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "09. 조직 일정")
@RestController
@RequestMapping("/organizations/{orgId}/schedules")
public class OrgScheduleController {
}
