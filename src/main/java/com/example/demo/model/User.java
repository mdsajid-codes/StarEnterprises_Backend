package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "users_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (unique = true, nullable = false, length = 50)
    private String username;
    
    @Column (nullable = false)
    private String password;

    private String email;
    private String customerName;
    private String flatNo;
    private String address;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bill> bills;
}
