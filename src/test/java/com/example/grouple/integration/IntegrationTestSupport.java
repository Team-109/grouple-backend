package com.example.grouple.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${spring.mvc.servlet.path:}")
    private String servletPath;

    protected String apiPath(String path) {
        String normalized = path.startsWith("/") ? path : "/" + path;
        if (!StringUtils.hasText(servletPath)) {
            return normalized;
        }
        String base = servletPath.startsWith("/") ? servletPath : "/" + servletPath;
        return base + normalized;
    }

    protected MockHttpServletRequestBuilder withApiServletPath(MockHttpServletRequestBuilder builder) {
        if (!StringUtils.hasText(servletPath)) {
            return builder;
        }
        String normalized = servletPath.startsWith("/") ? servletPath : "/" + servletPath;
        return builder.servletPath(normalized);
    }
}
