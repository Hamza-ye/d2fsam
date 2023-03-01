import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrganisationUnitGroupSetFormService } from './organisation-unit-group-set-form.service';
import { OrganisationUnitGroupSetService } from '../service/organisation-unit-group-set.service';
import { IOrganisationUnitGroupSet } from '../organisation-unit-group-set.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnitGroup } from 'app/entities/organisation-unit-group/organisation-unit-group.model';
import { OrganisationUnitGroupService } from 'app/entities/organisation-unit-group/service/organisation-unit-group.service';

import { OrganisationUnitGroupSetUpdateComponent } from './organisation-unit-group-set-update.component';

describe('OrganisationUnitGroupSet Management Update Component', () => {
  let comp: OrganisationUnitGroupSetUpdateComponent;
  let fixture: ComponentFixture<OrganisationUnitGroupSetUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let organisationUnitGroupSetFormService: OrganisationUnitGroupSetFormService;
  let organisationUnitGroupSetService: OrganisationUnitGroupSetService;
  let userService: UserService;
  let organisationUnitGroupService: OrganisationUnitGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrganisationUnitGroupSetUpdateComponent],
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
      .overrideTemplate(OrganisationUnitGroupSetUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrganisationUnitGroupSetUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organisationUnitGroupSetFormService = TestBed.inject(OrganisationUnitGroupSetFormService);
    organisationUnitGroupSetService = TestBed.inject(OrganisationUnitGroupSetService);
    userService = TestBed.inject(UserService);
    organisationUnitGroupService = TestBed.inject(OrganisationUnitGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const organisationUnitGroupSet: IOrganisationUnitGroupSet = { id: 456 };
      const createdBy: IUser = { id: 54724 };
      organisationUnitGroupSet.createdBy = createdBy;
      const updatedBy: IUser = { id: 91254 };
      organisationUnitGroupSet.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 15120 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnitGroupSet });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnitGroup query and add missing value', () => {
      const organisationUnitGroupSet: IOrganisationUnitGroupSet = { id: 456 };
      const organisationUnitGroups: IOrganisationUnitGroup[] = [{ id: 34735 }];
      organisationUnitGroupSet.organisationUnitGroups = organisationUnitGroups;

      const organisationUnitGroupCollection: IOrganisationUnitGroup[] = [{ id: 93950 }];
      jest.spyOn(organisationUnitGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitGroupCollection })));
      const additionalOrganisationUnitGroups = [...organisationUnitGroups];
      const expectedCollection: IOrganisationUnitGroup[] = [...additionalOrganisationUnitGroups, ...organisationUnitGroupCollection];
      jest.spyOn(organisationUnitGroupService, 'addOrganisationUnitGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnitGroupSet });
      comp.ngOnInit();

      expect(organisationUnitGroupService.query).toHaveBeenCalled();
      expect(organisationUnitGroupService.addOrganisationUnitGroupToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitGroupCollection,
        ...additionalOrganisationUnitGroups.map(expect.objectContaining)
      );
      expect(comp.organisationUnitGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const organisationUnitGroupSet: IOrganisationUnitGroupSet = { id: 456 };
      const createdBy: IUser = { id: 69309 };
      organisationUnitGroupSet.createdBy = createdBy;
      const updatedBy: IUser = { id: 43555 };
      organisationUnitGroupSet.updatedBy = updatedBy;
      const organisationUnitGroup: IOrganisationUnitGroup = { id: 51579 };
      organisationUnitGroupSet.organisationUnitGroups = [organisationUnitGroup];

      activatedRoute.data = of({ organisationUnitGroupSet });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.organisationUnitGroupsSharedCollection).toContain(organisationUnitGroup);
      expect(comp.organisationUnitGroupSet).toEqual(organisationUnitGroupSet);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitGroupSet>>();
      const organisationUnitGroupSet = { id: 123 };
      jest.spyOn(organisationUnitGroupSetFormService, 'getOrganisationUnitGroupSet').mockReturnValue(organisationUnitGroupSet);
      jest.spyOn(organisationUnitGroupSetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitGroupSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnitGroupSet }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitGroupSetFormService.getOrganisationUnitGroupSet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(organisationUnitGroupSetService.update).toHaveBeenCalledWith(expect.objectContaining(organisationUnitGroupSet));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitGroupSet>>();
      const organisationUnitGroupSet = { id: 123 };
      jest.spyOn(organisationUnitGroupSetFormService, 'getOrganisationUnitGroupSet').mockReturnValue({ id: null });
      jest.spyOn(organisationUnitGroupSetService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitGroupSet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnitGroupSet }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitGroupSetFormService.getOrganisationUnitGroupSet).toHaveBeenCalled();
      expect(organisationUnitGroupSetService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitGroupSet>>();
      const organisationUnitGroupSet = { id: 123 };
      jest.spyOn(organisationUnitGroupSetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitGroupSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organisationUnitGroupSetService.update).toHaveBeenCalled();
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

    describe('compareOrganisationUnitGroup', () => {
      it('Should forward to organisationUnitGroupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationUnitGroupService, 'compareOrganisationUnitGroup');
        comp.compareOrganisationUnitGroup(entity, entity2);
        expect(organisationUnitGroupService.compareOrganisationUnitGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
