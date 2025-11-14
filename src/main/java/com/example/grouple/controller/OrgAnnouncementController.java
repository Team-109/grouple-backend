package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "05. Announcement", description = "조직 공지사항 관련 API")
@RestController
@RequestMapping("/organizations/{organizationCode}/announcement")
public class OrgAnnouncementController {
}
