package com.example.demo.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void importExcelData(MultipartFile file){
        try(InputStream is = file.getInputStream()){
            Workbook workbook = WorkbookFactory.create(is);
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            List<User> users = new ArrayList<>();

            for(int i=1; i<=((org.apache.poi.ss.usermodel.Sheet) sheet).getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) continue;

                String username = row.getCell(0).getStringCellValue();
                String password = row.getCell(1).getStringCellValue();
                String email = row.getCell(2).getStringCellValue();
                String customerName = row.getCell(3).getStringCellValue();
                String flatNo = row.getCell(4).getStringCellValue();
                String address = row.getCell(5).getStringCellValue();

                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setCustomerName(customerName);
                user.setFlatNo(flatNo);
                user.setAddress(address);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                users.add(user);
            }
            userRepository.saveAll(users);
            workbook.close();
        }catch(Exception e){
            throw new RuntimeException("Fail to import excell data" + e.getMessage());
        }
    }

    public String addSingleUser(User user){
        User addUser = new User();
        addUser.setUsername(user.getUsername());
        addUser.setPassword(user.getPassword());
        addUser.setEmail(user.getEmail());
        addUser.setCustomerName(user.getCustomerName());
        addUser.setFlatNo(user.getFlatNo());
        addUser.setAddress(user.getAddress());
        addUser.setCreatedAt(LocalDateTime.now());
        addUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(addUser);
        return "User Added Successfully!";
    }

    public String updateUser(User user, String username){
        Optional <User> userData = userRepository.findByUsername(username);

        if(userData.isPresent()){
            User exitingUser = userData.get();
            exitingUser.setPassword(user.getPassword());
            exitingUser.setEmail(user.getEmail());
            exitingUser.setCustomerName(user.getCustomerName());
            exitingUser.setFlatNo(user.getFlatNo());
            exitingUser.setAddress(user.getAddress());
            exitingUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(exitingUser);
            return "User Update Successfully!";
        }else{
            return "Not Updated";
        }
    }

    public String updatePassword(User user, String username){
        Optional<User> userData = userRepository.findByUsername(username);

        if(userData.isPresent()){
            User exstingUser = userData.get();
            exstingUser.setPassword(user.getPassword());
            userRepository.save(exstingUser);
            return "Password Update Successfully!";
        }else{
            return "Failed to update Passowrd!";
        }
    }

    public void updateUsersByExcell(MultipartFile file) throws Exception{
        InputStream is = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(is);
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

        for(int i=1;i<=sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if(row == null) continue;

            String username = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();
            String email = row.getCell(2).getStringCellValue();
            String customerName = row.getCell(3).getStringCellValue();
            String flatNo = row.getCell(4).getStringCellValue();
            String address = row.getCell(5).getStringCellValue();

            Optional <User> updateUser = userRepository.findByUsername(username);

            if(updateUser.isPresent()){
                User exitingUser = updateUser.get();
                exitingUser.setPassword(password);
                exitingUser.setEmail(email);
                exitingUser.setCustomerName(customerName);
                exitingUser.setFlatNo(flatNo);
                exitingUser.setAddress(address);
                exitingUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(exitingUser);
            }
        }
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
