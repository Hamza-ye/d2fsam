import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRelativePeriods, NewRelativePeriods } from '../relative-periods.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelativePeriods for edit and NewRelativePeriodsFormGroupInput for create.
 */
type RelativePeriodsFormGroupInput = IRelativePeriods | PartialWithRequiredKeyOf<NewRelativePeriods>;

type RelativePeriodsFormDefaults = Pick<
  NewRelativePeriods,
  | 'id'
  | 'thisDay'
  | 'yesterday'
  | 'last3Days'
  | 'last7Days'
  | 'last14Days'
  | 'thisMonth'
  | 'lastMonth'
  | 'thisBimonth'
  | 'lastBimonth'
  | 'thisQuarter'
  | 'lastQuarter'
  | 'thisSixMonth'
  | 'lastSixMonth'
  | 'weeksThisYear'
  | 'monthsThisYear'
  | 'biMonthsThisYear'
  | 'quartersThisYear'
  | 'thisYear'
  | 'monthsLastYear'
  | 'quartersLastYear'
  | 'lastYear'
  | 'last5Years'
  | 'last12Months'
  | 'last6Months'
  | 'last3Months'
  | 'last6BiMonths'
  | 'last4Quarters'
  | 'last2SixMonths'
  | 'thisFinancialYear'
  | 'lastFinancialYear'
  | 'last5FinancialYears'
  | 'thisWeek'
  | 'lastWeek'
  | 'thisBiWeek'
  | 'lastBiWeek'
  | 'last4Weeks'
  | 'last4BiWeeks'
  | 'last12Weeks'
  | 'last52Weeks'
>;

type RelativePeriodsFormGroupContent = {
  id: FormControl<IRelativePeriods['id'] | NewRelativePeriods['id']>;
  thisDay: FormControl<IRelativePeriods['thisDay']>;
  yesterday: FormControl<IRelativePeriods['yesterday']>;
  last3Days: FormControl<IRelativePeriods['last3Days']>;
  last7Days: FormControl<IRelativePeriods['last7Days']>;
  last14Days: FormControl<IRelativePeriods['last14Days']>;
  thisMonth: FormControl<IRelativePeriods['thisMonth']>;
  lastMonth: FormControl<IRelativePeriods['lastMonth']>;
  thisBimonth: FormControl<IRelativePeriods['thisBimonth']>;
  lastBimonth: FormControl<IRelativePeriods['lastBimonth']>;
  thisQuarter: FormControl<IRelativePeriods['thisQuarter']>;
  lastQuarter: FormControl<IRelativePeriods['lastQuarter']>;
  thisSixMonth: FormControl<IRelativePeriods['thisSixMonth']>;
  lastSixMonth: FormControl<IRelativePeriods['lastSixMonth']>;
  weeksThisYear: FormControl<IRelativePeriods['weeksThisYear']>;
  monthsThisYear: FormControl<IRelativePeriods['monthsThisYear']>;
  biMonthsThisYear: FormControl<IRelativePeriods['biMonthsThisYear']>;
  quartersThisYear: FormControl<IRelativePeriods['quartersThisYear']>;
  thisYear: FormControl<IRelativePeriods['thisYear']>;
  monthsLastYear: FormControl<IRelativePeriods['monthsLastYear']>;
  quartersLastYear: FormControl<IRelativePeriods['quartersLastYear']>;
  lastYear: FormControl<IRelativePeriods['lastYear']>;
  last5Years: FormControl<IRelativePeriods['last5Years']>;
  last12Months: FormControl<IRelativePeriods['last12Months']>;
  last6Months: FormControl<IRelativePeriods['last6Months']>;
  last3Months: FormControl<IRelativePeriods['last3Months']>;
  last6BiMonths: FormControl<IRelativePeriods['last6BiMonths']>;
  last4Quarters: FormControl<IRelativePeriods['last4Quarters']>;
  last2SixMonths: FormControl<IRelativePeriods['last2SixMonths']>;
  thisFinancialYear: FormControl<IRelativePeriods['thisFinancialYear']>;
  lastFinancialYear: FormControl<IRelativePeriods['lastFinancialYear']>;
  last5FinancialYears: FormControl<IRelativePeriods['last5FinancialYears']>;
  thisWeek: FormControl<IRelativePeriods['thisWeek']>;
  lastWeek: FormControl<IRelativePeriods['lastWeek']>;
  thisBiWeek: FormControl<IRelativePeriods['thisBiWeek']>;
  lastBiWeek: FormControl<IRelativePeriods['lastBiWeek']>;
  last4Weeks: FormControl<IRelativePeriods['last4Weeks']>;
  last4BiWeeks: FormControl<IRelativePeriods['last4BiWeeks']>;
  last12Weeks: FormControl<IRelativePeriods['last12Weeks']>;
  last52Weeks: FormControl<IRelativePeriods['last52Weeks']>;
};

