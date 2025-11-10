package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "07. 조직 문서")
@RestController
@RequestMapping("/organizations/{orgId}/docs")
public class OrgDocumentController {
}
