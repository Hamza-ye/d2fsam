import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramStageInstanceFormService } from './program-stage-instance-form.service';
import { ProgramStageInstanceService } from '../service/program-stage-instance.service';
import { IProgramStageInstance } from '../program-stage-instance.model';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { ProgramInstanceService } from 'app/entities/program-instance/service/program-instance.service';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { ProgramStageService } from 'app/entities/program-stage/service/program-stage.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IComment } from 'app/entities/comment/comment.model';
import { CommentService } from 'app/entities/comment/service/comment.service';

import { ProgramStageInstanceUpdateComponent } from './program-stage-instance-update.component';

describe('ProgramStageInstance Management Update Component', () => {
  let comp: ProgramStageInstanceUpdateComponent;
  let fixture: ComponentFixture<ProgramStageInstanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programStageInstanceFormService: ProgramStageInstanceFormService;
  let programStageInstanceService: ProgramStageInstanceService;
  let programInstanceService: ProgramInstanceService;
  let programStageService: ProgramStageService;
  let organisationUnitService: OrganisationUnitService;
  let userService: UserService;
  let periodService: PeriodService;
  let commentService: CommentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramStageInstanceUpdateComponent],
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
      .overrideTemplate(ProgramStageInstanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramStageInstanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programStageInstanceFormService = TestBed.inject(ProgramStageInstanceFormService);
    programStageInstanceService = TestBed.inject(ProgramStageInstanceService);
    programInstanceService = TestBed.inject(ProgramInstanceService);
    programStageService = TestBed.inject(ProgramStageService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    userService = TestBed.inject(UserService);
    periodService = TestBed.inject(PeriodService);
    commentService = TestBed.inject(CommentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProgramInstance query and add missing value', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const programInstance: IProgramInstance = { id: 98448 };
      programStageInstance.programInstance = programInstance;

      const programInstanceCollection: IProgramInstance[] = [{ id: 19320 }];
      jest.spyOn(programInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: programInstanceCollection })));
      const additionalProgramInstances = [programInstance];
      const expectedCollection: IProgramInstance[] = [...additionalProgramInstances, ...programInstanceCollection];
      jest.spyOn(programInstanceService, 'addProgramInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(programInstanceService.query).toHaveBeenCalled();
      expect(programInstanceService.addProgramInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        programInstanceCollection,
        ...additionalProgramInstances.map(expect.objectContaining)
      );
      expect(comp.programInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProgramStage query and add missing value', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const programStage: IProgramStage = { id: 84206 };
      programStageInstance.programStage = programStage;

      const programStageCollection: IProgramStage[] = [{ id: 16670 }];
      jest.spyOn(programStageService, 'query').mockReturnValue(of(new HttpResponse({ body: programStageCollection })));
      const additionalProgramStages = [programStage];
      const expectedCollection: IProgramStage[] = [...additionalProgramStages, ...programStageCollection];
      jest.spyOn(programStageService, 'addProgramStageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(programStageService.query).toHaveBeenCalled();
      expect(programStageService.addProgramStageToCollectionIfMissing).toHaveBeenCalledWith(
        programStageCollection,
        ...additionalProgramStages.map(expect.objectContaining)
      );
      expect(comp.programStagesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 90092 };
      programStageInstance.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 89755 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const assignedUser: IUser = { id: 53018 };
      programStageInstance.assignedUser = assignedUser;
      const createdBy: IUser = { id: 19712 };
      programStageInstance.createdBy = createdBy;
      const updatedBy: IUser = { id: 80982 };
      programStageInstance.updatedBy = updatedBy;
      const approvedBy: IUser = { id: 2161 };
      programStageInstance.approvedBy = approvedBy;

      const userCollection: IUser[] = [{ id: 35569 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [assignedUser, createdBy, updatedBy, approvedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Period query and add missing value', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const period: IPeriod = { id: 19556 };
      programStageInstance.period = period;

      const periodCollection: IPeriod[] = [{ id: 53050 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Comment query and add missing value', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const comments: IComment[] = [{ id: 84972 }];
      programStageInstance.comments = comments;

      const commentCollection: IComment[] = [{ id: 23541 }];
      jest.spyOn(commentService, 'query').mockReturnValue(of(new HttpResponse({ body: commentCollection })));
      const additionalComments = [...comments];
      const expectedCollection: IComment[] = [...additionalComments, ...commentCollection];
      jest.spyOn(commentService, 'addCommentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(commentService.query).toHaveBeenCalled();
      expect(commentService.addCommentToCollectionIfMissing).toHaveBeenCalledWith(
        commentCollection,
        ...additionalComments.map(expect.objectContaining)
      );
      expect(comp.commentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programStageInstance: IProgramStageInstance = { id: 456 };
      const programInstance: IProgramInstance = { id: 45774 };
      programStageInstance.programInstance = programInstance;
      const programStage: IProgramStage = { id: 18120 };
      programStageInstance.programStage = programStage;
      const organisationUnit: IOrganisationUnit = { id: 77217 };
      programStageInstance.organisationUnit = organisationUnit;
      const assignedUser: IUser = { id: 94143 };
      programStageInstance.assignedUser = assignedUser;
      const createdBy: IUser = { id: 90974 };
      programStageInstance.createdBy = createdBy;
      const updatedBy: IUser = { id: 44185 };
      programStageInstance.updatedBy = updatedBy;
      const approvedBy: IUser = { id: 78625 };
      programStageInstance.approvedBy = approvedBy;
      const period: IPeriod = { id: 5279 };
      programStageInstance.period = period;
      const comment: IComment = { id: 18906 };
      programStageInstance.comments = [comment];

      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      expect(comp.programInstancesSharedCollection).toContain(programInstance);
      expect(comp.programStagesSharedCollection).toContain(programStage);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.usersSharedCollection).toContain(assignedUser);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.usersSharedCollection).toContain(approvedBy);
      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.commentsSharedCollection).toContain(comment);
      expect(comp.programStageInstance).toEqual(programStageInstance);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageInstance>>();
      const programStageInstance = { id: 123 };
      jest.spyOn(programStageInstanceFormService, 'getProgramStageInstance').mockReturnValue(programStageInstance);
      jest.spyOn(programStageInstanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStageInstance }));
      saveSubject.complete();

      // THEN
      expect(programStageInstanceFormService.getProgramStageInstance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programStageInstanceService.update).toHaveBeenCalledWith(expect.objectContaining(programStageInstance));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageInstance>>();
      const programStageInstance = { id: 123 };
      jest.spyOn(programStageInstanceFormService, 'getProgramStageInstance').mockReturnValue({ id: null });
      jest.spyOn(programStageInstanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageInstance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStageInstance }));
      saveSubject.complete();

      // THEN
      expect(programStageInstanceFormService.getProgramStageInstance).toHaveBeenCalled();
      expect(programStageInstanceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageInstance>>();
      const programStageInstance = { id: 123 };
      jest.spyOn(programStageInstanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageInstance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programStageInstanceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProgramInstance', () => {
      it('Should forward to programInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programInstanceService, 'compareProgramInstance');
        comp.compareProgramInstance(entity, entity2);
        expect(programInstanceService.compareProgramInstance).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareOrganisationUnit', () => {
      it('Should forward to organisationUnitService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationUnitService, 'compareOrganisationUnit');
        comp.compareOrganisationUnit(entity, entity2);
        expect(organisationUnitService.compareOrganisationUnit).toHaveBeenCalledWith(entity, entity2);
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

    describe('comparePeriod', () => {
      it('Should forward to periodService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(periodService, 'comparePeriod');
        comp.comparePeriod(entity, entity2);
        expect(periodService.comparePeriod).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareComment', () => {
      it('Should forward to commentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(commentService, 'compareComment');
        comp.compareComment(entity, entity2);
        expect(commentService.compareComment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
