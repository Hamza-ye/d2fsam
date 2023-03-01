import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RelationshipItemFormService } from './relationship-item-form.service';
import { RelationshipItemService } from '../service/relationship-item.service';
import { IRelationshipItem } from '../relationship-item.model';
import { IRelationship } from 'app/entities/relationship/relationship.model';
import { RelationshipService } from 'app/entities/relationship/service/relationship.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { ProgramInstanceService } from 'app/entities/program-instance/service/program-instance.service';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';
import { ProgramStageInstanceService } from 'app/entities/program-stage-instance/service/program-stage-instance.service';

import { RelationshipItemUpdateComponent } from './relationship-item-update.component';

describe('RelationshipItem Management Update Component', () => {
  let comp: RelationshipItemUpdateComponent;
  let fixture: ComponentFixture<RelationshipItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relationshipItemFormService: RelationshipItemFormService;
  let relationshipItemService: RelationshipItemService;
  let relationshipService: RelationshipService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;
  let programInstanceService: ProgramInstanceService;
  let programStageInstanceService: ProgramStageInstanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RelationshipItemUpdateComponent],
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
      .overrideTemplate(RelationshipItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relationshipItemFormService = TestBed.inject(RelationshipItemFormService);
    relationshipItemService = TestBed.inject(RelationshipItemService);
    relationshipService = TestBed.inject(RelationshipService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);
    programInstanceService = TestBed.inject(ProgramInstanceService);
    programStageInstanceService = TestBed.inject(ProgramStageInstanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Relationship query and add missing value', () => {
      const relationshipItem: IRelationshipItem = { id: 456 };
      const relationship: IRelationship = { id: 18861 };
      relationshipItem.relationship = relationship;

      const relationshipCollection: IRelationship[] = [{ id: 77367 }];
      jest.spyOn(relationshipService, 'query').mockReturnValue(of(new HttpResponse({ body: relationshipCollection })));
      const additionalRelationships = [relationship];
      const expectedCollection: IRelationship[] = [...additionalRelationships, ...relationshipCollection];
      jest.spyOn(relationshipService, 'addRelationshipToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      expect(relationshipService.query).toHaveBeenCalled();
      expect(relationshipService.addRelationshipToCollectionIfMissing).toHaveBeenCalledWith(
        relationshipCollection,
        ...additionalRelationships.map(expect.objectContaining)
      );
      expect(comp.relationshipsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityInstance query and add missing value', () => {
      const relationshipItem: IRelationshipItem = { id: 456 };
      const trackedEntityInstance: ITrackedEntityInstance = { id: 41415 };
      relationshipItem.trackedEntityInstance = trackedEntityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 84906 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [trackedEntityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProgramInstance query and add missing value', () => {
      const relationshipItem: IRelationshipItem = { id: 456 };
      const programInstance: IProgramInstance = { id: 17008 };
      relationshipItem.programInstance = programInstance;

      const programInstanceCollection: IProgramInstance[] = [{ id: 67671 }];
      jest.spyOn(programInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: programInstanceCollection })));
      const additionalProgramInstances = [programInstance];
      const expectedCollection: IProgramInstance[] = [...additionalProgramInstances, ...programInstanceCollection];
      jest.spyOn(programInstanceService, 'addProgramInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      expect(programInstanceService.query).toHaveBeenCalled();
      expect(programInstanceService.addProgramInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        programInstanceCollection,
        ...additionalProgramInstances.map(expect.objectContaining)
      );
      expect(comp.programInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProgramStageInstance query and add missing value', () => {
      const relationshipItem: IRelationshipItem = { id: 456 };
      const programStageInstance: IProgramStageInstance = { id: 41131 };
      relationshipItem.programStageInstance = programStageInstance;

      const programStageInstanceCollection: IProgramStageInstance[] = [{ id: 13971 }];
      jest.spyOn(programStageInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: programStageInstanceCollection })));
      const additionalProgramStageInstances = [programStageInstance];
      const expectedCollection: IProgramStageInstance[] = [...additionalProgramStageInstances, ...programStageInstanceCollection];
      jest.spyOn(programStageInstanceService, 'addProgramStageInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      expect(programStageInstanceService.query).toHaveBeenCalled();
      expect(programStageInstanceService.addProgramStageInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        programStageInstanceCollection,
        ...additionalProgramStageInstances.map(expect.objectContaining)
      );
      expect(comp.programStageInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const relationshipItem: IRelationshipItem = { id: 456 };
      const relationship: IRelationship = { id: 96296 };
      relationshipItem.relationship = relationship;
      const trackedEntityInstance: ITrackedEntityInstance = { id: 91804 };
      relationshipItem.trackedEntityInstance = trackedEntityInstance;
      const programInstance: IProgramInstance = { id: 49322 };
      relationshipItem.programInstance = programInstance;
      const programStageInstance: IProgramStageInstance = { id: 35007 };
      relationshipItem.programStageInstance = programStageInstance;

      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      expect(comp.relationshipsSharedCollection).toContain(relationship);
      expect(comp.trackedEntityInstancesSharedCollection).toContain(trackedEntityInstance);
      expect(comp.programInstancesSharedCollection).toContain(programInstance);
      expect(comp.programStageInstancesSharedCollection).toContain(programStageInstance);
      expect(comp.relationshipItem).toEqual(relationshipItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipItem>>();
      const relationshipItem = { id: 123 };
      jest.spyOn(relationshipItemFormService, 'getRelationshipItem').mockReturnValue(relationshipItem);
      jest.spyOn(relationshipItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationshipItem }));
      saveSubject.complete();

      // THEN
      expect(relationshipItemFormService.getRelationshipItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relationshipItemService.update).toHaveBeenCalledWith(expect.objectContaining(relationshipItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipItem>>();
      const relationshipItem = { id: 123 };
      jest.spyOn(relationshipItemFormService, 'getRelationshipItem').mockReturnValue({ id: null });
      jest.spyOn(relationshipItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationshipItem }));
      saveSubject.complete();

      // THEN
      expect(relationshipItemFormService.getRelationshipItem).toHaveBeenCalled();
      expect(relationshipItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipItem>>();
      const relationshipItem = { id: 123 };
      jest.spyOn(relationshipItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relationshipItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRelationship', () => {
      it('Should forward to relationshipService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(relationshipService, 'compareRelationship');
        comp.compareRelationship(entity, entity2);
        expect(relationshipService.compareRelationship).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareProgramInstance', () => {
      it('Should forward to programInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programInstanceService, 'compareProgramInstance');
        comp.compareProgramInstance(entity, entity2);
        expect(programInstanceService.compareProgramInstance).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProgramStageInstance', () => {
      it('Should forward to programStageInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programStageInstanceService, 'compareProgramStageInstance');
        comp.compareProgramStageInstance(entity, entity2);
        expect(programStageInstanceService.compareProgramStageInstance).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
