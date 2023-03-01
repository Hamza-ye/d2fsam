import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramTempOwnershipAuditFormService } from './program-temp-ownership-audit-form.service';
import { ProgramTempOwnershipAuditService } from '../service/program-temp-ownership-audit.service';
import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';

import { ProgramTempOwnershipAuditUpdateComponent } from './program-temp-ownership-audit-update.component';

describe('ProgramTempOwnershipAudit Management Update Component', () => {
  let comp: ProgramTempOwnershipAuditUpdateComponent;
  let fixture: ComponentFixture<ProgramTempOwnershipAuditUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programTempOwnershipAuditFormService: ProgramTempOwnershipAuditFormService;
  let programTempOwnershipAuditService: ProgramTempOwnershipAuditService;
  let programService: ProgramService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramTempOwnershipAuditUpdateComponent],
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
      .overrideTemplate(ProgramTempOwnershipAuditUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramTempOwnershipAuditUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programTempOwnershipAuditFormService = TestBed.inject(ProgramTempOwnershipAuditFormService);
    programTempOwnershipAuditService = TestBed.inject(ProgramTempOwnershipAuditService);
    programService = TestBed.inject(ProgramService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Program query and add missing value', () => {
      const programTempOwnershipAudit: IProgramTempOwnershipAudit = { id: 456 };
      const program: IProgram = { id: 745 };
      programTempOwnershipAudit.program = program;

      const programCollection: IProgram[] = [{ id: 51139 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTempOwnershipAudit });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityInstance query and add missing value', () => {
      const programTempOwnershipAudit: IProgramTempOwnershipAudit = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 15185 };
      programTempOwnershipAudit.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 57465 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTempOwnershipAudit });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programTempOwnershipAudit: IProgramTempOwnershipAudit = { id: 456 };
      const program: IProgram = { id: 49795 };
      programTempOwnershipAudit.program = program;
      const entityInstance: ITrackedEntityInstance = { id: 54956 };
      programTempOwnershipAudit.entityInstance = entityInstance;

      activatedRoute.data = of({ programTempOwnershipAudit });
      comp.ngOnInit();

      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.programTempOwnershipAudit).toEqual(programTempOwnershipAudit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTempOwnershipAudit>>();
      const programTempOwnershipAudit = { id: 123 };
      jest.spyOn(programTempOwnershipAuditFormService, 'getProgramTempOwnershipAudit').mockReturnValue(programTempOwnershipAudit);
      jest.spyOn(programTempOwnershipAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTempOwnershipAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programTempOwnershipAudit }));
      saveSubject.complete();

      // THEN
      expect(programTempOwnershipAuditFormService.getProgramTempOwnershipAudit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programTempOwnershipAuditService.update).toHaveBeenCalledWith(expect.objectContaining(programTempOwnershipAudit));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTempOwnershipAudit>>();
      const programTempOwnershipAudit = { id: 123 };
      jest.spyOn(programTempOwnershipAuditFormService, 'getProgramTempOwnershipAudit').mockReturnValue({ id: null });
      jest.spyOn(programTempOwnershipAuditService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTempOwnershipAudit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programTempOwnershipAudit }));
      saveSubject.complete();

      // THEN
      expect(programTempOwnershipAuditFormService.getProgramTempOwnershipAudit).toHaveBeenCalled();
      expect(programTempOwnershipAuditService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTempOwnershipAudit>>();
      const programTempOwnershipAudit = { id: 123 };
      jest.spyOn(programTempOwnershipAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTempOwnershipAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programTempOwnershipAuditService.update).toHaveBeenCalled();
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
  });
});
