package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "06. 조직 공지사항")
@RestController
@RequestMapping("/organizations/{orgId}/anncs")
public class OrgAnnouncementController {
}
