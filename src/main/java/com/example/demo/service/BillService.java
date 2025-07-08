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

import com.example.demo.dto.NewPdf;
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
            if(row == null || row.getCell(3) == null) continue;

            String username = row.getCell(3).getStringCellValue();
            String billMonth = row.getCell(26).getStringCellValue();
            Double amount = row.getCell(25).getNumericCellValue();
            String due = "";
            if (DateUtil.isCellDateFormatted(row.getCell(27))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Or your preferred format
                due = sdf.format(row.getCell(27).getDateCellValue());
            } else {
                due = row.getCell(22).toString(); // fallback to string representation
            }            String status = row.getCell(28).getStringCellValue();
            
            String pdfFileName = username + "_" + billMonth + ".pdf";
            String filePath = "bills/" + pdfFileName;
            
            User exitingData = userRepository.findByUsername(username)
                       .orElseThrow(() -> new RuntimeException("User not found with username: " + username));


            // Save data into DTA for creating pdf
            // CreateBillPdf createBillPdf = new CreateBillPdf();
            // createBillPdf.setUsername(username);
            // createBillPdf.setCustomerName(exitingData.getCustomerName());
            // createBillPdf.setFlatNo(exitingData.getFlatNo());
            // createBillPdf.setAddress(exitingData.getAddress());
            // createBillPdf.setBillMonth(billMonth);
            // createBillPdf.setTariffDetail(row.getCell(2).getStringCellValue());
            // createBillPdf.setAreaSqFt(row.getCell(3).getNumericCellValue());
            // createBillPdf.setTariffCategory(row.getCell(4).getStringCellValue());
            // createBillPdf.setLoadOnMains(row.getCell(5).getNumericCellValue());
            // createBillPdf.setLoadOnDG(row.getCell(6).getNumericCellValue());
            // createBillPdf.setMainUnitRate(row.getCell(7).getNumericCellValue());
            // createBillPdf.setDgUnitRate(row.getCell(8).getNumericCellValue());
            // createBillPdf.setStandingChargePerDay(row.getCell(9).getNumericCellValue());
            // createBillPdf.setBillingCycle(row.getCell(10).getStringCellValue());
            // createBillPdf.setAccountBalance(row.getCell(11).getNumericCellValue());
            // createBillPdf.setMainsOpeningUnit(row.getCell(12).getNumericCellValue());
            // createBillPdf.setMainsClosingUnit(row.getCell(13).getNumericCellValue());
            // createBillPdf.setMainsBillableUnit(row.getCell(14).getNumericCellValue());
            // createBillPdf.setMainsConsumption(row.getCell(15).getNumericCellValue());
            // createBillPdf.setTotalFixedChargePerMonth(row.getCell(16).getNumericCellValue());
            // createBillPdf.setDgOpeningUnit(row.getCell(17).getNumericCellValue());
            // createBillPdf.setDgBillableUnit(row.getCell(18).getNumericCellValue());
            // createBillPdf.setDgConsumption(row.getCell(19).getNumericCellValue());
            // createBillPdf.setRechargeForTheMonth(row.getCell(20).getNumericCellValue());
            // createBillPdf.setTotalDeductionForTheMonth(amount);

            NewPdf pdfBill = new NewPdf();
            pdfBill.setUsername(username);
            pdfBill.setCustomerName(exitingData.getCustomerName());
            pdfBill.setFlatNo(exitingData.getFlatNo());
            pdfBill.setAddress(exitingData.getAddress());
            pdfBill.setBillMonth(billMonth);
            pdfBill.setTower(row.getCell(2).getStringCellValue());
            pdfBill.setAreaSqFt(row.getCell(9).getNumericCellValue());
            pdfBill.setTariff(row.getCell(4).getStringCellValue());
            pdfBill.setMainsLoad(row.getCell(5).getNumericCellValue());
            pdfBill.setDgLoad(row.getCell(7).getNumericCellValue());
            pdfBill.setMainsFcKw(row.getCell(6).getNumericCellValue());
            pdfBill.setDgFcKw(row.getCell(8).getNumericCellValue());
            pdfBill.setPerDayStandingCharge(row.getCell(10).getNumericCellValue());
            pdfBill.setMonthlyFC(row.getCell(11).getNumericCellValue());
            pdfBill.setMainsUnitPrice(row.getCell(12).getNumericCellValue());
            pdfBill.setDgUnitPrice(row.getCell(13).getNumericCellValue());
            pdfBill.setAreaCharge(row.getCell(14).getNumericCellValue());
            pdfBill.setMeterBalance(row.getCell(15).getNumericCellValue());
            pdfBill.setTotalCashAddedInMeter(row.getCell(16).getNumericCellValue());
            pdfBill.setOpeningMainReading(row.getCell(17).getNumericCellValue());
            pdfBill.setClosingMainReading(row.getCell(18).getNumericCellValue());
            pdfBill.setMainsConsumption(row.getCell(19).getNumericCellValue());
            pdfBill.setMainsUnitAmount(row.getCell(20).getNumericCellValue());
            pdfBill.setOpeningDgReading(row.getCell(21).getNumericCellValue());
            pdfBill.setClosingDgReading(row.getCell(22).getNumericCellValue());
            pdfBill.setDgConsumption(row.getCell(23).getNumericCellValue());
            pdfBill.setDgUnitAmount(row.getCell(24).getNumericCellValue());
            pdfBill.setTotalBillGenerated(amount);

            createPDF(pdfBill, pdfFileName);

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

    private void createPDF(NewPdf pdfBill, String pdfFileName) throws IOException{
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

        Paragraph siteAddress = new Paragraph("SITE ADD:- MAHAGUN MANSION, INDIRAPURAM GAJIYABAD", normalFont);
        siteAddress.setAlignment(Element.ALIGN_CENTER);
        siteAddress.setSpacingAfter(10);
        doc.add(siteAddress);

        Paragraph reportTitle = new Paragraph("Electricity Bill", headerFont);
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
        customerInfoTable.addCell(createInfoCell(pdfBill.getCustomerName(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Meter Reading Month", normalFont, true));
        customerInfoTable.addCell(createInfoCell(pdfBill.getBillMonth(), normalFont, false));

        // Row 2
        customerInfoTable.addCell(createInfoCell("Flat No.", normalFont, true));
        customerInfoTable.addCell(createInfoCell(pdfBill.getFlatNo(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Tariff", normalFont, true));
        customerInfoTable.addCell(createInfoCell(pdfBill.getTariff(), normalFont, false));

        // Row 3
        customerInfoTable.addCell(createInfoCell("Tower ", normalFont, true));
        customerInfoTable.addCell(createInfoCell(pdfBill.getTower(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Sanctioned load on Mains", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getMainsLoad()), normalFont, false));

        // Row 4
       
        // Row 4 - Address spanning multiple cells
        PdfPCell addressLableCell = createInfoCell("Address", normalFont, true);
        customerInfoTable.addCell(addressLableCell);

        PdfPCell addressValueCell = createInfoCell(pdfBill.getAddress(), normalFont, false);
        customerInfoTable.addCell(addressValueCell);

        customerInfoTable.addCell(createInfoCell("Mains FC/KW ", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getMainsFcKw()), normalFont, false));

        // Row

        customerInfoTable.addCell(createInfoCell("", normalFont, true));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Sanctioned load on DG", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getDgLoad()), normalFont, false));


        // Row 6
        customerInfoTable.addCell(createInfoCell("Meter No", normalFont,true));
        customerInfoTable.addCell(createInfoCell(pdfBill.getUsername(), normalFont, false));
        customerInfoTable.addCell(createInfoCell("DG FC/KW ", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getDgFcKw()), normalFont, false));

        // Row 7
        customerInfoTable.addCell(createInfoCell("Tariff Detail", normalFont, true));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Mains Unit Price", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getMainsUnitPrice()), normalFont, false));

        // Row 8
        customerInfoTable.addCell(createInfoCell("Area Sq. ft", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getAreaSqFt()), normalFont, false));
        customerInfoTable.addCell(createInfoCell("DG Unit Pricee", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getDgUnitPrice()), normalFont, false));

        // Row 9
        customerInfoTable.addCell(createInfoCell("Area Charge", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getAreaCharge()), normalFont, false));
        customerInfoTable.addCell(createInfoCell("Per Day Standing Charge", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("%.0f", pdfBill.getPerDayStandingCharge()), normalFont, false));

        //Row 10
        customerInfoTable.addCell(createInfoCell("", normalFont, true));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Monthly FC", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("Rs. %.0f", pdfBill.getMonthlyFC()), normalFont, false));

        // Row 11

        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Meter Balance", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("Rs. %.0f", pdfBill.getMeterBalance()), normalFont, false));

        // Row 12

        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("", normalFont, false));
        customerInfoTable.addCell(createInfoCell("Total Cash Added", normalFont, true));
        customerInfoTable.addCell(createInfoCell(String.format("Rs. %.0f", pdfBill.getTotalCashAddedInMeter()), normalFont, false));

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
        deductionTable.addCell(createInfoCell("Mains Opening REading", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getOpeningMainReading()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Opening Reading", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getOpeningDgReading()), normalFont, false));

        deductionTable.addCell(createInfoCell("Mains Closing Reading", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getClosingMainReading()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Closing Reading", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getClosingDgReading()), normalFont, false));

        deductionTable.addCell(createInfoCell("Mains Unit Amount", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getMainsUnitAmount()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Unit Amount", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("%.1f", pdfBill.getDgUnitAmount()), normalFont, false));

        deductionTable.addCell(createInfoCell("Mains Consumption", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("₹ %.2f", pdfBill.getMainsConsumption()), normalFont, false));
        deductionTable.addCell(createInfoCell("DG Consumption", normalFont, true));
        deductionTable.addCell(createInfoCell(String.format("Rs. %.2f", pdfBill.getDgConsumption()), normalFont, false));

        doc.add(deductionTable);

        //  // Fixed charges and recharge section
        // PdfPTable chargesTable = new PdfPTable(4);
        // chargesTable.setWidthPercentage(100);
        // chargesTable.setWidths(new float[]{2.5f, 2f, 2.5f, 2f});
        // chargesTable.setSpacingAfter(15);

        // chargesTable.addCell(createInfoCell("Total Fixed Charge per Month", normalFont, true));
        // chargesTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getTotalFixedChargePerMonth()), normalFont, false));
        // chargesTable.addCell(createInfoCell("Recharged for the month", normalFont, true));
        // chargesTable.addCell(createInfoCell(String.format("%.0f", createBillPdf.getRechargeForTheMonth()), normalFont, false));

        // doc.add(chargesTable);


        // Total Deduction
        Paragraph totalDeduction = new Paragraph(String.format("TOTAL DEDUCTION FOR THE MONTH Rs.  :   %.2f", pdfBill.getTotalBillGenerated()), headerFont);
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
