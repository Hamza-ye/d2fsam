import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramOwnershipHistoryFormService } from './program-ownership-history-form.service';
import { ProgramOwnershipHistoryService } from '../service/program-ownership-history.service';
import { IProgramOwnershipHistory } from '../program-ownership-history.model';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { ProgramOwnershipHistoryUpdateComponent } from './program-ownership-history-update.component';

describe('ProgramOwnershipHistory Management Update Component', () => {
  let comp: ProgramOwnershipHistoryUpdateComponent;
  let fixture: ComponentFixture<ProgramOwnershipHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programOwnershipHistoryFormService: ProgramOwnershipHistoryFormService;
  let programOwnershipHistoryService: ProgramOwnershipHistoryService;
  let programService: ProgramService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;
  let organisationUnitService: OrganisationUnitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramOwnershipHistoryUpdateComponent],
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
      .overrideTemplate(ProgramOwnershipHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramOwnershipHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programOwnershipHistoryFormService = TestBed.inject(ProgramOwnershipHistoryFormService);
    programOwnershipHistoryService = TestBed.inject(ProgramOwnershipHistoryService);
    programService = TestBed.inject(ProgramService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Program query and add missing value', () => {
      const programOwnershipHistory: IProgramOwnershipHistory = { id: 456 };
      const program: IProgram = { id: 7040 };
      programOwnershipHistory.program = program;

      const programCollection: IProgram[] = [{ id: 60092 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programOwnershipHistory });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityInstance query and add missing value', () => {
      const programOwnershipHistory: IProgramOwnershipHistory = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 22373 };
      programOwnershipHistory.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 71017 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programOwnershipHistory });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const programOwnershipHistory: IProgramOwnershipHistory = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 23334 };
      programOwnershipHistory.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 27736 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programOwnershipHistory });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programOwnershipHistory: IProgramOwnershipHistory = { id: 456 };
      const program: IProgram = { id: 73826 };
      programOwnershipHistory.program = program;
      const entityInstance: ITrackedEntityInstance = { id: 12665 };
      programOwnershipHistory.entityInstance = entityInstance;
      const organisationUnit: IOrganisationUnit = { id: 71102 };
      programOwnershipHistory.organisationUnit = organisationUnit;

      activatedRoute.data = of({ programOwnershipHistory });
      comp.ngOnInit();

      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.programOwnershipHistory).toEqual(programOwnershipHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramOwnershipHistory>>();
      const programOwnershipHistory = { id: 123 };
      jest.spyOn(programOwnershipHistoryFormService, 'getProgramOwnershipHistory').mockReturnValue(programOwnershipHistory);
      jest.spyOn(programOwnershipHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programOwnershipHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programOwnershipHistory }));
      saveSubject.complete();

      // THEN
      expect(programOwnershipHistoryFormService.getProgramOwnershipHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programOwnershipHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(programOwnershipHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramOwnershipHistory>>();
      const programOwnershipHistory = { id: 123 };
      jest.spyOn(programOwnershipHistoryFormService, 'getProgramOwnershipHistory').mockReturnValue({ id: null });
      jest.spyOn(programOwnershipHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programOwnershipHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programOwnershipHistory }));
      saveSubject.complete();

      // THEN
      expect(programOwnershipHistoryFormService.getProgramOwnershipHistory).toHaveBeenCalled();
      expect(programOwnershipHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramOwnershipHistory>>();
      const programOwnershipHistory = { id: 123 };
      jest.spyOn(programOwnershipHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programOwnershipHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programOwnershipHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProgram', () => {
      it('Should forward to programService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programService, 'compareProgram');
        comp.compareProgram(entity, entity2);
        expect(programService.compareProgram).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTrackedEntityInstance', () => {
      it('Should forward to trackedEntityInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trackedEntityInstanceService, 'compareTrackedEntityInstance');
        comp.compareTrackedEntityInstance(entity, entity2);
        expect(trackedEntityInstanceService.compareTrackedEntityInstance).toHaveBeenCalledWith(entity, entity2);
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
