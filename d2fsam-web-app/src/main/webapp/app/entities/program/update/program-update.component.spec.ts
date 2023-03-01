import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramFormService } from './program-form.service';
import { ProgramService } from '../service/program.service';
import { IProgram } from '../program.model';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IPeriodType } from 'app/entities/period-type/period-type.model';
import { PeriodTypeService } from 'app/entities/period-type/service/period-type.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { ProgramUpdateComponent } from './program-update.component';

describe('Program Management Update Component', () => {
  let comp: ProgramUpdateComponent;
  let fixture: ComponentFixture<ProgramUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programFormService: ProgramFormService;
  let programService: ProgramService;
  let periodService: PeriodService;
  let periodTypeService: PeriodTypeService;
  let trackedEntityTypeService: TrackedEntityTypeService;
  let userService: UserService;
  let organisationUnitService: OrganisationUnitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramUpdateComponent],
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
      .overrideTemplate(ProgramUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programFormService = TestBed.inject(ProgramFormService);
    programService = TestBed.inject(ProgramService);
    periodService = TestBed.inject(PeriodService);
    periodTypeService = TestBed.inject(PeriodTypeService);
    trackedEntityTypeService = TestBed.inject(TrackedEntityTypeService);
    userService = TestBed.inject(UserService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Period query and add missing value', () => {
      const program: IProgram = { id: 456 };
      const period: IPeriod = { id: 49250 };
      program.period = period;

      const periodCollection: IPeriod[] = [{ id: 34354 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call PeriodType query and add missing value', () => {
      const program: IProgram = { id: 456 };
      const expiryPeriodType: IPeriodType = { id: 71818 };
      program.expiryPeriodType = expiryPeriodType;

      const periodTypeCollection: IPeriodType[] = [{ id: 57922 }];
      jest.spyOn(periodTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: periodTypeCollection })));
      const additionalPeriodTypes = [expiryPeriodType];
      const expectedCollection: IPeriodType[] = [...additionalPeriodTypes, ...periodTypeCollection];
      jest.spyOn(periodTypeService, 'addPeriodTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(periodTypeService.query).toHaveBeenCalled();
      expect(periodTypeService.addPeriodTypeToCollectionIfMissing).toHaveBeenCalledWith(
        periodTypeCollection,
        ...additionalPeriodTypes.map(expect.objectContaining)
      );
      expect(comp.periodTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Program query and add missing value', () => {
      const program: IProgram = { id: 456 };
      const relatedProgram: IProgram = { id: 48704 };
      program.relatedProgram = relatedProgram;

      const programCollection: IProgram[] = [{ id: 95586 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [relatedProgram];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityType query and add missing value', () => {
      const program: IProgram = { id: 456 };
      const trackedEntityType: ITrackedEntityType = { id: 11381 };
      program.trackedEntityType = trackedEntityType;

      const trackedEntityTypeCollection: ITrackedEntityType[] = [{ id: 65651 }];
      jest.spyOn(trackedEntityTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityTypeCollection })));
      const additionalTrackedEntityTypes = [trackedEntityType];
      const expectedCollection: ITrackedEntityType[] = [...additionalTrackedEntityTypes, ...trackedEntityTypeCollection];
      jest.spyOn(trackedEntityTypeService, 'addTrackedEntityTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(trackedEntityTypeService.query).toHaveBeenCalled();
      expect(trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityTypeCollection,
        ...additionalTrackedEntityTypes.map(expect.objectContaining)
      );
      expect(comp.trackedEntityTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const program: IProgram = { id: 456 };
      const createdBy: IUser = { id: 71979 };
      program.createdBy = createdBy;
      const updatedBy: IUser = { id: 43123 };
      program.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 32794 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const program: IProgram = { id: 456 };
      const organisationUnits: IOrganisationUnit[] = [{ id: 23919 }];
      program.organisationUnits = organisationUnits;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 93464 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [...organisationUnits];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const program: IProgram = { id: 456 };
      const period: IPeriod = { id: 10609 };
      program.period = period;
      const expiryPeriodType: IPeriodType = { id: 81814 };
      program.expiryPeriodType = expiryPeriodType;
      const relatedProgram: IProgram = { id: 69524 };
      program.relatedProgram = relatedProgram;
      const trackedEntityType: ITrackedEntityType = { id: 48936 };
      program.trackedEntityType = trackedEntityType;
      const createdBy: IUser = { id: 65966 };
      program.createdBy = createdBy;
      const updatedBy: IUser = { id: 80318 };
      program.updatedBy = updatedBy;
      const organisationUnit: IOrganisationUnit = { id: 1548 };
      program.organisationUnits = [organisationUnit];

      activatedRoute.data = of({ program });
      comp.ngOnInit();

      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.periodTypesSharedCollection).toContain(expiryPeriodType);
      expect(comp.programsSharedCollection).toContain(relatedProgram);
      expect(comp.trackedEntityTypesSharedCollection).toContain(trackedEntityType);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.program).toEqual(program);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgram>>();
      const program = { id: 123 };
      jest.spyOn(programFormService, 'getProgram').mockReturnValue(program);
      jest.spyOn(programService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ program });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: program }));
      saveSubject.complete();

      // THEN
      expect(programFormService.getProgram).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programService.update).toHaveBeenCalledWith(expect.objectContaining(program));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgram>>();
      const program = { id: 123 };
      jest.spyOn(programFormService, 'getProgram').mockReturnValue({ id: null });
      jest.spyOn(programService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ program: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: program }));
      saveSubject.complete();

      // THEN
      expect(programFormService.getProgram).toHaveBeenCalled();
      expect(programService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgram>>();
      const program = { id: 123 };
      jest.spyOn(programService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ program });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programService.update).toHaveBeenCalled();
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

    describe('comparePeriodType', () => {
      it('Should forward to periodTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(periodTypeService, 'comparePeriodType');
        comp.comparePeriodType(entity, entity2);
        expect(periodTypeService.comparePeriodType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProgram', () => {
      it('Should forward to programService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programService, 'compareProgram');
        comp.compareProgram(entity, entity2);
        expect(programService.compareProgram).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTrackedEntityType', () => {
      it('Should forward to trackedEntityTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trackedEntityTypeService, 'compareTrackedEntityType');
        comp.compareTrackedEntityType(entity, entity2);
        expect(trackedEntityTypeService.compareTrackedEntityType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
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
