package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class UserDashboardController {
    @Autowired
    private AuthService authService;

     @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestHeader ("Authorization") String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            if(authService.validateToken(token)){
                String username = authService.getUsernameFromToken(token);
                Map<String, Object> data = new HashMap<>();
                data.put("message", "Welcome to dashboard");
                data.put("username", username);
                data.put("timesStamps", System.currentTimeMillis());

                return ResponseEntity.ok(data);
            }
        }
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    
    }
}
