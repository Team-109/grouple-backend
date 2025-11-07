package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "07. Receipt", description = "조직 가계부 관련 API")
@RestController
@RequestMapping("/organizations/{organizationCode}/receipt")
public class OrgReceiptController {
}
