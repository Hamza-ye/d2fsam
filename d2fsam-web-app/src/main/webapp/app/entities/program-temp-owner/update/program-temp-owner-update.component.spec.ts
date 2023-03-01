import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramTempOwnerFormService } from './program-temp-owner-form.service';
import { ProgramTempOwnerService } from '../service/program-temp-owner.service';
import { IProgramTempOwner } from '../program-temp-owner.model';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ProgramTempOwnerUpdateComponent } from './program-temp-owner-update.component';

describe('ProgramTempOwner Management Update Component', () => {
  let comp: ProgramTempOwnerUpdateComponent;
  let fixture: ComponentFixture<ProgramTempOwnerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programTempOwnerFormService: ProgramTempOwnerFormService;
  let programTempOwnerService: ProgramTempOwnerService;
  let programService: ProgramService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramTempOwnerUpdateComponent],
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
      .overrideTemplate(ProgramTempOwnerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramTempOwnerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programTempOwnerFormService = TestBed.inject(ProgramTempOwnerFormService);
    programTempOwnerService = TestBed.inject(ProgramTempOwnerService);
    programService = TestBed.inject(ProgramService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Program query and add missing value', () => {
      const programTempOwner: IProgramTempOwner = { id: 456 };
      const program: IProgram = { id: 87310 };
      programTempOwner.program = program;

      const programCollection: IProgram[] = [{ id: 20688 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTempOwner });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityInstance query and add missing value', () => {
      const programTempOwner: IProgramTempOwner = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 1599 };
      programTempOwner.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 70452 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTempOwner });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const programTempOwner: IProgramTempOwner = { id: 456 };
      const user: IUser = { id: 18203 };
      programTempOwner.user = user;

      const userCollection: IUser[] = [{ id: 94657 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTempOwner });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programTempOwner: IProgramTempOwner = { id: 456 };
      const program: IProgram = { id: 86336 };
      programTempOwner.program = program;
      const entityInstance: ITrackedEntityInstance = { id: 58469 };
      programTempOwner.entityInstance = entityInstance;
      const user: IUser = { id: 96340 };
      programTempOwner.user = user;

      activatedRoute.data = of({ programTempOwner });
      comp.ngOnInit();

      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.programTempOwner).toEqual(programTempOwner);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTempOwner>>();
      const programTempOwner = { id: 123 };
      jest.spyOn(programTempOwnerFormService, 'getProgramTempOwner').mockReturnValue(programTempOwner);
      jest.spyOn(programTempOwnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTempOwner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programTempOwner }));
      saveSubject.complete();

      // THEN
      expect(programTempOwnerFormService.getProgramTempOwner).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programTempOwnerService.update).toHaveBeenCalledWith(expect.objectContaining(programTempOwner));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTempOwner>>();
      const programTempOwner = { id: 123 };
      jest.spyOn(programTempOwnerFormService, 'getProgramTempOwner').mockReturnValue({ id: null });
      jest.spyOn(programTempOwnerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTempOwner: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programTempOwner }));
      saveSubject.complete();

      // THEN
      expect(programTempOwnerFormService.getProgramTempOwner).toHaveBeenCalled();
      expect(programTempOwnerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTempOwner>>();
      const programTempOwner = { id: 123 };
      jest.spyOn(programTempOwnerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTempOwner });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programTempOwnerService.update).toHaveBeenCalled();
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
