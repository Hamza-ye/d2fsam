import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AssignmentFormService } from './assignment-form.service';
import { AssignmentService } from '../service/assignment.service';
import { IAssignment } from '../assignment.model';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { AssignmentUpdateComponent } from './assignment-update.component';

describe('Assignment Management Update Component', () => {
  let comp: AssignmentUpdateComponent;
  let fixture: ComponentFixture<AssignmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let assignmentFormService: AssignmentFormService;
  let assignmentService: AssignmentService;
  let activityService: ActivityService;
  let organisationUnitService: OrganisationUnitService;
  let teamService: TeamService;
  let periodService: PeriodService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AssignmentUpdateComponent],
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
      .overrideTemplate(AssignmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AssignmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    assignmentFormService = TestBed.inject(AssignmentFormService);
    assignmentService = TestBed.inject(AssignmentService);
    activityService = TestBed.inject(ActivityService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    teamService = TestBed.inject(TeamService);
    periodService = TestBed.inject(PeriodService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Activity query and add missing value', () => {
      const assignment: IAssignment = { id: 456 };
      const activity: IActivity = { id: 63224 };
      assignment.activity = activity;

      const activityCollection: IActivity[] = [{ id: 23629 }];
      jest.spyOn(activityService, 'query').mockReturnValue(of(new HttpResponse({ body: activityCollection })));
      const additionalActivities = [activity];
      const expectedCollection: IActivity[] = [...additionalActivities, ...activityCollection];
      jest.spyOn(activityService, 'addActivityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      expect(activityService.query).toHaveBeenCalled();
      expect(activityService.addActivityToCollectionIfMissing).toHaveBeenCalledWith(
        activityCollection,
        ...additionalActivities.map(expect.objectContaining)
      );
      expect(comp.activitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const assignment: IAssignment = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 61540 };
      assignment.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 85684 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Team query and add missing value', () => {
      const assignment: IAssignment = { id: 456 };
      const assignedTeam: ITeam = { id: 81305 };
      assignment.assignedTeam = assignedTeam;

      const teamCollection: ITeam[] = [{ id: 99548 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeams = [assignedTeam];
      const expectedCollection: ITeam[] = [...additionalTeams, ...teamCollection];
      jest.spyOn(teamService, 'addTeamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamToCollectionIfMissing).toHaveBeenCalledWith(
        teamCollection,
        ...additionalTeams.map(expect.objectContaining)
      );
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Period query and add missing value', () => {
      const assignment: IAssignment = { id: 456 };
      const period: IPeriod = { id: 67256 };
      assignment.period = period;

      const periodCollection: IPeriod[] = [{ id: 94387 }];
      jest.spyOn(periodService, 'query').mockReturnValue(of(new HttpResponse({ body: periodCollection })));
      const additionalPeriods = [period];
      const expectedCollection: IPeriod[] = [...additionalPeriods, ...periodCollection];
      jest.spyOn(periodService, 'addPeriodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      expect(periodService.query).toHaveBeenCalled();
      expect(periodService.addPeriodToCollectionIfMissing).toHaveBeenCalledWith(
        periodCollection,
        ...additionalPeriods.map(expect.objectContaining)
      );
      expect(comp.periodsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const assignment: IAssignment = { id: 456 };
      const createdBy: IUser = { id: 62135 };
      assignment.createdBy = createdBy;
      const updatedBy: IUser = { id: 55574 };
      assignment.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 93391 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const assignment: IAssignment = { id: 456 };
      const activity: IActivity = { id: 60627 };
      assignment.activity = activity;
      const organisationUnit: IOrganisationUnit = { id: 13708 };
      assignment.organisationUnit = organisationUnit;
      const assignedTeam: ITeam = { id: 39648 };
      assignment.assignedTeam = assignedTeam;
      const period: IPeriod = { id: 13200 };
      assignment.period = period;
      const createdBy: IUser = { id: 47350 };
      assignment.createdBy = createdBy;
      const updatedBy: IUser = { id: 79767 };
      assignment.updatedBy = updatedBy;

      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      expect(comp.activitiesSharedCollection).toContain(activity);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.teamsSharedCollection).toContain(assignedTeam);
      expect(comp.periodsSharedCollection).toContain(period);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.assignment).toEqual(assignment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAssignment>>();
      const assignment = { id: 123 };
      jest.spyOn(assignmentFormService, 'getAssignment').mockReturnValue(assignment);
      jest.spyOn(assignmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: assignment }));
      saveSubject.complete();

      // THEN
      expect(assignmentFormService.getAssignment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(assignmentService.update).toHaveBeenCalledWith(expect.objectContaining(assignment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAssignment>>();
      const assignment = { id: 123 };
      jest.spyOn(assignmentFormService, 'getAssignment').mockReturnValue({ id: null });
      jest.spyOn(assignmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assignment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: assignment }));
      saveSubject.complete();

      // THEN
      expect(assignmentFormService.getAssignment).toHaveBeenCalled();
      expect(assignmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAssignment>>();
      const assignment = { id: 123 };
      jest.spyOn(assignmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assignment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(assignmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareActivity', () => {
      it('Should forward to activityService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(activityService, 'compareActivity');
        comp.compareActivity(entity, entity2);
        expect(activityService.compareActivity).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareTeam', () => {
      it('Should forward to teamService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamService, 'compareTeam');
        comp.compareTeam(entity, entity2);
        expect(teamService.compareTeam).toHaveBeenCalledWith(entity, entity2);
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
