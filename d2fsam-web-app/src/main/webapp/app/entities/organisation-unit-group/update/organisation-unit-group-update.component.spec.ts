import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrganisationUnitGroupFormService } from './organisation-unit-group-form.service';
import { OrganisationUnitGroupService } from '../service/organisation-unit-group.service';
import { IOrganisationUnitGroup } from '../organisation-unit-group.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { OrganisationUnitGroupUpdateComponent } from './organisation-unit-group-update.component';

describe('OrganisationUnitGroup Management Update Component', () => {
  let comp: OrganisationUnitGroupUpdateComponent;
  let fixture: ComponentFixture<OrganisationUnitGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let organisationUnitGroupFormService: OrganisationUnitGroupFormService;
  let organisationUnitGroupService: OrganisationUnitGroupService;
  let userService: UserService;
  let organisationUnitService: OrganisationUnitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrganisationUnitGroupUpdateComponent],
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
      .overrideTemplate(OrganisationUnitGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrganisationUnitGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organisationUnitGroupFormService = TestBed.inject(OrganisationUnitGroupFormService);
    organisationUnitGroupService = TestBed.inject(OrganisationUnitGroupService);
    userService = TestBed.inject(UserService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const organisationUnitGroup: IOrganisationUnitGroup = { id: 456 };
      const createdBy: IUser = { id: 30633 };
      organisationUnitGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 40879 };
      organisationUnitGroup.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 44343 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnitGroup });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const organisationUnitGroup: IOrganisationUnitGroup = { id: 456 };
      const members: IOrganisationUnit[] = [{ id: 55028 }];
      organisationUnitGroup.members = members;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 65531 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [...members];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnitGroup });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const organisationUnitGroup: IOrganisationUnitGroup = { id: 456 };
      const createdBy: IUser = { id: 37351 };
      organisationUnitGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 83362 };
      organisationUnitGroup.updatedBy = updatedBy;
      const member: IOrganisationUnit = { id: 94922 };
      organisationUnitGroup.members = [member];

      activatedRoute.data = of({ organisationUnitGroup });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.organisationUnitsSharedCollection).toContain(member);
      expect(comp.organisationUnitGroup).toEqual(organisationUnitGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitGroup>>();
      const organisationUnitGroup = { id: 123 };
      jest.spyOn(organisationUnitGroupFormService, 'getOrganisationUnitGroup').mockReturnValue(organisationUnitGroup);
      jest.spyOn(organisationUnitGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnitGroup }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitGroupFormService.getOrganisationUnitGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(organisationUnitGroupService.update).toHaveBeenCalledWith(expect.objectContaining(organisationUnitGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitGroup>>();
      const organisationUnitGroup = { id: 123 };
      jest.spyOn(organisationUnitGroupFormService, 'getOrganisationUnitGroup').mockReturnValue({ id: null });
      jest.spyOn(organisationUnitGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnitGroup }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitGroupFormService.getOrganisationUnitGroup).toHaveBeenCalled();
      expect(organisationUnitGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitGroup>>();
      const organisationUnitGroup = { id: 123 };
      jest.spyOn(organisationUnitGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organisationUnitGroupService.update).toHaveBeenCalled();
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
  });
});
