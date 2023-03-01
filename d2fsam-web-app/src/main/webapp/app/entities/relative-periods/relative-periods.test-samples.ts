import { IRelativePeriods, NewRelativePeriods } from './relative-periods.model';

export const sampleWithRequiredData: IRelativePeriods = {
  id: 98465,
};

export const sampleWithPartialData: IRelativePeriods = {
  id: 82812,
  yesterday: false,
  last3Days: true,
  last7Days: false,
  thisMonth: false,
  lastMonth: true,
  thisQuarter: true,
  thisSixMonth: true,
  lastSixMonth: false,
  weeksThisYear: false,
  quartersThisYear: true,
  monthsLastYear: true,
  lastYear: true,
  last12Months: false,
  last6Months: true,
  last2SixMonths: false,
  thisFinancialYear: true,
  lastFinancialYear: false,
  last5FinancialYears: false,
  thisWeek: false,
  thisBiWeek: false,
  last4Weeks: false,
};

export const sampleWithFullData: IRelativePeriods = {
  id: 14069,
  thisDay: false,
  yesterday: false,
  last3Days: false,
  last7Days: false,
  last14Days: false,
  thisMonth: true,
  lastMonth: false,
  thisBimonth: true,
  lastBimonth: false,
  thisQuarter: true,
  lastQuarter: true,
  thisSixMonth: true,
  lastSixMonth: true,
  weeksThisYear: false,
  monthsThisYear: true,
  biMonthsThisYear: true,
  quartersThisYear: true,
  thisYear: false,
  monthsLastYear: true,
  quartersLastYear: false,
  lastYear: true,
  last5Years: true,
  last12Months: false,
  last6Months: true,
  last3Months: false,
  last6BiMonths: false,
  last4Quarters: false,
  last2SixMonths: false,
  thisFinancialYear: false,
  lastFinancialYear: false,
  last5FinancialYears: false,
  thisWeek: true,
  lastWeek: true,
  thisBiWeek: false,
  lastBiWeek: false,
  last4Weeks: false,
  last4BiWeeks: true,
  last12Weeks: false,
  last52Weeks: false,
};

export const sampleWithNewData: NewRelativePeriods = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
