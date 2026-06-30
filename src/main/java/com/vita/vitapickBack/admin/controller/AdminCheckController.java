package com.vita.vitapickBack.admin.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminCheckController {

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAdmin() {
        return ResponseEntity.ok(Map.of("admin", true));
    }
}
