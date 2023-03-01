import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DataInputPeriodFormService } from './data-input-period-form.service';
import { DataInputPeriodService } from '../service/data-input-period.service';
import { IDataInputPeriod } from '../data-input-period.model';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';

import { DataInputPeriodUpdateComponent } from './data-input-period-update.component';

describe('DataInputPeriod Management Update Component', () => {
  let comp: DataInputPeriodUpdateComponent;
  let fixture: ComponentFixture<DataInputPeriodUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dataInputPeriodFormService: DataInputPeriodFormService;
  let dataInputPeriodService: DataInputPeriodService;
  let periodService: PeriodService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DataInputPeriodUpdateComponent],
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
      .overrideTemplate(DataInputPeriodUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DataInputPeriodUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dataInputPeriodFormService = TestBed.inject(DataInputPeriodFormService);
    dataInputPeriodService = TestBed.inject(DataInputPeriodService);
    periodService = TestBed.inject(PeriodService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Period query and add missing value', () => {
      const dataInputPeriod: IDataInputPeriod = { id: 456 };
      const period: IPeriod = { id: 63372 };
      dataInputPeriod.period = period;

      const periodCollection: IPeriod[] = [{ id: 76546 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dataInputPeriod });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const dataInputPeriod: IDataInputPeriod = { id: 456 };
      const period: IPeriod = { id: 24795 };
      dataInputPeriod.period = period;

      activatedRoute.data = of({ dataInputPeriod });
      comp.ngOnInit();

      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.dataInputPeriod).toEqual(dataInputPeriod);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataInputPeriod>>();
      const dataInputPeriod = { id: 123 };
      jest.spyOn(dataInputPeriodFormService, 'getDataInputPeriod').mockReturnValue(dataInputPeriod);
      jest.spyOn(dataInputPeriodService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataInputPeriod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dataInputPeriod }));
      saveSubject.complete();

      // THEN
      expect(dataInputPeriodFormService.getDataInputPeriod).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dataInputPeriodService.update).toHaveBeenCalledWith(expect.objectContaining(dataInputPeriod));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataInputPeriod>>();
      const dataInputPeriod = { id: 123 };
      jest.spyOn(dataInputPeriodFormService, 'getDataInputPeriod').mockReturnValue({ id: null });
      jest.spyOn(dataInputPeriodService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataInputPeriod: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dataInputPeriod }));
      saveSubject.complete();

      // THEN
      expect(dataInputPeriodFormService.getDataInputPeriod).toHaveBeenCalled();
      expect(dataInputPeriodService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataInputPeriod>>();
      const dataInputPeriod = { id: 123 };
      jest.spyOn(dataInputPeriodService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataInputPeriod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dataInputPeriodService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePeriod', () => {
      it('Should forward to periodService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(periodService, 'comparePeriod');
        comp.comparePeriod(entity, entity2);
        expect(periodService.comparePeriod).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
