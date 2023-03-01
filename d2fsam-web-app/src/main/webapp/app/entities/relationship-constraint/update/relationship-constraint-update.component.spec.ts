import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RelationshipConstraintFormService } from './relationship-constraint-form.service';
import { RelationshipConstraintService } from '../service/relationship-constraint.service';
import { IRelationshipConstraint } from '../relationship-constraint.model';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { ProgramStageService } from 'app/entities/program-stage/service/program-stage.service';

import { RelationshipConstraintUpdateComponent } from './relationship-constraint-update.component';

describe('RelationshipConstraint Management Update Component', () => {
  let comp: RelationshipConstraintUpdateComponent;
  let fixture: ComponentFixture<RelationshipConstraintUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relationshipConstraintFormService: RelationshipConstraintFormService;
  let relationshipConstraintService: RelationshipConstraintService;
  let trackedEntityTypeService: TrackedEntityTypeService;
  let programService: ProgramService;
  let programStageService: ProgramStageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RelationshipConstraintUpdateComponent],
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
      .overrideTemplate(RelationshipConstraintUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipConstraintUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relationshipConstraintFormService = TestBed.inject(RelationshipConstraintFormService);
    relationshipConstraintService = TestBed.inject(RelationshipConstraintService);
    trackedEntityTypeService = TestBed.inject(TrackedEntityTypeService);
    programService = TestBed.inject(ProgramService);
    programStageService = TestBed.inject(ProgramStageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TrackedEntityType query and add missing value', () => {
      const relationshipConstraint: IRelationshipConstraint = { id: 456 };
      const trackedEntityType: ITrackedEntityType = { id: 79140 };
      relationshipConstraint.trackedEntityType = trackedEntityType;

      const trackedEntityTypeCollection: ITrackedEntityType[] = [{ id: 12050 }];
      jest.spyOn(trackedEntityTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityTypeCollection })));
      const additionalTrackedEntityTypes = [trackedEntityType];
      const expectedCollection: ITrackedEntityType[] = [...additionalTrackedEntityTypes, ...trackedEntityTypeCollection];
      jest.spyOn(trackedEntityTypeService, 'addTrackedEntityTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipConstraint });
      comp.ngOnInit();

      expect(trackedEntityTypeService.query).toHaveBeenCalled();
      expect(trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityTypeCollection,
        ...additionalTrackedEntityTypes.map(expect.objectContaining)
      );
      expect(comp.trackedEntityTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Program query and add missing value', () => {
      const relationshipConstraint: IRelationshipConstraint = { id: 456 };
      const program: IProgram = { id: 52614 };
      relationshipConstraint.program = program;

      const programCollection: IProgram[] = [{ id: 66348 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipConstraint });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProgramStage query and add missing value', () => {
      const relationshipConstraint: IRelationshipConstraint = { id: 456 };
      const programStage: IProgramStage = { id: 28515 };
      relationshipConstraint.programStage = programStage;

      const programStageCollection: IProgramStage[] = [{ id: 21560 }];
      jest.spyOn(programStageService, 'query').mockReturnValue(of(new HttpResponse({ body: programStageCollection })));
      const additionalProgramStages = [programStage];
      const expectedCollection: IProgramStage[] = [...additionalProgramStages, ...programStageCollection];
      jest.spyOn(programStageService, 'addProgramStageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipConstraint });
      comp.ngOnInit();

      expect(programStageService.query).toHaveBeenCalled();
      expect(programStageService.addProgramStageToCollectionIfMissing).toHaveBeenCalledWith(
        programStageCollection,
        ...additionalProgramStages.map(expect.objectContaining)
      );
      expect(comp.programStagesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const relationshipConstraint: IRelationshipConstraint = { id: 456 };
      const trackedEntityType: ITrackedEntityType = { id: 46907 };
      relationshipConstraint.trackedEntityType = trackedEntityType;
      const program: IProgram = { id: 11820 };
      relationshipConstraint.program = program;
      const programStage: IProgramStage = { id: 52547 };
      relationshipConstraint.programStage = programStage;

      activatedRoute.data = of({ relationshipConstraint });
      comp.ngOnInit();

      expect(comp.trackedEntityTypesSharedCollection).toContain(trackedEntityType);
      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.programStagesSharedCollection).toContain(programStage);
      expect(comp.relationshipConstraint).toEqual(relationshipConstraint);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipConstraint>>();
      const relationshipConstraint = { id: 123 };
      jest.spyOn(relationshipConstraintFormService, 'getRelationshipConstraint').mockReturnValue(relationshipConstraint);
      jest.spyOn(relationshipConstraintService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipConstraint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationshipConstraint }));
      saveSubject.complete();

      // THEN
      expect(relationshipConstraintFormService.getRelationshipConstraint).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relationshipConstraintService.update).toHaveBeenCalledWith(expect.objectContaining(relationshipConstraint));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipConstraint>>();
      const relationshipConstraint = { id: 123 };
      jest.spyOn(relationshipConstraintFormService, 'getRelationshipConstraint').mockReturnValue({ id: null });
      jest.spyOn(relationshipConstraintService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipConstraint: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationshipConstraint }));
      saveSubject.complete();

      // THEN
      expect(relationshipConstraintFormService.getRelationshipConstraint).toHaveBeenCalled();
      expect(relationshipConstraintService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipConstraint>>();
      const relationshipConstraint = { id: 123 };
      jest.spyOn(relationshipConstraintService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipConstraint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relationshipConstraintService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTrackedEntityType', () => {
      it('Should forward to trackedEntityTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trackedEntityTypeService, 'compareTrackedEntityType');
        comp.compareTrackedEntityType(entity, entity2);
        expect(trackedEntityTypeService.compareTrackedEntityType).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareProgramStage', () => {
      it('Should forward to programStageService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programStageService, 'compareProgramStage');
        comp.compareProgramStage(entity, entity2);
        expect(programStageService.compareProgramStage).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
