import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramStageFormService } from './program-stage-form.service';
import { ProgramStageService } from '../service/program-stage.service';
import { IProgramStage } from '../program-stage.model';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ProgramStageUpdateComponent } from './program-stage-update.component';

describe('ProgramStage Management Update Component', () => {
  let comp: ProgramStageUpdateComponent;
  let fixture: ComponentFixture<ProgramStageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programStageFormService: ProgramStageFormService;
  let programStageService: ProgramStageService;
  let periodService: PeriodService;
  let programService: ProgramService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramStageUpdateComponent],
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
      .overrideTemplate(ProgramStageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramStageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programStageFormService = TestBed.inject(ProgramStageFormService);
    programStageService = TestBed.inject(ProgramStageService);
    periodService = TestBed.inject(PeriodService);
    programService = TestBed.inject(ProgramService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Period query and add missing value', () => {
      const programStage: IProgramStage = { id: 456 };
      const periodType: IPeriod = { id: 95383 };
      programStage.periodType = periodType;

      const periodCollection: IPeriod[] = [{ id: 43564 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [periodType];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStage });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Program query and add missing value', () => {
      const programStage: IProgramStage = { id: 456 };
      const program: IProgram = { id: 5328 };
      programStage.program = program;

      const programCollection: IProgram[] = [{ id: 11001 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStage });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const programStage: IProgramStage = { id: 456 };
      const createdBy: IUser = { id: 3691 };
      programStage.createdBy = createdBy;
      const updatedBy: IUser = { id: 52690 };
      programStage.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 99501 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStage });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programStage: IProgramStage = { id: 456 };
      const periodType: IPeriod = { id: 45741 };
      programStage.periodType = periodType;
      const program: IProgram = { id: 75023 };
      programStage.program = program;
      const createdBy: IUser = { id: 82734 };
      programStage.createdBy = createdBy;
      const updatedBy: IUser = { id: 33984 };
      programStage.updatedBy = updatedBy;

      activatedRoute.data = of({ programStage });
      comp.ngOnInit();

      expect(comp.periodsSharedCollection).toContain(periodType);
      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.programStage).toEqual(programStage);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStage>>();
      const programStage = { id: 123 };
      jest.spyOn(programStageFormService, 'getProgramStage').mockReturnValue(programStage);
      jest.spyOn(programStageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStage }));
      saveSubject.complete();

      // THEN
      expect(programStageFormService.getProgramStage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programStageService.update).toHaveBeenCalledWith(expect.objectContaining(programStage));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStage>>();
      const programStage = { id: 123 };
      jest.spyOn(programStageFormService, 'getProgramStage').mockReturnValue({ id: null });
      jest.spyOn(programStageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStage }));
      saveSubject.complete();

      // THEN
      expect(programStageFormService.getProgramStage).toHaveBeenCalled();
      expect(programStageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStage>>();
      const programStage = { id: 123 };
      jest.spyOn(programStageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programStageService.update).toHaveBeenCalled();
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
