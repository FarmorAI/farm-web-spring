package com.farmorai.backend.mapper;

import com.farmorai.backend.dto.PaymentPlan;

public class testCode {
    public static void main(String[] args) {
        String subsPlan = "Basic";
        String subsPrice = "1000";

        System.out.println(PaymentPlan.valueOf(subsPlan).ordinal());

        System.out.println(Integer.parseInt(subsPrice));
    }
}
