import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityDataValueAuditFormService } from './tracked-entity-data-value-audit-form.service';
import { TrackedEntityDataValueAuditService } from '../service/tracked-entity-data-value-audit.service';
import { ITrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';
import { ProgramStageInstanceService } from 'app/entities/program-stage-instance/service/program-stage-instance.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';

import { TrackedEntityDataValueAuditUpdateComponent } from './tracked-entity-data-value-audit-update.component';

describe('TrackedEntityDataValueAudit Management Update Component', () => {
  let comp: TrackedEntityDataValueAuditUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityDataValueAuditUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityDataValueAuditFormService: TrackedEntityDataValueAuditFormService;
  let trackedEntityDataValueAuditService: TrackedEntityDataValueAuditService;
  let programStageInstanceService: ProgramStageInstanceService;
  let dataElementService: DataElementService;
  let periodService: PeriodService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityDataValueAuditUpdateComponent],
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
      .overrideTemplate(TrackedEntityDataValueAuditUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityDataValueAuditUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityDataValueAuditFormService = TestBed.inject(TrackedEntityDataValueAuditFormService);
    trackedEntityDataValueAuditService = TestBed.inject(TrackedEntityDataValueAuditService);
    programStageInstanceService = TestBed.inject(ProgramStageInstanceService);
    dataElementService = TestBed.inject(DataElementService);
    periodService = TestBed.inject(PeriodService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProgramStageInstance query and add missing value', () => {
      const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = { id: 456 };
      const programStageInstance: IProgramStageInstance = { id: 49709 };
      trackedEntityDataValueAudit.programStageInstance = programStageInstance;

      const programStageInstanceCollection: IProgramStageInstance[] = [{ id: 64723 }];
      jest.spyOn(programStageInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: programStageInstanceCollection })));
      const additionalProgramStageInstances = [programStageInstance];
      const expectedCollection: IProgramStageInstance[] = [...additionalProgramStageInstances, ...programStageInstanceCollection];
      jest.spyOn(programStageInstanceService, 'addProgramStageInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityDataValueAudit });
      comp.ngOnInit();

      expect(programStageInstanceService.query).toHaveBeenCalled();
      expect(programStageInstanceService.addProgramStageInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        programStageInstanceCollection,
        ...additionalProgramStageInstances.map(expect.objectContaining)
      );
      expect(comp.programStageInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call DataElement query and add missing value', () => {
      const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = { id: 456 };
      const dataElement: IDataElement = { id: 72227 };
      trackedEntityDataValueAudit.dataElement = dataElement;

      const dataElementCollection: IDataElement[] = [{ id: 42734 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [dataElement];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityDataValueAudit });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Period query and add missing value', () => {
      const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = { id: 456 };
      const period: IPeriod = { id: 21956 };
      trackedEntityDataValueAudit.period = period;

      const periodCollection: IPeriod[] = [{ id: 45319 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityDataValueAudit });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = { id: 456 };
      const programStageInstance: IProgramStageInstance = { id: 67859 };
      trackedEntityDataValueAudit.programStageInstance = programStageInstance;
      const dataElement: IDataElement = { id: 17175 };
      trackedEntityDataValueAudit.dataElement = dataElement;
      const period: IPeriod = { id: 2568 };
      trackedEntityDataValueAudit.period = period;

      activatedRoute.data = of({ trackedEntityDataValueAudit });
      comp.ngOnInit();

      expect(comp.programStageInstancesSharedCollection).toContain(programStageInstance);
      expect(comp.dataElementsSharedCollection).toContain(dataElement);
      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.trackedEntityDataValueAudit).toEqual(trackedEntityDataValueAudit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityDataValueAudit>>();
      const trackedEntityDataValueAudit = { id: 123 };
      jest.spyOn(trackedEntityDataValueAuditFormService, 'getTrackedEntityDataValueAudit').mockReturnValue(trackedEntityDataValueAudit);
      jest.spyOn(trackedEntityDataValueAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityDataValueAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityDataValueAudit }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityDataValueAuditFormService.getTrackedEntityDataValueAudit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityDataValueAuditService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityDataValueAudit));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityDataValueAudit>>();
      const trackedEntityDataValueAudit = { id: 123 };
      jest.spyOn(trackedEntityDataValueAuditFormService, 'getTrackedEntityDataValueAudit').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityDataValueAuditService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityDataValueAudit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityDataValueAudit }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityDataValueAuditFormService.getTrackedEntityDataValueAudit).toHaveBeenCalled();
      expect(trackedEntityDataValueAuditService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityDataValueAudit>>();
      const trackedEntityDataValueAudit = { id: 123 };
      jest.spyOn(trackedEntityDataValueAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityDataValueAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityDataValueAuditService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProgramStageInstance', () => {
      it('Should forward to programStageInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programStageInstanceService, 'compareProgramStageInstance');
        comp.compareProgramStageInstance(entity, entity2);
        expect(programStageInstanceService.compareProgramStageInstance).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
  });
});
