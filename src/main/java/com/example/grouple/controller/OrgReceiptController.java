package com.example.grouple.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "08. 조직 가계부")
@RestController
@RequestMapping("/organizations/{orgId}/receipts")
public class OrgReceiptController {
}
