package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillPdf {
    private String username;
    private String customerName;
    private String flatNo;
    private String address;
    private String billMonth;
    private String tariffDetail;
    private Double areaSqFt;
    private String tariffCategory;
    private Double loadOnMains;
    private Double loadOnDG;
    private Double mainUnitRate;
    private Double dgUnitRate;
    private Double standingChargePerDay;
    private String billingCycle;
    private Double accountBalance;
    private Double mainsOpeningUnit;
    private Double mainsClosingUnit;
    private Double mainsBillableUnit;
    private Double mainsConsumption;
    private Double totalFixedChargePerMonth;
    private Double dgOpeningUnit;
    private Double dgBillableUnit;
    private Double dgConsumption;
    private Double rechargeForTheMonth;
    private Double totalDeductionForTheMonth;

}
