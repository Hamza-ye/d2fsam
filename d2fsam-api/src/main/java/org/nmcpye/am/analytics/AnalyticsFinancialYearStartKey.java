package org.nmcpye.am.analytics;

import org.nmcpye.am.period.FinancialAprilPeriodType;
import org.nmcpye.am.period.FinancialJulyPeriodType;
import org.nmcpye.am.period.FinancialOctoberPeriodType;
import org.nmcpye.am.period.FinancialPeriodType;

public enum AnalyticsFinancialYearStartKey {
    FINANCIAL_YEAR_APRIL("FINANCIAL_YEAR_APRIL", new FinancialAprilPeriodType()),
    FINANCIAL_YEAR_JULY("FINANCIAL_YEAR_JULY", new FinancialJulyPeriodType()),
    FINANCIAL_YEAR_OCTOBER("FINANCIAL_YEAR_OCTOBER", new FinancialOctoberPeriodType());

    private final String name;

    private final FinancialPeriodType financialPeriodType;

    AnalyticsFinancialYearStartKey(String name, FinancialPeriodType financialPeriodType) {
        this.name = name;
        this.financialPeriodType = financialPeriodType;
    }

    public String getName() {
        return name;
    }

    public FinancialPeriodType getFinancialPeriodType() {
        return financialPeriodType;
    }
}
