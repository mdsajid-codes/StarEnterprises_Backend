package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin (origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired 
    private UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam ("file") MultipartFile file){
        userService.importExcelData(file);
        return ResponseEntity.ok("Excell Data Imported successfully");
    }

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody User user){
        return ResponseEntity.ok(userService.addSingleUser(user));
    }

    @GetMapping("/allUser")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Optional<User>> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/updateSingleUser/{username}")
    public ResponseEntity<String> updateSingleUser(@RequestBody User user, @PathVariable String username){
        return ResponseEntity.ok(userService.updateUser(user, username));
    }

    @PutMapping("/updatePassword/{username}")
    public ResponseEntity<String> updatePassword(@RequestBody User user, @PathVariable String username){
        return ResponseEntity.ok(userService.updatePassword(user, username));
    }


    @PutMapping("/updateBulkUser")
    public ResponseEntity<String> updateBulkUser(@RequestParam ("file") MultipartFile file){
        try {
            userService.updateUsersByExcell(file);
            return ResponseEntity.ok("Users Updated Successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update Users");
        }
    }

}
