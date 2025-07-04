package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query ("SELECT b FROM Bill b WHERE b.user.username = :username AND b.billMonth = :month")
    Optional <Bill> findByUsernameAndMonth(@Param ("username") String useranme, @Param ("month") String billMonth);
    List<Bill> findByUserUsername(String username);
}
