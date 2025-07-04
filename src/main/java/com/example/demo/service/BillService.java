package com.example.demo.service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.CreateBillPdf;
import com.example.demo.model.Bill;
import com.example.demo.model.User;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class BillService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BillRepository billRepository;

    public void importBill(MultipartFile file) throws Exception {
        try(InputStream is = file.getInputStream()){
        Workbook workbook = WorkbookFactory.create(is);
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

        for(int i=1; i<=sheet.getLastRowNum(); i++){
            org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
            if(row == null) continue;

            String username = row.getCell(0).getStringCellValue();
            String billMonth = row.getCell(1).getStringCellValue();
            Double amount = row.getCell(21).getNumericCellValue();
            String due = "";
            if (DateUtil.isCellDateFormatted(row.getCell(22))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Or your preferred format
                due = sdf.format(row.getCell(22).getDateCellValue());
            } else {
                due = row.getCell(22).toString(); // fallback to string representation
            }            String status = row.getCell(23).getStringCellValue();
            
            String pdfFileName = username + "_" + billMonth + ".pdf";
            String filePath = "bills/" + pdfFileName;
            
            User exitingData = userRepository.findByUsername(username)
                       .orElseThrow(() -> new RuntimeException("User not found with username: " + username));


            // Save data into DTA for creating pdf
            CreateBillPdf createBillPdf = new CreateBillPdf();
            createBillPdf.setUsername(username);
            createBillPdf.setCustomerName(exitingData.getCustomerName());
            createBillPdf.setFlatNo(exitingData.getFlatNo());
            createBillPdf.setAddress(exitingData.getAddress());
            createBillPdf.setBillMonth(billMonth);
            createBillPdf.setTariffDetail(row.getCell(2).getStringCellValue());
            createBillPdf.setAreaSqFt(row.getCell(3).getNumericCellValue());
            createBillPdf.setTariffCategory(row.getCell(4).getStringCellValue());
            createBillPdf.setLoadOnMains(row.getCell(5).getNumericCellValue());
            createBillPdf.setLoadOnDG(row.getCell(6).getNumericCellValue());
            createBillPdf.setMainUnitRate(row.getCell(7).getNumericCellValue());
            createBillPdf.setDgUnitRate(row.getCell(8).getNumericCellValue());
            createBillPdf.setStandingChargePerDay(row.getCell(9).getNumericCellValue());
            createBillPdf.setBillingCycle(row.getCell(10).getStringCellValue());
            createBillPdf.setAccountBalance(row.getCell(11).getNumericCellValue());
            createBillPdf.setMainsOpeningUnit(row.getCell(12).getNumericCellValue());
            createBillPdf.setMainsClosingUnit(row.getCell(13).getNumericCellValue());
            createBillPdf.setMainsBillableUnit(row.getCell(14).getNumericCellValue());
            createBillPdf.setMainsConsumption(row.getCell(15).getNumericCellValue());
            createBillPdf.setTotalFixedChargePerMonth(row.getCell(16).getNumericCellValue());
            createBillPdf.setDgOpeningUnit(row.getCell(17).getNumericCellValue());
            createBillPdf.setDgBillableUnit(row.getCell(18).getNumericCellValue());
            createBillPdf.setDgConsumption(row.getCell(19).getNumericCellValue());
            createBillPdf.setRechargeForTheMonth(row.getCell(20).getNumericCellValue());
            createBillPdf.setTotalDeductionForTheMonth(amount);

            createPDF(createBillPdf, pdfFileName);

            User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("User not found!"));

            Bill bill = new Bill();

            bill.setUser(user);
            bill.setBillMonth(billMonth);
            bill.setAmount(amount);
            bill.setFilePath(filePath);
            bill.setDue(due);
            bill.setStatus(status);
            billRepository.save(bill);
            }
        }
    }

    private void createPDF(CreateBillPdf createBillPdf, String pdfFileName) throws IOException{
        Document doc = new Document(PageSize.A4, 30,30,30,30);
        Path path = Paths.get("bills");
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }

        PdfWriter.getInstance(doc, new FileOutputStream("bills/" + pdfFileName));
        doc.open();

        // Define font
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font subHeaderFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 9);
        Font smallFont = new Font(Font.HELVETICA, 8);

        // Header Section - Company Name and Address
        Paragraph companyName = new Paragraph("Star Enterprises", titleFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        doc.add(companyName);

        Paragraph siteAddress = new Paragraph("SITE ADD:- Star Enterprises, Kunli, Sonipat(Haryana) Kingsbury Apartment", normalFont);
        siteAddress.setAlignment(Element.ALIGN_CENTER);
        siteAddress.setSpacingAfter(10);
        doc.add(siteAddress);

        Paragraph reportTitle = new Paragraph("REPORT", headerFont);
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        reportTitle.setSpacingAfter(15);
        doc.add(reportTitle);

        // Customer Information Section- 5 Column layout
        PdfPTable customerInfoTable = new PdfPTable(4);
        customerInfoTable.setWidthPercentage(100);
        customerInfoTable.setWidths(new float[]{2f,2f,2f,2f});
        customerInfoTable.setSpacingAfter(10);

        // Row 1
        customerInfoTable.addCell(createInfoCell("Name Of Customer", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getCustomerName(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Meter Reading Month", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getBillMonth(), normalFont, false));

        // Row 2
        customerInfoTable.addCell(createInfoCell("Flat No.", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getFlatNo(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Tariff Category", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getTariffCategory(), normalFont, false));

        // Row 3
        customerInfoTable.addCell(createInfoCell("Meter No. ", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getUsername(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Sanctioned load on Mains", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getLoadOnMains()), normalFont, false));

        // Row 4
        customerInfoTable.addCell(createInfoCell("Tariff Detail", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getTariffDetail(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Sanctioned load on DG", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getLoadOnDG()), normalFont, false));

        // Row 4 - Address spanning multiple cells
        PdfPCell addressLableCell = createInfoCell("Address", normalFont, true);
        customerInfoTable.addCell(addressLableCell);

        PdfPCell addressValueCell = createInfoCell(createBillPdf.getAddress(), normalFont, false);
        customerInfoTable.addCell(addressValueCell);

        customerInfoTable.addCell(createInfoCell("Mains Unit Rate", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getMainUnitRate()), normalFont, false));

        // Row 6
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("DG Unit Rate", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getDgUnitRate()), normalFont, false));

        // Row 7
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Standing Charge Per Day", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getStandingChargePerDay()), normalFont, false));

        // Row 8
        customerInfoTable.addCell(createInfoCell("Area Sq.ft", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getAreaSqFt()), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Billing Cycle", normalFont, true));
        customerInfoTable.addCell(createInfoCell(createBillPdf.getBillingCycle(), normalFont, false));

        //Row 9
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Account Balance", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("Rs. %.0f", createBillPdf.getAccountBalance()), normalFont, false));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));

        doc.add(customerInfoTable);

         // Electricity Deduction Header
        Paragraph electricityTitle = new Paragraph("ELECTRICITY DEDUCTION", headerFont);
        electricityTitle.setAlignment(Element.ALIGN_CENTER);
        electricityTitle.setSpacingBefore(15);
        electricityTitle.setSpacingAfter(10);
        doc.add(electricityTitle);

        // Main Supply and DG Supply Table - 5 columns
        PdfPTable deductionTable = new PdfPTable(4);
        deductionTable.setWidthPercentage(100);
        deductionTable.setWidths(new float[]{2.5f, 2f, 2.5f, 2f});
        deductionTable.setSpacingAfter(15);

        // Headers
        PdfPCell mainSupplyHeader = createHeaderCell("ACCOUNT OF MAIN SUPPLY", subHeaderFont);
        mainSupplyHeader.setColspan(2);
        mainSupplyHeader.setBackgroundColor(new Color(220, 220, 220));
        deductionTable.addCell(mainSupplyHeader);

        PdfPCell dgSupplyHeader = createHeaderCell("ACCOUNT OF DG SUPPLY", subHeaderFont);
        dgSupplyHeader.setColspan(2);
        dgSupplyHeader.setBackgroundColor(new Color(220, 220, 220));
        deductionTable.addCell(dgSupplyHeader);

        // Mains and DG data rows
        deductionTable.addCell(createInfoCell("Mains Opening Unit", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getMainsOpeningUnit()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Opening Unit", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getDgOpeningUnit()), normalFont, false));

        deductionTable.addCell(createInfoCell("Mains Closing Unit", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getMainsClosingUnit()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Closing Unit", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getDgBillableUnit()), normalFont, false));

        deductionTable.addCell(createInfoCell("Mains Billable Unit", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getMainsBillableUnit()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Billable Unit", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", createBillPdf.getDgBillableUnit()), normalFont, false));

        deductionTable.addCell(createInfoCell("Mains Consumption", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("₹ %.2f", createBillPdf.getMainsConsumption()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Consumption", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("Rs. %.2f", createBillPdf.getDgConsumption()), normalFont, false));

        doc.add(deductionTable);

         // Fixed charges and recharge section
        PdfPTable chargesTable = new PdfPTable(4);
        chargesTable.setWidthPercentage(100);
        chargesTable.setWidths(new float[]{2.5f, 2f, 2.5f, 2f});
        chargesTable.setSpacingAfter(15);

        chargesTable.addCell(createInfoCell("Total Fixed Charge per Month", normalFont, true));
        chargesTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getTotalFixedChargePerMonth()), normalFont, false));
        chargesTable.addCell(createInfoCell("Recharged for the month", normalFont, true));
        chargesTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getRechargeForTheMonth()), normalFont, false));

        doc.add(chargesTable);


        // Total Deduction
        Paragraph totalDeduction = new Paragraph(String.format("TOTAL DEDUCTION FOR THE MONTH ₹ :   %.2f", createBillPdf.getTotalDeductionForTheMonth()), headerFont);
        totalDeduction.setAlignment(Element.ALIGN_CENTER);
        totalDeduction.setSpacingBefore(10);
        totalDeduction.setSpacingAfter(15);
        doc.add(totalDeduction);

         // Remarks
        Paragraph remarks = new Paragraph("Remarks: This is for your information that per day standing charges comprises of all the charges mentioned below & per day amount will deduct every morning at 7.00 a.m.", smallFont);
        remarks.setSpacingAfter(10);
        doc.add(remarks);

        doc.close();

    }

    private PdfPCell createInfoCell(String text, Font font, boolean isBold) {
        Font cellFont = isBold ? new Font(font.getFamily(), font.getSize(), Font.BOLD) : font;
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", cellFont));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(3);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new Color(240, 240, 240));
        return cell;
    }

    public Bill getBillByUsernameAndMonth(String username, String billMonth){
        return billRepository.findByUsernameAndMonth(username, billMonth).orElseThrow(()-> new RuntimeException("Bill Not found!"));
    }

    public java.util.List<Bill> getBillUsername(String username){
        return billRepository.findByUserUsername(username);
    }

    public String updateBill(String username, String billMonth){
        Optional<Bill> billData = billRepository.findByUsernameAndMonth(username, billMonth);

        if(billData.isPresent()){
            Bill existingBill = billData.get();
            existingBill.setStatus("Paid");
            billRepository.save(existingBill);
            return "Status update successfully";
        }else{
            return "Not updated!";
        }
    }
}
