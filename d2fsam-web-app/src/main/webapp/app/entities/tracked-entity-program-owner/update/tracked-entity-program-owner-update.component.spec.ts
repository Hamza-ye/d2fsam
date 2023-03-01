import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityProgramOwnerFormService } from './tracked-entity-program-owner-form.service';
import { TrackedEntityProgramOwnerService } from '../service/tracked-entity-program-owner.service';
import { ITrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { TrackedEntityProgramOwnerUpdateComponent } from './tracked-entity-program-owner-update.component';

describe('TrackedEntityProgramOwner Management Update Component', () => {
  let comp: TrackedEntityProgramOwnerUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityProgramOwnerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityProgramOwnerFormService: TrackedEntityProgramOwnerFormService;
  let trackedEntityProgramOwnerService: TrackedEntityProgramOwnerService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;
  let programService: ProgramService;
  let organisationUnitService: OrganisationUnitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityProgramOwnerUpdateComponent],
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
      .overrideTemplate(TrackedEntityProgramOwnerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityProgramOwnerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityProgramOwnerFormService = TestBed.inject(TrackedEntityProgramOwnerFormService);
    trackedEntityProgramOwnerService = TestBed.inject(TrackedEntityProgramOwnerService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);
    programService = TestBed.inject(ProgramService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TrackedEntityInstance query and add missing value', () => {
      const trackedEntityProgramOwner: ITrackedEntityProgramOwner = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 58725 };
      trackedEntityProgramOwner.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 23659 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityProgramOwner });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Program query and add missing value', () => {
      const trackedEntityProgramOwner: ITrackedEntityProgramOwner = { id: 456 };
      const program: IProgram = { id: 10310 };
      trackedEntityProgramOwner.program = program;

      const programCollection: IProgram[] = [{ id: 55281 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityProgramOwner });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const trackedEntityProgramOwner: ITrackedEntityProgramOwner = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 59232 };
      trackedEntityProgramOwner.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 25042 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityProgramOwner });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityProgramOwner: ITrackedEntityProgramOwner = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 79865 };
      trackedEntityProgramOwner.entityInstance = entityInstance;
      const program: IProgram = { id: 88694 };
      trackedEntityProgramOwner.program = program;
      const organisationUnit: IOrganisationUnit = { id: 64406 };
      trackedEntityProgramOwner.organisationUnit = organisationUnit;

      activatedRoute.data = of({ trackedEntityProgramOwner });
      comp.ngOnInit();

      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.trackedEntityProgramOwner).toEqual(trackedEntityProgramOwner);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityProgramOwner>>();
      const trackedEntityProgramOwner = { id: 123 };
      jest.spyOn(trackedEntityProgramOwnerFormService, 'getTrackedEntityProgramOwner').mockReturnValue(trackedEntityProgramOwner);
      jest.spyOn(trackedEntityProgramOwnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityProgramOwner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityProgramOwner }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityProgramOwnerFormService.getTrackedEntityProgramOwner).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityProgramOwnerService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityProgramOwner));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityProgramOwner>>();
      const trackedEntityProgramOwner = { id: 123 };
      jest.spyOn(trackedEntityProgramOwnerFormService, 'getTrackedEntityProgramOwner').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityProgramOwnerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityProgramOwner: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityProgramOwner }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityProgramOwnerFormService.getTrackedEntityProgramOwner).toHaveBeenCalled();
      expect(trackedEntityProgramOwnerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityProgramOwner>>();
      const trackedEntityProgramOwner = { id: 123 };
      jest.spyOn(trackedEntityProgramOwnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityProgramOwner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityProgramOwnerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTrackedEntityInstance', () => {
      it('Should forward to trackedEntityInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trackedEntityInstanceService, 'compareTrackedEntityInstance');
        comp.compareTrackedEntityInstance(entity, entity2);
        expect(trackedEntityInstanceService.compareTrackedEntityInstance).toHaveBeenCalledWith(entity, entity2);
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
