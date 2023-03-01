import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserDataFormService } from './user-data-form.service';
import { UserDataService } from '../service/user-data.service';
import { IUserData } from '../user-data.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IUserAuthorityGroup } from 'app/entities/user-authority-group/user-authority-group.model';
import { UserAuthorityGroupService } from 'app/entities/user-authority-group/service/user-authority-group.service';

import { UserDataUpdateComponent } from './user-data-update.component';

describe('UserData Management Update Component', () => {
  let comp: UserDataUpdateComponent;
  let fixture: ComponentFixture<UserDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userDataFormService: UserDataFormService;
  let userDataService: UserDataService;
  let userService: UserService;
  let organisationUnitService: OrganisationUnitService;
  let userAuthorityGroupService: UserAuthorityGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserDataUpdateComponent],
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
      .overrideTemplate(UserDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userDataFormService = TestBed.inject(UserDataFormService);
    userDataService = TestBed.inject(UserDataService);
    userService = TestBed.inject(UserService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    userAuthorityGroupService = TestBed.inject(UserAuthorityGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userData: IUserData = { id: 456 };
      const createdBy: IUser = { id: 92568 };
      userData.createdBy = createdBy;
      const updatedBy: IUser = { id: 39630 };
      userData.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 53371 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const userData: IUserData = { id: 456 };
      const organisationUnits: IOrganisationUnit[] = [{ id: 5959 }];
      userData.organisationUnits = organisationUnits;
      const teiSearchOrganisationUnits: IOrganisationUnit[] = [{ id: 8701 }];
      userData.teiSearchOrganisationUnits = teiSearchOrganisationUnits;
      const dataViewOrganisationUnits: IOrganisationUnit[] = [{ id: 82694 }];
      userData.dataViewOrganisationUnits = dataViewOrganisationUnits;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 68816 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [...organisationUnits, ...teiSearchOrganisationUnits, ...dataViewOrganisationUnits];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call UserAuthorityGroup query and add missing value', () => {
      const userData: IUserData = { id: 456 };
      const userAuthorityGroups: IUserAuthorityGroup[] = [{ id: 88039 }];
      userData.userAuthorityGroups = userAuthorityGroups;

      const userAuthorityGroupCollection: IUserAuthorityGroup[] = [{ id: 84371 }];
      jest.spyOn(userAuthorityGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: userAuthorityGroupCollection })));
      const additionalUserAuthorityGroups = [...userAuthorityGroups];
      const expectedCollection: IUserAuthorityGroup[] = [...additionalUserAuthorityGroups, ...userAuthorityGroupCollection];
      jest.spyOn(userAuthorityGroupService, 'addUserAuthorityGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(userAuthorityGroupService.query).toHaveBeenCalled();
      expect(userAuthorityGroupService.addUserAuthorityGroupToCollectionIfMissing).toHaveBeenCalledWith(
        userAuthorityGroupCollection,
        ...additionalUserAuthorityGroups.map(expect.objectContaining)
      );
      expect(comp.userAuthorityGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userData: IUserData = { id: 456 };
      const createdBy: IUser = { id: 26544 };
      userData.createdBy = createdBy;
      const updatedBy: IUser = { id: 83191 };
      userData.updatedBy = updatedBy;
      const organisationUnit: IOrganisationUnit = { id: 7040 };
      userData.organisationUnits = [organisationUnit];
      const teiSearchOrganisationUnit: IOrganisationUnit = { id: 14422 };
      userData.teiSearchOrganisationUnits = [teiSearchOrganisationUnit];
      const dataViewOrganisationUnit: IOrganisationUnit = { id: 69048 };
      userData.dataViewOrganisationUnits = [dataViewOrganisationUnit];
      const userAuthorityGroups: IUserAuthorityGroup = { id: 64760 };
      userData.userAuthorityGroups = [userAuthorityGroups];

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.organisationUnitsSharedCollection).toContain(teiSearchOrganisationUnit);
      expect(comp.organisationUnitsSharedCollection).toContain(dataViewOrganisationUnit);
      expect(comp.userAuthorityGroupsSharedCollection).toContain(userAuthorityGroups);
      expect(comp.userData).toEqual(userData);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserData>>();
      const userData = { id: 123 };
      jest.spyOn(userDataFormService, 'getUserData').mockReturnValue(userData);
      jest.spyOn(userDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userData }));
      saveSubject.complete();

      // THEN
      expect(userDataFormService.getUserData).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userDataService.update).toHaveBeenCalledWith(expect.objectContaining(userData));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserData>>();
      const userData = { id: 123 };
      jest.spyOn(userDataFormService, 'getUserData').mockReturnValue({ id: null });
      jest.spyOn(userDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userData: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userData }));
      saveSubject.complete();

      // THEN
      expect(userDataFormService.getUserData).toHaveBeenCalled();
      expect(userDataService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserData>>();
      const userData = { id: 123 };
      jest.spyOn(userDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userDataService.update).toHaveBeenCalled();
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

    describe('compareOrganisationUnit', () => {
      it('Should forward to organisationUnitService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationUnitService, 'compareOrganisationUnit');
        comp.compareOrganisationUnit(entity, entity2);
        expect(organisationUnitService.compareOrganisationUnit).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUserAuthorityGroup', () => {
      it('Should forward to userAuthorityGroupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userAuthorityGroupService, 'compareUserAuthorityGroup');
        comp.compareUserAuthorityGroup(entity, entity2);
        expect(userAuthorityGroupService.compareUserAuthorityGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
