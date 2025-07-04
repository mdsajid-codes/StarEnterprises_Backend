package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin (origins = "*")
public class AdminController {
    @Autowired
    private AdminRepository adminRepository;

    @PostMapping
    public ResponseEntity<Admin> addAdmin(@RequestBody Admin admin){
        Admin adminData = new Admin();
        adminData.setUsername(admin.getUsername());
        adminData.setPassword(admin.getPassword());
        adminRepository.save(adminData);
        return ResponseEntity.ok(adminData);
    }
    @PutMapping("/{username}")
    public ResponseEntity<String> updateAdmin(@RequestBody Admin admin, @PathVariable String username){
        Optional<Admin> adminData = adminRepository.findByUsername(username);

        if(adminData.isPresent()){
            Admin existingAdmin = adminData.get();
            existingAdmin.setUsername(admin.getUsername());
            existingAdmin.setPassword(admin.getPassword());
            adminRepository.save(existingAdmin);
            return ResponseEntity.ok("Admin Username and Password Updated Successfully!");
        }else{
            return ResponseEntity.badRequest().body("Failed to update");
        }
    }
}
