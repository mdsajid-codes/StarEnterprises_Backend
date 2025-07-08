package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPdf {
    private String username;
    private String customerName;
    private String flatNo;
    private String address;
    private String billMonth;
    private String tariffDetail;
    private Double areaSqFt;
    private String tariff;
    private Double mainsLoad;
    private Double mainsFcKw;
    private Double dgLoad;
    private Double dgFcKw;
    private Double perDayStandingCharge;
    private Double monthlyFC;
    private Double mainsUnitPrice;
    private Double dgUnitPrice;
    private Double areaCharge;
    private Double meterBalance;
    private Double totalCashAddedInMeter;
    private Double openingMainReading;
    private Double closingMainReading;
    private Double mainsConsumption;
    private Double mainsUnitAmount;
    private Double openingDgReading;
    private Double closingDgReading;
    private Double dgConsumption;
    private Double dgUnitAmount;
    private Double totalBillGenerated;

}
