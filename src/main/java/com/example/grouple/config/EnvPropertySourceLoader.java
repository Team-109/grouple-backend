package com.example.grouple.config;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[] { "env" };
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        Dotenv dotenv = Dotenv.load();

        Map<String, Object> props = new HashMap<>();
        dotenv.entries().forEach(e -> props.put(e.getKey(), e.getValue()));  // ← 람다로 처리

        return List.of(new MapPropertySource(name, props));
    }
}