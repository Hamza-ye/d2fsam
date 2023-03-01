import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../relative-periods.test-samples';

import { RelativePeriodsFormService } from './relative-periods-form.service';

describe('RelativePeriods Form Service', () => {
  let service: RelativePeriodsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelativePeriodsFormService);
  });

  describe('Service methods', () => {
    describe('createRelativePeriodsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelativePeriodsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            thisDay: expect.any(Object),
            yesterday: expect.any(Object),
            last3Days: expect.any(Object),
            last7Days: expect.any(Object),
            last14Days: expect.any(Object),
            thisMonth: expect.any(Object),
            lastMonth: expect.any(Object),
            thisBimonth: expect.any(Object),
            lastBimonth: expect.any(Object),
            thisQuarter: expect.any(Object),
            lastQuarter: expect.any(Object),
            thisSixMonth: expect.any(Object),
            lastSixMonth: expect.any(Object),
            weeksThisYear: expect.any(Object),
            monthsThisYear: expect.any(Object),
            biMonthsThisYear: expect.any(Object),
            quartersThisYear: expect.any(Object),
            thisYear: expect.any(Object),
            monthsLastYear: expect.any(Object),
            quartersLastYear: expect.any(Object),
            lastYear: expect.any(Object),
            last5Years: expect.any(Object),
            last12Months: expect.any(Object),
            last6Months: expect.any(Object),
            last3Months: expect.any(Object),
            last6BiMonths: expect.any(Object),
            last4Quarters: expect.any(Object),
            last2SixMonths: expect.any(Object),
            thisFinancialYear: expect.any(Object),
            lastFinancialYear: expect.any(Object),
            last5FinancialYears: expect.any(Object),
            thisWeek: expect.any(Object),
            lastWeek: expect.any(Object),
            thisBiWeek: expect.any(Object),
            lastBiWeek: expect.any(Object),
            last4Weeks: expect.any(Object),
            last4BiWeeks: expect.any(Object),
            last12Weeks: expect.any(Object),
            last52Weeks: expect.any(Object),
          })
        );
      });

      it('passing IRelativePeriods should create a new form with FormGroup', () => {
        const formGroup = service.createRelativePeriodsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            thisDay: expect.any(Object),
            yesterday: expect.any(Object),
            last3Days: expect.any(Object),
            last7Days: expect.any(Object),
            last14Days: expect.any(Object),
            thisMonth: expect.any(Object),
            lastMonth: expect.any(Object),
            thisBimonth: expect.any(Object),
            lastBimonth: expect.any(Object),
            thisQuarter: expect.any(Object),
            lastQuarter: expect.any(Object),
            thisSixMonth: expect.any(Object),
            lastSixMonth: expect.any(Object),
            weeksThisYear: expect.any(Object),
            monthsThisYear: expect.any(Object),
            biMonthsThisYear: expect.any(Object),
            quartersThisYear: expect.any(Object),
            thisYear: expect.any(Object),
            monthsLastYear: expect.any(Object),
            quartersLastYear: expect.any(Object),
            lastYear: expect.any(Object),
            last5Years: expect.any(Object),
            last12Months: expect.any(Object),
            last6Months: expect.any(Object),
            last3Months: expect.any(Object),
            last6BiMonths: expect.any(Object),
            last4Quarters: expect.any(Object),
            last2SixMonths: expect.any(Object),
            thisFinancialYear: expect.any(Object),
            lastFinancialYear: expect.any(Object),
            last5FinancialYears: expect.any(Object),
            thisWeek: expect.any(Object),
            lastWeek: expect.any(Object),
            thisBiWeek: expect.any(Object),
            lastBiWeek: expect.any(Object),
            last4Weeks: expect.any(Object),
            last4BiWeeks: expect.any(Object),
            last12Weeks: expect.any(Object),
            last52Weeks: expect.any(Object),
          })
        );
      });
    });

    describe('getRelativePeriods', () => {
      it('should return NewRelativePeriods for default RelativePeriods initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRelativePeriodsFormGroup(sampleWithNewData);

        const relativePeriods = service.getRelativePeriods(formGroup) as any;

        expect(relativePeriods).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelativePeriods for empty RelativePeriods initial value', () => {
        const formGroup = service.createRelativePeriodsFormGroup();

        const relativePeriods = service.getRelativePeriods(formGroup) as any;

        expect(relativePeriods).toMatchObject({});
      });

      it('should return IRelativePeriods', () => {
        const formGroup = service.createRelativePeriodsFormGroup(sampleWithRequiredData);

        const relativePeriods = service.getRelativePeriods(formGroup) as any;

        expect(relativePeriods).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelativePeriods should not enable id FormControl', () => {
        const formGroup = service.createRelativePeriodsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRelativePeriods should disable id FormControl', () => {
        const formGroup = service.createRelativePeriodsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
