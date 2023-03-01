import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityInstanceFormService } from './tracked-entity-instance-form.service';
import { TrackedEntityInstanceService } from '../service/tracked-entity-instance.service';
import { ITrackedEntityInstance } from '../tracked-entity-instance.model';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TrackedEntityInstanceUpdateComponent } from './tracked-entity-instance-update.component';

describe('TrackedEntityInstance Management Update Component', () => {
  let comp: TrackedEntityInstanceUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityInstanceFormService: TrackedEntityInstanceFormService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;
  let periodService: PeriodService;
  let organisationUnitService: OrganisationUnitService;
  let trackedEntityTypeService: TrackedEntityTypeService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityInstanceUpdateComponent],
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
      .overrideTemplate(TrackedEntityInstanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityInstanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityInstanceFormService = TestBed.inject(TrackedEntityInstanceFormService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);
    periodService = TestBed.inject(PeriodService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    trackedEntityTypeService = TestBed.inject(TrackedEntityTypeService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Period query and add missing value', () => {
      const trackedEntityInstance: ITrackedEntityInstance = { id: 456 };
      const period: IPeriod = { id: 53817 };
      trackedEntityInstance.period = period;

      const periodCollection: IPeriod[] = [{ id: 8241 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const trackedEntityInstance: ITrackedEntityInstance = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 48758 };
      trackedEntityInstance.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 54682 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityType query and add missing value', () => {
      const trackedEntityInstance: ITrackedEntityInstance = { id: 456 };
      const trackedEntityType: ITrackedEntityType = { id: 22155 };
      trackedEntityInstance.trackedEntityType = trackedEntityType;

      const trackedEntityTypeCollection: ITrackedEntityType[] = [{ id: 9362 }];
      jest.spyOn(trackedEntityTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityTypeCollection })));
      const additionalTrackedEntityTypes = [trackedEntityType];
      const expectedCollection: ITrackedEntityType[] = [...additionalTrackedEntityTypes, ...trackedEntityTypeCollection];
      jest.spyOn(trackedEntityTypeService, 'addTrackedEntityTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      expect(trackedEntityTypeService.query).toHaveBeenCalled();
      expect(trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityTypeCollection,
        ...additionalTrackedEntityTypes.map(expect.objectContaining)
      );
      expect(comp.trackedEntityTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const trackedEntityInstance: ITrackedEntityInstance = { id: 456 };
      const createdBy: IUser = { id: 15411 };
      trackedEntityInstance.createdBy = createdBy;
      const updatedBy: IUser = { id: 57330 };
      trackedEntityInstance.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 35486 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityInstance: ITrackedEntityInstance = { id: 456 };
      const period: IPeriod = { id: 73678 };
      trackedEntityInstance.period = period;
      const organisationUnit: IOrganisationUnit = { id: 12147 };
      trackedEntityInstance.organisationUnit = organisationUnit;
      const trackedEntityType: ITrackedEntityType = { id: 5519 };
      trackedEntityInstance.trackedEntityType = trackedEntityType;
      const createdBy: IUser = { id: 65146 };
      trackedEntityInstance.createdBy = createdBy;
      const updatedBy: IUser = { id: 97515 };
      trackedEntityInstance.updatedBy = updatedBy;

      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.trackedEntityTypesSharedCollection).toContain(trackedEntityType);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.trackedEntityInstance).toEqual(trackedEntityInstance);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstance>>();
      const trackedEntityInstance = { id: 123 };
      jest.spyOn(trackedEntityInstanceFormService, 'getTrackedEntityInstance').mockReturnValue(trackedEntityInstance);
      jest.spyOn(trackedEntityInstanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityInstance }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityInstanceFormService.getTrackedEntityInstance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityInstanceService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityInstance));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstance>>();
      const trackedEntityInstance = { id: 123 };
      jest.spyOn(trackedEntityInstanceFormService, 'getTrackedEntityInstance').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityInstanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityInstance }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityInstanceFormService.getTrackedEntityInstance).toHaveBeenCalled();
      expect(trackedEntityInstanceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstance>>();
      const trackedEntityInstance = { id: 123 };
      jest.spyOn(trackedEntityInstanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityInstanceService.update).toHaveBeenCalled();
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

    describe('compareOrganisationUnit', () => {
      it('Should forward to organisationUnitService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationUnitService, 'compareOrganisationUnit');
        comp.compareOrganisationUnit(entity, entity2);
        expect(organisationUnitService.compareOrganisationUnit).toHaveBeenCalledWith(entity, entity2);
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
  });
});
