package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "06. Document", description = "조직 문서 관련 API")
@RestController
@RequestMapping("/organizations/{organizationCode}/document")
public class OrgDocumentController {
}
