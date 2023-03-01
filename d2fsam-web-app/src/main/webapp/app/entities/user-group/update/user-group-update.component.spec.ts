import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserGroupFormService } from './user-group-form.service';
import { UserGroupService } from '../service/user-group.service';
import { IUserGroup } from '../user-group.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { UserDataService } from 'app/entities/user-data/service/user-data.service';

import { UserGroupUpdateComponent } from './user-group-update.component';

describe('UserGroup Management Update Component', () => {
  let comp: UserGroupUpdateComponent;
  let fixture: ComponentFixture<UserGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userGroupFormService: UserGroupFormService;
  let userGroupService: UserGroupService;
  let userService: UserService;
  let userDataService: UserDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserGroupUpdateComponent],
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
      .overrideTemplate(UserGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userGroupFormService = TestBed.inject(UserGroupFormService);
    userGroupService = TestBed.inject(UserGroupService);
    userService = TestBed.inject(UserService);
    userDataService = TestBed.inject(UserDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userGroup: IUserGroup = { id: 456 };
      const createdBy: IUser = { id: 88706 };
      userGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 82437 };
      userGroup.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 1614 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userGroup });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call UserData query and add missing value', () => {
      const userGroup: IUserGroup = { id: 456 };
      const members: IUserData[] = [{ id: 10454 }];
      userGroup.members = members;

      const userDataCollection: IUserData[] = [{ id: 61061 }];
      jest.spyOn(userDataService, 'query').mockReturnValue(of(new HttpResponse({ body: userDataCollection })));
      const additionalUserData = [...members];
      const expectedCollection: IUserData[] = [...additionalUserData, ...userDataCollection];
      jest.spyOn(userDataService, 'addUserDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userGroup });
      comp.ngOnInit();

      expect(userDataService.query).toHaveBeenCalled();
      expect(userDataService.addUserDataToCollectionIfMissing).toHaveBeenCalledWith(
        userDataCollection,
        ...additionalUserData.map(expect.objectContaining)
      );
      expect(comp.userDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should call UserGroup query and add missing value', () => {
      const userGroup: IUserGroup = { id: 456 };
      const managedGroups: IUserGroup[] = [{ id: 2045 }];
      userGroup.managedGroups = managedGroups;

      const userGroupCollection: IUserGroup[] = [{ id: 56089 }];
      jest.spyOn(userGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: userGroupCollection })));
      const additionalUserGroups = [...managedGroups];
      const expectedCollection: IUserGroup[] = [...additionalUserGroups, ...userGroupCollection];
      jest.spyOn(userGroupService, 'addUserGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userGroup });
      comp.ngOnInit();

      expect(userGroupService.query).toHaveBeenCalled();
      expect(userGroupService.addUserGroupToCollectionIfMissing).toHaveBeenCalledWith(
        userGroupCollection,
        ...additionalUserGroups.map(expect.objectContaining)
      );
      expect(comp.userGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userGroup: IUserGroup = { id: 456 };
      const createdBy: IUser = { id: 34621 };
      userGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 10638 };
      userGroup.updatedBy = updatedBy;
      const member: IUserData = { id: 36900 };
      userGroup.members = [member];
      const managedGroup: IUserGroup = { id: 24100 };
      userGroup.managedGroups = [managedGroup];

      activatedRoute.data = of({ userGroup });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.userDataSharedCollection).toContain(member);
      expect(comp.userGroupsSharedCollection).toContain(managedGroup);
      expect(comp.userGroup).toEqual(userGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserGroup>>();
      const userGroup = { id: 123 };
      jest.spyOn(userGroupFormService, 'getUserGroup').mockReturnValue(userGroup);
      jest.spyOn(userGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userGroup }));
      saveSubject.complete();

      // THEN
      expect(userGroupFormService.getUserGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userGroupService.update).toHaveBeenCalledWith(expect.objectContaining(userGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserGroup>>();
      const userGroup = { id: 123 };
      jest.spyOn(userGroupFormService, 'getUserGroup').mockReturnValue({ id: null });
      jest.spyOn(userGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userGroup }));
      saveSubject.complete();

      // THEN
      expect(userGroupFormService.getUserGroup).toHaveBeenCalled();
      expect(userGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserGroup>>();
      const userGroup = { id: 123 };
      jest.spyOn(userGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userGroupService.update).toHaveBeenCalled();
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

    describe('compareUserData', () => {
      it('Should forward to userDataService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userDataService, 'compareUserData');
        comp.compareUserData(entity, entity2);
        expect(userDataService.compareUserData).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUserGroup', () => {
      it('Should forward to userGroupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userGroupService, 'compareUserGroup');
        comp.compareUserGroup(entity, entity2);
        expect(userGroupService.compareUserGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
