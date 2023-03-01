import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityInstanceFilterFormService } from './tracked-entity-instance-filter-form.service';
import { TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';
import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TrackedEntityInstanceFilterUpdateComponent } from './tracked-entity-instance-filter-update.component';

describe('TrackedEntityInstanceFilter Management Update Component', () => {
  let comp: TrackedEntityInstanceFilterUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceFilterUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityInstanceFilterFormService: TrackedEntityInstanceFilterFormService;
  let trackedEntityInstanceFilterService: TrackedEntityInstanceFilterService;
  let programService: ProgramService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityInstanceFilterUpdateComponent],
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
      .overrideTemplate(TrackedEntityInstanceFilterUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityInstanceFilterUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityInstanceFilterFormService = TestBed.inject(TrackedEntityInstanceFilterFormService);
    trackedEntityInstanceFilterService = TestBed.inject(TrackedEntityInstanceFilterService);
    programService = TestBed.inject(ProgramService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Program query and add missing value', () => {
      const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = { id: 456 };
      const program: IProgram = { id: 64783 };
      trackedEntityInstanceFilter.program = program;

      const programCollection: IProgram[] = [{ id: 67281 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityInstanceFilter });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = { id: 456 };
      const createdBy: IUser = { id: 96849 };
      trackedEntityInstanceFilter.createdBy = createdBy;
      const updatedBy: IUser = { id: 79794 };
      trackedEntityInstanceFilter.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 93460 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityInstanceFilter });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = { id: 456 };
      const program: IProgram = { id: 98899 };
      trackedEntityInstanceFilter.program = program;
      const createdBy: IUser = { id: 60458 };
      trackedEntityInstanceFilter.createdBy = createdBy;
      const updatedBy: IUser = { id: 12445 };
      trackedEntityInstanceFilter.updatedBy = updatedBy;

      activatedRoute.data = of({ trackedEntityInstanceFilter });
      comp.ngOnInit();

      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.trackedEntityInstanceFilter).toEqual(trackedEntityInstanceFilter);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstanceFilter>>();
      const trackedEntityInstanceFilter = { id: 123 };
      jest.spyOn(trackedEntityInstanceFilterFormService, 'getTrackedEntityInstanceFilter').mockReturnValue(trackedEntityInstanceFilter);
      jest.spyOn(trackedEntityInstanceFilterService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstanceFilter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityInstanceFilter }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityInstanceFilterFormService.getTrackedEntityInstanceFilter).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityInstanceFilterService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityInstanceFilter));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstanceFilter>>();
      const trackedEntityInstanceFilter = { id: 123 };
      jest.spyOn(trackedEntityInstanceFilterFormService, 'getTrackedEntityInstanceFilter').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityInstanceFilterService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstanceFilter: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityInstanceFilter }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityInstanceFilterFormService.getTrackedEntityInstanceFilter).toHaveBeenCalled();
      expect(trackedEntityInstanceFilterService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstanceFilter>>();
      const trackedEntityInstanceFilter = { id: 123 };
      jest.spyOn(trackedEntityInstanceFilterService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstanceFilter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityInstanceFilterService.update).toHaveBeenCalled();
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
