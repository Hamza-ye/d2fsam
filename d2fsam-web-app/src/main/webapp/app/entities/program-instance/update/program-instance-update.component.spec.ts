import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramInstanceFormService } from './program-instance-form.service';
import { ProgramInstanceService } from '../service/program-instance.service';
import { IProgramInstance } from '../program-instance.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IComment } from 'app/entities/comment/comment.model';
import { CommentService } from 'app/entities/comment/service/comment.service';

import { ProgramInstanceUpdateComponent } from './program-instance-update.component';

describe('ProgramInstance Management Update Component', () => {
  let comp: ProgramInstanceUpdateComponent;
  let fixture: ComponentFixture<ProgramInstanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programInstanceFormService: ProgramInstanceFormService;
  let programInstanceService: ProgramInstanceService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;
  let programService: ProgramService;
  let organisationUnitService: OrganisationUnitService;
  let activityService: ActivityService;
  let userService: UserService;
  let commentService: CommentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramInstanceUpdateComponent],
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
      .overrideTemplate(ProgramInstanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramInstanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programInstanceFormService = TestBed.inject(ProgramInstanceFormService);
    programInstanceService = TestBed.inject(ProgramInstanceService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);
    programService = TestBed.inject(ProgramService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    activityService = TestBed.inject(ActivityService);
    userService = TestBed.inject(UserService);
    commentService = TestBed.inject(CommentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TrackedEntityInstance query and add missing value', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 38756 };
      programInstance.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 28661 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Program query and add missing value', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const program: IProgram = { id: 53303 };
      programInstance.program = program;

      const programCollection: IProgram[] = [{ id: 28359 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 83655 };
      programInstance.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 95961 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Activity query and add missing value', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const activity: IActivity = { id: 39015 };
      programInstance.activity = activity;

      const activityCollection: IActivity[] = [{ id: 25686 }];
      jest.spyOn(activityService, 'query').mockReturnValue(of(new HttpResponse({ body: activityCollection })));
      const additionalActivities = [activity];
      const expectedCollection: IActivity[] = [...additionalActivities, ...activityCollection];
      jest.spyOn(activityService, 'addActivityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(activityService.query).toHaveBeenCalled();
      expect(activityService.addActivityToCollectionIfMissing).toHaveBeenCalledWith(
        activityCollection,
        ...additionalActivities.map(expect.objectContaining)
      );
      expect(comp.activitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const createdBy: IUser = { id: 5420 };
      programInstance.createdBy = createdBy;
      const updatedBy: IUser = { id: 51573 };
      programInstance.updatedBy = updatedBy;
      const approvedBy: IUser = { id: 71647 };
      programInstance.approvedBy = approvedBy;

      const userCollection: IUser[] = [{ id: 49458 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy, approvedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Comment query and add missing value', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const comments: IComment[] = [{ id: 31018 }];
      programInstance.comments = comments;

      const commentCollection: IComment[] = [{ id: 65309 }];
      jest.spyOn(commentService, 'query').mockReturnValue(of(new HttpResponse({ body: commentCollection })));
      const additionalComments = [...comments];
      const expectedCollection: IComment[] = [...additionalComments, ...commentCollection];
      jest.spyOn(commentService, 'addCommentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(commentService.query).toHaveBeenCalled();
      expect(commentService.addCommentToCollectionIfMissing).toHaveBeenCalledWith(
        commentCollection,
        ...additionalComments.map(expect.objectContaining)
      );
      expect(comp.commentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programInstance: IProgramInstance = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 3806 };
      programInstance.entityInstance = entityInstance;
      const program: IProgram = { id: 9652 };
      programInstance.program = program;
      const organisationUnit: IOrganisationUnit = { id: 85777 };
      programInstance.organisationUnit = organisationUnit;
      const activity: IActivity = { id: 56268 };
      programInstance.activity = activity;
      const createdBy: IUser = { id: 74784 };
      programInstance.createdBy = createdBy;
      const updatedBy: IUser = { id: 15905 };
      programInstance.updatedBy = updatedBy;
      const approvedBy: IUser = { id: 23744 };
      programInstance.approvedBy = approvedBy;
      const comment: IComment = { id: 415 };
      programInstance.comments = [comment];

      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.activitiesSharedCollection).toContain(activity);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.usersSharedCollection).toContain(approvedBy);
      expect(comp.commentsSharedCollection).toContain(comment);
      expect(comp.programInstance).toEqual(programInstance);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramInstance>>();
      const programInstance = { id: 123 };
      jest.spyOn(programInstanceFormService, 'getProgramInstance').mockReturnValue(programInstance);
      jest.spyOn(programInstanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programInstance }));
      saveSubject.complete();

      // THEN
      expect(programInstanceFormService.getProgramInstance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programInstanceService.update).toHaveBeenCalledWith(expect.objectContaining(programInstance));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramInstance>>();
      const programInstance = { id: 123 };
      jest.spyOn(programInstanceFormService, 'getProgramInstance').mockReturnValue({ id: null });
      jest.spyOn(programInstanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programInstance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programInstance }));
      saveSubject.complete();

      // THEN
      expect(programInstanceFormService.getProgramInstance).toHaveBeenCalled();
      expect(programInstanceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramInstance>>();
      const programInstance = { id: 123 };
      jest.spyOn(programInstanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programInstance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programInstanceService.update).toHaveBeenCalled();
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

    describe('compareActivity', () => {
      it('Should forward to activityService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(activityService, 'compareActivity');
        comp.compareActivity(entity, entity2);
        expect(activityService.compareActivity).toHaveBeenCalledWith(entity, entity2);
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
