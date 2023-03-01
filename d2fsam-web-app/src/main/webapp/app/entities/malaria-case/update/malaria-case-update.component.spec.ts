import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MalariaCaseFormService } from './malaria-case-form.service';
import { MalariaCaseService } from '../service/malaria-case.service';
import { IMalariaCase } from '../malaria-case.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { MalariaCaseUpdateComponent } from './malaria-case-update.component';

describe('MalariaCase Management Update Component', () => {
  let comp: MalariaCaseUpdateComponent;
  let fixture: ComponentFixture<MalariaCaseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let malariaCaseFormService: MalariaCaseFormService;
  let malariaCaseService: MalariaCaseService;
  let organisationUnitService: OrganisationUnitService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MalariaCaseUpdateComponent],
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
      .overrideTemplate(MalariaCaseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MalariaCaseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    malariaCaseFormService = TestBed.inject(MalariaCaseFormService);
    malariaCaseService = TestBed.inject(MalariaCaseService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call OrganisationUnit query and add missing value', () => {
      const malariaCase: IMalariaCase = { id: 456 };
      const subVillage: IOrganisationUnit = { id: 98206 };
      malariaCase.subVillage = subVillage;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 94928 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [subVillage];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ malariaCase });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const malariaCase: IMalariaCase = { id: 456 };
      const createdBy: IUser = { id: 47075 };
      malariaCase.createdBy = createdBy;
      const updatedBy: IUser = { id: 49022 };
      malariaCase.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 28288 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ malariaCase });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const malariaCase: IMalariaCase = { id: 456 };
      const subVillage: IOrganisationUnit = { id: 92469 };
      malariaCase.subVillage = subVillage;
      const createdBy: IUser = { id: 54838 };
      malariaCase.createdBy = createdBy;
      const updatedBy: IUser = { id: 549 };
      malariaCase.updatedBy = updatedBy;

      activatedRoute.data = of({ malariaCase });
      comp.ngOnInit();

      expect(comp.organisationUnitsSharedCollection).toContain(subVillage);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.malariaCase).toEqual(malariaCase);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMalariaCase>>();
      const malariaCase = { id: 123 };
      jest.spyOn(malariaCaseFormService, 'getMalariaCase').mockReturnValue(malariaCase);
      jest.spyOn(malariaCaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ malariaCase });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: malariaCase }));
      saveSubject.complete();

      // THEN
      expect(malariaCaseFormService.getMalariaCase).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(malariaCaseService.update).toHaveBeenCalledWith(expect.objectContaining(malariaCase));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMalariaCase>>();
      const malariaCase = { id: 123 };
      jest.spyOn(malariaCaseFormService, 'getMalariaCase').mockReturnValue({ id: null });
      jest.spyOn(malariaCaseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ malariaCase: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: malariaCase }));
      saveSubject.complete();

      // THEN
      expect(malariaCaseFormService.getMalariaCase).toHaveBeenCalled();
      expect(malariaCaseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMalariaCase>>();
      const malariaCase = { id: 123 };
      jest.spyOn(malariaCaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ malariaCase });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(malariaCaseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
  });
});
