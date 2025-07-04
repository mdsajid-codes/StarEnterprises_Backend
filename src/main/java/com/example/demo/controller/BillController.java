package com.example.demo.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Bill;
import com.example.demo.service.BillService;

@RestController
@RequestMapping("/api/bill")
@CrossOrigin (origins = "*")
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("/import")
    public ResponseEntity<String> importBill(@RequestParam ("file") MultipartFile file) throws Exception{
        billService.importBill(file);
        return ResponseEntity.ok("Bill Generated Successfully");
    }

    @GetMapping("/{username}/{billMonth}")
    public ResponseEntity<Bill> getBillByUsernameMonth(@PathVariable String username, @PathVariable String billMonth){
        Bill bill = billService.getBillByUsernameAndMonth(username, billMonth);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Bill>> getBillByUsername(@PathVariable String username){
        return ResponseEntity.ok(billService.getBillUsername(username));
    }

    @GetMapping("/view/{username}/{billMonth}")
    public ResponseEntity<UrlResource> viewPdf(@PathVariable String username, @PathVariable String billMonth) throws Exception{
        Bill bill = billService.getBillByUsernameAndMonth(username, billMonth);
        Path path = Paths.get(bill.getFilePath());

        UrlResource resource = new UrlResource(path.toUri());

        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
    }

    @GetMapping("/download/{username}/{billMonth}")
    public ResponseEntity<UrlResource> downloadPdf(@PathVariable String username, @PathVariable String billMonth) throws Exception{
        Bill bill = billService.getBillByUsernameAndMonth(username, billMonth);
        Path path = Paths.get(bill.getFilePath());
        UrlResource resource = new UrlResource(path.toUri());

        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
                    , "attachment; fileName=" + path.getFileName())
                    .body(resource);

    }
    @PutMapping("/updateBill/{username}/{billMonth}")
    public ResponseEntity<String> updateBill(@PathVariable String username, @PathVariable String billMonth){
        billService.updateBill(username, billMonth);
        return ResponseEntity.ok("Bill Updated Successfully");
    }
}
