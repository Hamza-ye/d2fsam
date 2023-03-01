import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramStageInstanceFilterFormService } from './program-stage-instance-filter-form.service';
import { ProgramStageInstanceFilterService } from '../service/program-stage-instance-filter.service';
import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ProgramStageInstanceFilterUpdateComponent } from './program-stage-instance-filter-update.component';

describe('ProgramStageInstanceFilter Management Update Component', () => {
  let comp: ProgramStageInstanceFilterUpdateComponent;
  let fixture: ComponentFixture<ProgramStageInstanceFilterUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programStageInstanceFilterFormService: ProgramStageInstanceFilterFormService;
  let programStageInstanceFilterService: ProgramStageInstanceFilterService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramStageInstanceFilterUpdateComponent],
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
      .overrideTemplate(ProgramStageInstanceFilterUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramStageInstanceFilterUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programStageInstanceFilterFormService = TestBed.inject(ProgramStageInstanceFilterFormService);
    programStageInstanceFilterService = TestBed.inject(ProgramStageInstanceFilterService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const programStageInstanceFilter: IProgramStageInstanceFilter = { id: 456 };
      const createdBy: IUser = { id: 15082 };
      programStageInstanceFilter.createdBy = createdBy;
      const updatedBy: IUser = { id: 68507 };
      programStageInstanceFilter.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 76626 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstanceFilter });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programStageInstanceFilter: IProgramStageInstanceFilter = { id: 456 };
      const createdBy: IUser = { id: 10026 };
      programStageInstanceFilter.createdBy = createdBy;
      const updatedBy: IUser = { id: 5813 };
      programStageInstanceFilter.updatedBy = updatedBy;

      activatedRoute.data = of({ programStageInstanceFilter });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.programStageInstanceFilter).toEqual(programStageInstanceFilter);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageInstanceFilter>>();
      const programStageInstanceFilter = { id: 123 };
      jest.spyOn(programStageInstanceFilterFormService, 'getProgramStageInstanceFilter').mockReturnValue(programStageInstanceFilter);
      jest.spyOn(programStageInstanceFilterService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageInstanceFilter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStageInstanceFilter }));
      saveSubject.complete();

      // THEN
      expect(programStageInstanceFilterFormService.getProgramStageInstanceFilter).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programStageInstanceFilterService.update).toHaveBeenCalledWith(expect.objectContaining(programStageInstanceFilter));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageInstanceFilter>>();
      const programStageInstanceFilter = { id: 123 };
      jest.spyOn(programStageInstanceFilterFormService, 'getProgramStageInstanceFilter').mockReturnValue({ id: null });
      jest.spyOn(programStageInstanceFilterService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageInstanceFilter: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStageInstanceFilter }));
      saveSubject.complete();

      // THEN
      expect(programStageInstanceFilterFormService.getProgramStageInstanceFilter).toHaveBeenCalled();
      expect(programStageInstanceFilterService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageInstanceFilter>>();
      const programStageInstanceFilter = { id: 123 };
      jest.spyOn(programStageInstanceFilterService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageInstanceFilter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programStageInstanceFilterService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
