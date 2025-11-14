package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "04. Members", description = "조직 회원 관련 API")
@RestController
@RequestMapping("/organizations/{organizationCode}/members")
public class OrgMemberController {
}