export type RelativePeriodsFormGroup = FormGroup<RelativePeriodsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelativePeriodsFormService {
  createRelativePeriodsFormGroup(relativePeriods: RelativePeriodsFormGroupInput = { id: null }): RelativePeriodsFormGroup {
    const relativePeriodsRawValue = {
      ...this.getFormDefaults(),
      ...relativePeriods,
    };
    return new FormGroup<RelativePeriodsFormGroupContent>({
      id: new FormControl(
        { value: relativePeriodsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      thisDay: new FormControl(relativePeriodsRawValue.thisDay),
      yesterday: new FormControl(relativePeriodsRawValue.yesterday),
      last3Days: new FormControl(relativePeriodsRawValue.last3Days),
      last7Days: new FormControl(relativePeriodsRawValue.last7Days),
      last14Days: new FormControl(relativePeriodsRawValue.last14Days),
      thisMonth: new FormControl(relativePeriodsRawValue.thisMonth),
      lastMonth: new FormControl(relativePeriodsRawValue.lastMonth),
      thisBimonth: new FormControl(relativePeriodsRawValue.thisBimonth),
      lastBimonth: new FormControl(relativePeriodsRawValue.lastBimonth),
      thisQuarter: new FormControl(relativePeriodsRawValue.thisQuarter),
      lastQuarter: new FormControl(relativePeriodsRawValue.lastQuarter),
      thisSixMonth: new FormControl(relativePeriodsRawValue.thisSixMonth),
      lastSixMonth: new FormControl(relativePeriodsRawValue.lastSixMonth),
      weeksThisYear: new FormControl(relativePeriodsRawValue.weeksThisYear),
      monthsThisYear: new FormControl(relativePeriodsRawValue.monthsThisYear),
      biMonthsThisYear: new FormControl(relativePeriodsRawValue.biMonthsThisYear),
      quartersThisYear: new FormControl(relativePeriodsRawValue.quartersThisYear),
      thisYear: new FormControl(relativePeriodsRawValue.thisYear),
      monthsLastYear: new FormControl(relativePeriodsRawValue.monthsLastYear),
      quartersLastYear: new FormControl(relativePeriodsRawValue.quartersLastYear),
      lastYear: new FormControl(relativePeriodsRawValue.lastYear),
      last5Years: new FormControl(relativePeriodsRawValue.last5Years),
      last12Months: new FormControl(relativePeriodsRawValue.last12Months),
      last6Months: new FormControl(relativePeriodsRawValue.last6Months),
      last3Months: new FormControl(relativePeriodsRawValue.last3Months),
      last6BiMonths: new FormControl(relativePeriodsRawValue.last6BiMonths),
      last4Quarters: new FormControl(relativePeriodsRawValue.last4Quarters),
      last2SixMonths: new FormControl(relativePeriodsRawValue.last2SixMonths),
      thisFinancialYear: new FormControl(relativePeriodsRawValue.thisFinancialYear),
      lastFinancialYear: new FormControl(relativePeriodsRawValue.lastFinancialYear),
      last5FinancialYears: new FormControl(relativePeriodsRawValue.last5FinancialYears),
      thisWeek: new FormControl(relativePeriodsRawValue.thisWeek),
      lastWeek: new FormControl(relativePeriodsRawValue.lastWeek),
      thisBiWeek: new FormControl(relativePeriodsRawValue.thisBiWeek),
      lastBiWeek: new FormControl(relativePeriodsRawValue.lastBiWeek),
      last4Weeks: new FormControl(relativePeriodsRawValue.last4Weeks),
      last4BiWeeks: new FormControl(relativePeriodsRawValue.last4BiWeeks),
      last12Weeks: new FormControl(relativePeriodsRawValue.last12Weeks),
      last52Weeks: new FormControl(relativePeriodsRawValue.last52Weeks),
    });
  }

  getRelativePeriods(form: RelativePeriodsFormGroup): IRelativePeriods | NewRelativePeriods {
    return form.getRawValue() as IRelativePeriods | NewRelativePeriods;
  }

  resetForm(form: RelativePeriodsFormGroup, relativePeriods: RelativePeriodsFormGroupInput): void {
    const relativePeriodsRawValue = { ...this.getFormDefaults(), ...relativePeriods };
    form.reset(
      {
        ...relativePeriodsRawValue,
        id: { value: relativePeriodsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RelativePeriodsFormDefaults {
    return {
      id: null,
      thisDay: false,
      yesterday: false,
      last3Days: false,
      last7Days: false,
      last14Days: false,
      thisMonth: false,
      lastMonth: false,
      thisBimonth: false,
      lastBimonth: false,
      thisQuarter: false,
      lastQuarter: false,
      thisSixMonth: false,
      lastSixMonth: false,
      weeksThisYear: false,
      monthsThisYear: false,
      biMonthsThisYear: false,
      quartersThisYear: false,
      thisYear: false,
      monthsLastYear: false,
      quartersLastYear: false,
      lastYear: false,
      last5Years: false,
      last12Months: false,
      last6Months: false,
      last3Months: false,
      last6BiMonths: false,
      last4Quarters: false,
      last2SixMonths: false,
      thisFinancialYear: false,
      lastFinancialYear: false,
      last5FinancialYears: false,
      thisWeek: false,
      lastWeek: false,
      thisBiWeek: false,
      lastBiWeek: false,
      last4Weeks: false,
      last4BiWeeks: false,
      last12Weeks: false,
      last52Weeks: false,
    };
  }
}
