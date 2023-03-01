import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DataValueFormService } from './data-value-form.service';
import { DataValueService } from '../service/data-value.service';
import { IDataValue } from '../data-value.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { DataValueUpdateComponent } from './data-value-update.component';

describe('DataValue Management Update Component', () => {
  let comp: DataValueUpdateComponent;
  let fixture: ComponentFixture<DataValueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dataValueFormService: DataValueFormService;
  let dataValueService: DataValueService;
  let dataElementService: DataElementService;
  let periodService: PeriodService;
  let organisationUnitService: OrganisationUnitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DataValueUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DataValueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DataValueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dataValueFormService = TestBed.inject(DataValueFormService);
    dataValueService = TestBed.inject(DataValueService);
    dataElementService = TestBed.inject(DataElementService);
    periodService = TestBed.inject(PeriodService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DataElement query and add missing value', () => {
      const dataValue: IDataValue = { id: 456 };
      const dataElement: IDataElement = { id: 55249 };
      dataValue.dataElement = dataElement;

      const dataElementCollection: IDataElement[] = [{ id: 88934 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [dataElement];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dataValue });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Period query and add missing value', () => {
      const dataValue: IDataValue = { id: 456 };
      const period: IPeriod = { id: 92053 };
      dataValue.period = period;

      const periodCollection: IPeriod[] = [{ id: 59099 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dataValue });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const dataValue: IDataValue = { id: 456 };
      const source: IOrganisationUnit = { id: 79323 };
      dataValue.source = source;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 71266 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [source];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dataValue });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const dataValue: IDataValue = { id: 456 };
      const dataElement: IDataElement = { id: 36091 };
      dataValue.dataElement = dataElement;
      const period: IPeriod = { id: 99781 };
      dataValue.period = period;
      const source: IOrganisationUnit = { id: 78850 };
      dataValue.source = source;

      activatedRoute.data = of({ dataValue });
      comp.ngOnInit();

      expect(comp.dataElementsSharedCollection).toContain(dataElement);
      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.organisationUnitsSharedCollection).toContain(source);
      expect(comp.dataValue).toEqual(dataValue);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataValue>>();
      const dataValue = { id: 123 };
      jest.spyOn(dataValueFormService, 'getDataValue').mockReturnValue(dataValue);
      jest.spyOn(dataValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dataValue }));
      saveSubject.complete();

      // THEN
      expect(dataValueFormService.getDataValue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dataValueService.update).toHaveBeenCalledWith(expect.objectContaining(dataValue));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataValue>>();
      const dataValue = { id: 123 };
      jest.spyOn(dataValueFormService, 'getDataValue').mockReturnValue({ id: null });
      jest.spyOn(dataValueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataValue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dataValue }));
      saveSubject.complete();

      // THEN
      expect(dataValueFormService.getDataValue).toHaveBeenCalled();
      expect(dataValueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataValue>>();
      const dataValue = { id: 123 };
      jest.spyOn(dataValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dataValueService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDataElement', () => {
      it('Should forward to dataElementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(dataElementService, 'compareDataElement');
        comp.compareDataElement(entity, entity2);
        expect(dataElementService.compareDataElement).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePeriod', () => {
      it('Should forward to periodService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(periodService, 'comparePeriod');
        comp.comparePeriod(entity, entity2);
        expect(periodService.comparePeriod).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareOrganisationUnit', () => {
      it('Should forward to organisationUnitService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationUnitService, 'compareOrganisationUnit');
        comp.compareOrganisationUnit(entity, entity2);
        expect(organisationUnitService.compareOrganisationUnit).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
