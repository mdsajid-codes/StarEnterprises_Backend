package com.example.demo.service;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AdminAuthService {
    @Autowired
    private AdminRepository adminRepository;

     private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String authenticateAdmin(String username, String password){
        Optional<Admin> userData = adminRepository.findByUsername(username);
        if(userData.isPresent() && password.equals(userData.get().getPassword())){
            return generateToken(username);
        }
        return null;
    }

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey)
                .compact();

    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }
}
