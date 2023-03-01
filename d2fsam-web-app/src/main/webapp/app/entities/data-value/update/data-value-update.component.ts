import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataValueFormService, DataValueFormGroup } from './data-value-form.service';
import { IDataValue } from '../data-value.model';
import { DataValueService } from '../service/data-value.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

@Component({
  selector: 'app-data-value-update',
  templateUrl: './data-value-update.component.html',
})
export class DataValueUpdateComponent implements OnInit {
  isSaving = false;
  dataValue: IDataValue | null = null;
  auditTypeValues = Object.keys(AuditType);

  dataElementsSharedCollection: IDataElement[] = [];
  periodsSharedCollection: IPeriod[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];

  editForm: DataValueFormGroup = this.dataValueFormService.createDataValueFormGroup();

  constructor(
    protected dataValueService: DataValueService,
    protected dataValueFormService: DataValueFormService,
    protected dataElementService: DataElementService,
    protected periodService: PeriodService,
    protected organisationUnitService: OrganisationUnitService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dataValue }) => {
      this.dataValue = dataValue;
      if (dataValue) {
        this.updateForm(dataValue);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dataValue = this.dataValueFormService.getDataValue(this.editForm);
    if (dataValue.id !== null) {
      this.subscribeToSaveResponse(this.dataValueService.update(dataValue));
    } else {
      this.subscribeToSaveResponse(this.dataValueService.create(dataValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDataValue>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(dataValue: IDataValue): void {
    this.dataValue = dataValue;
    this.dataValueFormService.resetForm(this.editForm, dataValue);

    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      dataValue.dataElement
    );
    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      dataValue.period
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      dataValue.source
    );
  }

  protected loadRelationshipsOptions(): void {
    this.dataElementService
      .query()
      .pipe(map((res: HttpResponse<IDataElement[]>) => res.body ?? []))
      .pipe(
        map((dataElements: IDataElement[]) =>
          this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(dataElements, this.dataValue?.dataElement)
        )
      )
      .subscribe((dataElements: IDataElement[]) => (this.dataElementsSharedCollection = dataElements));

    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.dataValue?.period)))
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.dataValue?.source
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));
  }
}
